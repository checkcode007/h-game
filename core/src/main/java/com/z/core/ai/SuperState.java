package com.z.core.ai;


import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import com.z.model.type.PosType;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.function.Predicate;

public abstract class SuperState {
    private static final Log log = LogFactory.getLog(SuperState.class);
    public static final int diffW1 = 100;
    public static final int diffHighW1 = 5;

    public static final Random RANDOM = new Random();

    protected SlotState k;
    /**
     * 每排相同的概率
     */
    protected float C1 = 0.8f;

    protected float C2 = 0.7f;

    protected float C3 = 0.5f;

    protected float C4 = 0.2f;



    protected  double roomC3 = 0.05;  // 房间输赢次数差的权重
    protected  double roomC4 = 0.0005;  // 房间输赢金额差的权重

    public SuperState(SlotState k) {
        this.k = k;
    }

    /**
     * 随机符号
     *
     * @param slots 所有牌
     * @param goals 中奖的牌
     *              RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *              高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *              低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     *

     * @return
     */
    public Slot random(CommonGame.GameType gameType, Table<Integer, Integer, SlotModel> board, Map<Integer, Slot> slots, Set<Integer> goals, BetParam param) {
        List<Slot> list = new ArrayList<>(slots.values());
        int x = param.getX();
        int continueC = param.getContinueC();
        //检测特定游戏
        list = checkGame(gameType, board, list, param.isFree());
//        print(list,"random1");
        //检测每轴的个数
        checkCount(board, list, x);
//        print(list,"random2");
        //检测位置
        checkPos(list, x);
//        print(list,"random3");
        //检测连续的次数
        checkContinue(board, list, x, continueC);
//        print(list,"random4");
        //高，中，低状态处理

        betStateFilter(board, list, param);
//        print(list,"random5");
        //检查每列
        checkCol(slots, board, list, param);
//        print(list,"random6");
        //动态修改权重
        var map = weight(slots, list, goals, param);
//        print(list,"random7");
        //高，中，低状态处理
        betStateWight(board, map, slots, param);
        //选择符号
        var slot = selectSlot(board, map, slots, list, param);

        return slot;
    }

    public void checkContinue(Table<Integer, Integer, SlotModel> board, List<Slot> list, int x, int continueC) {
        if (x < 1) return;
        if (continueC < 2) return;
        list.removeIf(e -> e.isScatter() || e.isBonus() || e.isBaida());
        if (continueC < 4) return;
        if (x == 1) {//不让符号连接的太多,最多三个连接
            Set<Integer> set1 = new HashSet<>();
            for (SlotModel m : board.row(0).values()) {
                set1.add(m.getK());
            }
            Set<Integer> set2 = new HashSet<>();
            for (SlotModel m : board.row(1).values()) {
                set2.add(m.getK());
            }
            set2.retainAll(set1);
            if (!set2.isEmpty()) {
                if (!set2.isEmpty()) {
                    list.removeIf(e -> set2.contains(e.getK()));
                }
            }
        }

    }

    void betStateFilter(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
    }

    void betStateWight(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots, BetParam param) {

    }

    //todo 水浒传从右到左处理
    public void checkCol(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        //选择符号
        int x = param.getX();
        switch (x) {
            case 0:
                col_0(slots, board, list, param);
                break;
            case 1:
                col_1(slots, board, list, param);
                break;
            case 2:
                col_2(slots, board, list, param);
                break;
            case 3:
                col_3(slots, board, list, param);
                break;
            case 4:
                col_4(slots, board, list, param);
                break;
        }
    }
    public Slot winSlot(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots, List<Slot> list, BetParam param) {
        int x = param.getX();
        int y = param.getY();
        if(x<1) return null;
        if(x>2)return null;
        if(param.getLineSet().contains(x) ) return null;
        double roomStateFactor = calculateRoomStateFactor(param.getRoomWinC(), param.getRoomTotalC(), param.getRoomBetGold(), param.getRoomWinGold());
        boolean win = calculateWinProbability(roomStateFactor);
        if(!win) return null;
        StringJoiner sj = new StringJoiner(",");
        for (Integer i : param.getLineSet()) {
            sj.add(i+"");
        }
        log.info(sj.toString());
        SlotModel m = board.get(x-1,y);
        if(m == null) return null;
        for (Slot slot : list) {
            if(slot.getK() == m.getK()){
                param.addLine(x);
                return slot;
            }
        }
        return null;
    }
    public Slot selectSlot(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots, List<Slot> list, BetParam param) {
        Slot winSlot = winSlot(board, map, slots, list, param);
        if(winSlot!=null) return winSlot;
        long winCount = param.getRoomWinC();
        long tatalC =param.getRoomTotalC();

        long loseCount  = tatalC -winCount;
        double betAmount = param.getRoomBetGold();
        double winAmount = param.getRoomWinGold();

        // 计算房间状态因子
        double roomStateFactor = calculateRoomStateFactor(winCount, loseCount, betAmount, winAmount);

        // 调整每个符号的权重（w1）
        Map<Integer, Integer> adjustedMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int symbol = entry.getKey();
            int originalWeight = entry.getValue();

            // 根据房间状态因子调整符号的权重
            int adjustedWeight = (int) (originalWeight + roomStateFactor * 10); // 例如调整权重的比例为 10
            adjustedWeight = Math.max(0, adjustedWeight); // 确保权重不为负数

            adjustedMap.put(symbol, adjustedWeight);
        }

        // 计算权重总和
        int totalWeight = 0;
        for (Integer weight : adjustedMap.values()) {
            totalWeight += weight;
        }

        // 生成一个随机数
        int random = RandomUtil.randomInt(1, totalWeight);

        // 根据随机数选择符号
        int currentWeight = 0;
        Slot target = null;
        Iterator<Map.Entry<Integer, Integer>> iterator = adjustedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int symbol = entry.getKey();
            int weight = entry.getValue();
            currentWeight += weight;

            if (random <= currentWeight) {
                target = slots.get(symbol);
                break;
            }
        }
        if (target == null) {
            int randomIndex = RandomUtil.randomInt(0, list.size());
            target = list.get(randomIndex);
        }
        return target;
    }

    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {

    }

    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushList(slots, board, list, param, C1);
    }

    public void col_2(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushList(slots, board, list, param, C2);
    }

    public void col_3(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushList(slots, board, list, param, C3);
    }

    public void col_4(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushList(slots, board, list, param, C4);
    }

    public void reflushList(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param, float probalityLimit) {
//        Set<Integer> set = new HashSet<>();
//        for (SlotModel m : board.row(param.getX() - 1).values()) {
//            set.add(m.getK());
//        }
//        double roomStateFactor = calculateRoomStateFactor(param.getRoomWinC(),param.getRoomTotalC(),param.getRoomBetGold(),param.getRoomWinGold());
//        boolean win = calculateWinProbability(roomStateFactor);
//        if (win) {
//            list.clear();
//            for (Integer k : set) {
//                list.add(slots.get(k));
//            }
//        } else {
//            interrupt(board, list, param.getX());
//        }
    }


    /**
     * 计算中奖概率，基于房间状态因子的调整
     *
     * @param roomStateFactor 房间状态影响因子
     * @return 是否中奖
     */
    private boolean calculateWinProbability(double roomStateFactor) {
        // 基础中奖概率为 10%
        double baseProbability = 0.1;

        // 如果房间状态因子为正，增加中奖概率
        double adjustedProbability = baseProbability + (roomStateFactor * 0.05); // 状态因子影响中奖概率

        // 防止概率超过1
        if (adjustedProbability > 1) {
            adjustedProbability = 1;
        }

        // 根据调整后的中奖概率计算是否中奖
        double probability = Math.random();
        return probability < adjustedProbability;
    }

    public Map<Integer, Integer> weight(Map<Integer, Slot> slots, List<Slot> list, Set<Integer> goals, BetParam param) {
        Map<Integer, Integer> map = new HashMap();
        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            // 动态调整权重变化：目标符号增加的幅度比非目标符号小
            int adjustFactor = isGoal ? diffW1 : (int) (diffW1 * 0.5); // 目标符号调整幅度小于非目标符号
            if (s.isBaida() || s.isBonus()) {
                adjustFactor = isGoal ? diffW1 * 2 : diffW1 / 2;
            } else if (s.isScatter()) {
                adjustFactor = isGoal ? diffW1 * 3 : diffW1 / 3;
            }
            if (isGoal) {
                s.subW1(adjustFactor);
            } else {
                s.addW1(adjustFactor);
            }
            map.put(s.getK(), s.getW1());
        }
        freeWeight(map, slots, param);
        return map;
    }
    /**
     * 计算房间状态影响因子，综合考虑房间的输赢数据
     *
     * @param winCount 房间内所有玩家的总赢次数
     * @param loseCount 房间内所有玩家的总输次数
     * @param betAmount 房间内所有玩家的总投注金额
     * @param winAmount 房间内所有玩家的总赢得金额
     * @return 房间状态影响因子
     */
    private double calculateRoomStateFactor(long winCount, long loseCount, double betAmount, double winAmount) {
        // 基础参数系数（可以根据实际情况调整）
        // 房间状态因子 = C1 * (loseCount - winCount) + C2 * (betAmount - winAmount)
        return roomC3 * (loseCount - winCount) + roomC4 * (betAmount - winAmount);
    }
    /**
     * 计算符号的出现概率并结合房间的输赢数据进行调整
     *
     * @param symbolWeight 符号的权重（例如，某个符号的基础权重）
     * @param winCount 房间内的玩家赢的次数
     * @param loseCount 房间内的玩家输的次数
     * @param betAmount 房间内的玩家投注金额
     * @param winAmount 房间内的玩家赢得金额
     * @return 调整后的符号出现概率
     */
    private double calculateSymbolProbability(double symbolWeight, int winCount, int loseCount, double betAmount, double winAmount) {
        // 计算房间状态影响因子（S_state）
        double roomStateFactor = calculateRoomStateFactor(winCount, loseCount, betAmount, winAmount);

        // 计算符号的最终出现概率
        return symbolWeight * roomStateFactor;
    }


    /**
     * 更新每个符号在支付线上的出现概率
     *
     * @param symbolWeights 符号权重数组（每个符号对应的基础权重）
     * @param winCount 房间内的玩家赢的次数
     * @param loseCount 房间内的玩家输的次数
     * @param betAmount 房间内的玩家投注金额
     * @param winAmount 房间内的玩家赢得金额
     * @return 符号出现概率数组
     */
    private double[] updateSymbolProbabilities(double[] symbolWeights, int winCount, int loseCount, double betAmount, double winAmount) {
        double[] adjustedProbabilities = new double[symbolWeights.length];
        for (int i = 0; i < symbolWeights.length; i++) {
            // 根据每个符号的权重和房间状态计算调整后的概率
            adjustedProbabilities[i] = calculateSymbolProbability(symbolWeights[i], winCount, loseCount, betAmount, winAmount);
        }
        return adjustedProbabilities;
    }

    public void freeWeight(Map<Integer, Integer> map, Map<Integer, Slot> slots, BetParam param) {
        int freeC = param.getFreeC();
        if (freeC < 1) return;

        if (freeC > 5) {
            for (Slot s : slots.values()) {
                int k = s.getK();
                if (s.isBonus()) {
                    if (map.containsKey(k)) {
                        map.put(k, 0);
                    }
                } else if (s.isBaida()) {
                    if (map.containsKey(k)) {
                        map.put(k, 0);
                    }
                }
            }
            return;
        }
        for (Slot s : slots.values()) {
            int k = s.getK();
            if (s.isBonus()) {
                if (map.containsKey(k)) {
                    map.put(k, s.getW1() / 2);
                }
            } else if (s.isBaida()) {
                if (map.containsKey(k)) {
                    map.put(k, s.getW1() / 2);
                }
            }
        }
    }

    public List<Slot> checkGame(CommonGame.GameType gameType, Table<Integer, Integer, SlotModel> board, List<Slot> list, boolean free) {
        if (gameType == CommonGame.GameType.JIUXIANLAWANG) {
            for (SlotModel v : board.values()) {
                if (v.getK() == CommonGame.LINE9.L9_7_VALUE) {
                    list.removeIf(slot -> slot.getK() == CommonGame.LINE9.L9_7_VALUE);
                    break;
                }
            }
        } else if (gameType == CommonGame.GameType.BAIBIAN_XIAOMALI) {
            int scatterC = 0;
            for (SlotModel v : board.values()) {
                if (v.isScatter()) {
                    scatterC++;
                }
            }
            if (scatterC > 1) {
                list.removeIf(slot -> slot.isScatter());
            }
        } else if (gameType == CommonGame.GameType.SHAOLIN_ZUQIU) {
            //免费处理
            if (free) {
                int randomType = RandomUtil.randomInt(CommonGame.FOOTBALL.FT_MONK1_VALUE, CommonGame.FOOTBALL.FT_MONK5_VALUE + 1);
                list.removeIf(slot -> slot.getK() != randomType);
            }
        }
        return list;
    }

    /**
     * 元素坐标检查
     * @param list
     * @param x
     * @return
     */
    public List<Slot> checkPos(List<Slot> list, int x) {
        Predicate<Slot> filter = new Predicate<Slot>() {
            @Override
            public boolean test(Slot slot) {
                PosType posType = slot.getPosType();
                if (PosType.ALL == posType) {
                    return false;
                }
                if (PosType.Y == posType) {
                    if (!slot.containsPos(x)) {
                        return true;
                    }
                } else if (PosType.N == posType) {
                    if (slot.containsPos(x)) {
                        return true;
                    }
                }
                return false;
            }
        };
        list.removeIf(filter);
        return list;
    }

    /**
     * 检查每轴的个数
     *
     * @param list
     * @param x
     * @return
     */
    public List<Slot> checkCount(Table<Integer, Integer, SlotModel> board, List<Slot> list, int x) {
        Set<Integer> onlySet = new HashSet<>(list.size());
        for (Slot slot : list) {
            if (slot.isOnly()) {
                onlySet.add(slot.getK());
            }
        }
        if (onlySet.isEmpty()) return list;
        Set<Integer> delSet = new HashSet<>(list.size());
        for (SlotModel m : board.row(x).values()) {
            if (onlySet.contains(m.getK())) {
                delSet.add(m.getK());
            }
        }
        if (delSet.isEmpty()) return list;
        list.removeIf(e -> delSet.contains(e.getK()));
        return list;
    }



    /**
     * 高级玩法随机符号
     *
     * @param slots 所有牌
     * @param goals 中奖的牌
     *              RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *              高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *              低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     * @return
     */
    public Slot randomHigher(CommonGame.GameType gameType, Map<Integer, Slot> slots, Collection<Integer> goals, BetParam param) {
        int x = param.getX();
        List<Slot> list = new ArrayList<>(slots.values());
        if (x != 0) { //炸弹只留在外层循环
            list.removeIf(slot -> slot.isQuit());
        }
        Map<Integer, Integer> map = new HashMap<>();

        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            if (isGoal) {
                s.subW1(diffHighW1);
            } else {
                s.addW2(diffHighW1);
            }
            map.put(s.getK(), s.getW1());
        }

        int totalW1 = 0;
        for (Integer v : map.values()) {
            totalW1 += v;
        }
        // 生成一个随机数
        int randomW1 = RandomUtil.randomInt(1, totalW1);
        // 根据随机数选择元素
        int curW1 = 0;
        Slot target = null;
        for (Slot s : list) {
            curW1 += s.getW1();
            if (randomW1 < curW1) {
                target = s;
                break;
            }
        }
        if (target == null) {
            int randomIndex = RandomUtil.randomInt(0, list.size());
            target = list.get(randomIndex);
        }
        return target;
    }

    public void interrupt(Table<Integer, Integer, SlotModel> board, List<Slot> list, int index) {
        Set<Integer> lastSet = new HashSet<>();
        for (int i = 0; i < index; i++) {
            Set<Integer> set = new HashSet<>();
            for (SlotModel m : board.row(i).values()) {
                set.add(m.getK());
            }
            if (i == 0) {
                lastSet.addAll(set);
            } else {
                lastSet.retainAll(set);
            }
        }
        list.removeIf(slot -> lastSet.contains(slot.getK()));
    }


    public void print(List<Slot> list, String action) {
        StringJoiner sj = new StringJoiner(",");
        for (Slot slot : list) {
            sj.add(slot.getK() + "");
        }
        log.info(action + "--->" + sj);
    }


}

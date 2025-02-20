package com.z.core.ai;


import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.core.ai.clear.ClearLowState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import com.z.model.type.LineType;
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

    protected SlotState k;
    /**
     * 每排相同的概率
     */
    protected float C1 = 0.8f;

    protected float C2 = 0.7f;

    protected float C3 = 0.5f;

    protected float C4 = 0.2f;



    protected  double roomC3 = 0.05;  // 房间输赢次数差的权重
    protected  double roomC4 = 0.00001;  // 房间输赢金额差的权重

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
        //检测每轴的个数
        checkCount(board, list, x);
        //检测位置
        checkPos(list, x);
        //检测连续的次数
        checkContinue(board, list, x, continueC);
        //高，中，低状态处理

        betStateFilter(board, list, param);
        //检查每列
        checkCol(slots, board, list, param);
        //动态修改权重
        var map = weight(slots, list, goals, param);
        //高，中，低状态处理
        betStateWight(board, map, slots, param);
        //选择符号
        var slot = selectSlot(board, map, slots, list, param);

        return slot;
    }

    public void checkContinue(Table<Integer, Integer, SlotModel> board, List<Slot> list, int x, int continueC) {
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
                print(list,"col0-->1");
                col_0(slots, board, list, param);
                print(list,"col0-->2");
                break;
            case 1:
                print(list,"col1-->1");
                col_1(slots, board, list, param);
                print(list,"col1-->2");
                break;
            case 2:
                print(list,"col2-->1");
                col_2(slots, board, list, param);
                print(list,"col2-->2");
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
//        reflushCol(slots, board, list, param, C1);
    }

    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushCol(slots, board, list, param, C1);
    }

    public void col_2(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushCol(slots, board, list, param, C2);
    }

    public void col_3(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushCol(slots, board, list, param, C3);
    }

    public void col_4(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        reflushCol(slots, board, list, param, C4);
    }

    public void reflushCol(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param, float probalityLimit) {
        double roomStateFactor = calculateRoomStateFactor(param.getRoomWinC(),param.getRoomTotalC(),param.getRoomBetGold(),param.getRoomWinGold());
        if (!calculateWinProbability(roomStateFactor,probalityLimit)) {
            log.info("roomStateFactor--->"+roomStateFactor+" probalityLimit--->"+probalityLimit);
            long loss = param.getTotalC()-param.getWinC();
            if(loss%10!=0){
                interrupt(board, list, param.getX());
            }
        }
    }

    /**
     * 计算房间状态影响因子，综合考虑房间的输赢数据
     *
     * @param winCount 房间内所有玩家的总赢次数
     * @param totalC 房间内所有玩家的总次数
     * @param betAmount 房间内所有玩家的总投注金额
     * @param winAmount 房间内所有玩家的总赢得金额
     * @return 房间状态影响因子
     */
    protected   double calculateRoomStateFactor(long winCount, long totalC, double betAmount, double winAmount) {
        // 基础参数系数（可以根据实际情况调整）
        // 房间状态因子 = C1 * (loseCount - winCount) + C2 * (betAmount - winAmount)
        long loseCount= totalC -winCount;
       long radio1 =(loseCount - winCount);
       double radio2 =(betAmount - winAmount);
        return roomC3 *radio1 + roomC4 * radio2;
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
     * 计算中奖概率，基于房间状态因子的调整
     *
     * @param roomStateFactor 房间状态影响因子
     * @param limit 当前随机生成的中奖概率阈值（0~1之间）
     * @return 是否中奖
     */
    protected   boolean calculateWinProbability(double roomStateFactor, float limit) {
        // 基础中奖概率为 10%
        double baseProbability = 0.1;

        // 状态因子对中奖概率的影响
        double adjustedProbability = baseProbability + (roomStateFactor * 0.05); // 状态因子影响中奖概率

        // 限制中奖概率在 [0, 1] 之间
        adjustedProbability = Math.max(0, Math.min(adjustedProbability, 1));

        // 进行中奖判断
        return adjustedProbability >= limit;
    }

    private boolean calculateWinProbability(double roomStateFactor) {
        // 基础中奖概率为 10%
        double baseProbability = 0.1;

        // 如果房间状态因子为正，增加中奖概率
        double adjustedProbability = baseProbability + (roomStateFactor * 0.05); // 状态因子影响中奖概率

        // 防止概率超过1
        if (adjustedProbability > 1) {
            adjustedProbability = 1;
        }
        return RandomUtil.randomDouble() <adjustedProbability;
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
                list.removeIf(Slot::isScatter);
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

    /**
     * 打断相连
     * @param board
     * @param list
     * @param index
     */
    public void interrupt(Table<Integer, Integer, SlotModel> board, List<Slot> list, int index) {

    }

    public void print(List<Slot> list, String action) {
        StringJoiner sj = new StringJoiner(",");
        for (Slot slot : list) {
            sj.add(slot.getK() + "");
        }
        log.info(action + "--->" + sj);
    }
    /**
     * 冰球突破
     * 大wild处理
     */
    public int bigWild(BetParam param){
        return 0;
    }

    public List<Rewardline> getRandomline(Map<LineType,List<Rewardline>> lineMap, BetParam param) {
        return null;
    }
    public static void main(String[] args) {
        SuperState superState = new ClearLowState(null);
        int winCount = 228;
        int totaC = 578;
        double betAmount = 1787200;
        double winAmount = 135505810;
        double f1 = superState.calculateRoomStateFactor(winCount,totaC,betAmount,winAmount);
        System.err.println(f1);

        System.err.println(superState.calculateWinProbability(f1,superState.C1));
        System.err.println(superState.calculateWinProbability(f1,superState.C2));
    }
}

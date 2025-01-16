package com.z.core.ai;


import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import com.z.model.type.SlotState;
import com.z.model.type.PosType;
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

    public SuperState(SlotState k) {
        this.k = k;
    }

    /**
     * 随机符号
     *
     * @param slots     所有牌
     * @param goals     中奖的牌
     *                  RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *                  高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *                  低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     * @return
     */
    public Slot random(CommonGame.GameType gameType, Table<Integer, Integer, SlotModel> board, Map<Integer, Slot> slots, Set<Integer> goals, BetParam param) {
        List<Slot> list = new ArrayList<>(slots.values());
        int x = param.getX();
        int continueC =param.getContinueC();
        //检测特定游戏
        list = checkGame(gameType, board, list, param.isFree());
//        print(list,"random1");
        //检测每轴的个数
        checkCount(board, list, x);
        print(list,"random2");
        //检测位置
        checkPos(list, x);
        print(list,"random3");
        //检测连续的次数
        checkContinue(board, list, x, continueC);
        print(list,"random4");
        //高，中，低状态处理

        betStateFilter(board, list,param);
        print(list,"random5");
        //检查每列
        checkCol(board,list,param);
        print(list,"random6");
        //动态修改权重
        var map = weight(slots, list, goals, param);
        print(list,"random7");
        //高，中，低状态处理
        betStateWight(board, map, slots, param);
        //选择符号
        var slot = selectSlot(board,map, slots, list,param);

        return slot;
    }

    public void checkContinue(Table<Integer, Integer, SlotModel> board, List<Slot> list, int x,int continueC) {
        if(x<1) return;
        if(continueC<2) return;
        list.removeIf(e -> e.isScatter() || e.isBonus() || e.isBaida());
        if (continueC<4)return;
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

    void betStateFilter(Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {
    }

    void betStateWight(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots, BetParam param){

    }
//todo 水浒传从右到左处理
    public void checkCol(Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {
        //选择符号
        int x = param.getX();
        switch (x) {
            case 0:
                print(list, "checkCol01");
                col_0(board, list, param);
                print(list, "checkCol02");
                break;
            case 1:
                print(list, "checkCol11");
                col_1(board, list, param);
                print(list, "checkCol12");
                break;
            case 2:
                print(list, "checkCol21");
                col_2(board, list, param);
                print(list, "checkCol22");
                break;
            case 3:
                print(list, "checkCol31");
                col_3(board, list, param);
                print(list, "checkCol32");
                break;
            case 4:
                print(list, "checkCol41");
                col_4(board, list, param);
                print(list, "checkCol42");
                break;
        }
    }
    public Slot selectSlot(Table<Integer, Integer, SlotModel> board,Map<Integer, Integer> map, Map<Integer, Slot> slots, List<Slot> list,BetParam param) {
        //选择符号
        int totalW1 = 0;
        for (Integer v : map.values()) {
            totalW1 += v;
        }
        // 生成一个随机数
        int randomW1 = RandomUtil.randomInt(1, totalW1);
        // 根据随机数选择元素
        int curW1 = 0;
        Slot target = null;
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int k = entry.getKey();
            int w1 = entry.getValue();
            curW1 += w1;
            if (randomW1 < curW1) {
                target = slots.get(k);
                break;
            }
        }
        if (target == null) {
            int randomIndex = RandomUtil.randomInt(0, list.size());
            target = list.get(randomIndex);
        }
        return target;
    }
    public void col_0( Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {

    }
    public void col_1( Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {
    }
    public void col_2( Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {

    }
    public void col_3( Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {
    }

    public void col_4( Table<Integer, Integer, SlotModel> board, List<Slot> list,BetParam param) {
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
        freeWeight(map,slots, param);
        return map;
    }

    public void freeWeight(Map<Integer, Integer> map, Map<Integer, Slot> slots,BetParam param) {
        int freeC = param.getFreeC();
        if(freeC<1) return;

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
     *
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
    public void interrupt(Table<Integer, Integer, SlotModel> board ,List<Slot> list,int index){
        Set<Integer> lastSet = new HashSet<>();
        for (int i = 0; i < index; i++) {
            Set<Integer> set = new HashSet<>();
            for (SlotModel m : board.row(i).values()) {
                set.add(m.getK());
            }
            if(i == 0){
                lastSet.addAll(set);
            }else{
                lastSet.retainAll(set);
            }
        }
        StringJoiner sj = new StringJoiner(",");
        for (Integer i : lastSet) {
            sj.add(i+"");
        }
        log.info("last-->"+sj);
        print(list,"interrupt1-->"+index);
        list.removeIf(slot -> lastSet.contains(slot.getK()));
        print(list,"interrupt2-->"+index);
    }


    public void print(List<Slot> list,String action){
        StringJoiner sj = new StringJoiner(",");
        for (Slot slot : list) {
            sj.add(slot.getK()+"");
        }
        log.info(action+"--->"+sj);
    }


}

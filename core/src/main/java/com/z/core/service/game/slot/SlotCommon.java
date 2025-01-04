package com.z.core.service.game.slot;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.User;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.type.PosType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

/**
 * 百变玛丽
 * 通用变量
 */
public enum SlotCommon {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());

    public static final int W1_MAX = 1000000, W2_MAX = 10000;
    public static final int diffW1 = 100, diffW2 = 10;

    public static final int diffHighW1 = 5;

    public static final int BASE = 9;

    SlotCommon() {
        init();
    }

    public void init() {
    }

    /**
     * 随机符号
     *
     * @param slots    所有牌
     * @param goals    中奖的牌
     * @param x        第几排
     *                 RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *                 高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *                 低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     * @return
     */
    public Slot random(CommonGame.GameType gameType, Table<Integer,Integer,SlotModel> board, Map<Integer, Slot> slots, Set<Integer> goals,
                       int x,boolean free, User user) {
        List<Slot> list = new ArrayList<>(slots.values());


        list = checkGame(gameType,board,list,free);
        checkCount(board,list,x);
        checkPos(list,x);
        Map<Integer, Integer> map = new HashMap();
        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            // 动态调整权重变化：目标符号增加的幅度比非目标符号小
            int adjustFactor = isGoal ? diffW1 : (int) (diffW1 * 0.2); // 目标符号调整幅度小于非目标符号
            if (s.getK() == CommonGame.MJ.BAIDA.getNumber() || s.getK() == CommonGame.MJ.HU.getNumber()) {
                adjustFactor = isGoal ? diffW1 * 5 : diffW1 / 5;
            }
            if (isGoal) {
                s.subW1(adjustFactor);
            } else {
                s.addW1(adjustFactor);
            }
            map.put(s.getK(), s.getW1());
        }
        map = freeWeight( map,user,slots, x);
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
    public  Map<Integer, Integer> freeWeight( Map<Integer, Integer> map,User user,Map<Integer, Slot> slots,int x){
        if(user!=null) {
            if (user.getFree() > 0) {
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
            } else if (user.getFree() > 5) {
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
            }
        }
        return map;
    }
    public List<Slot> checkGame(CommonGame.GameType gameType, Table<Integer,Integer,SlotModel> board, List<Slot> list,boolean free){
        if(gameType == CommonGame.GameType.JIUXIANLAWANG){
            for (SlotModel v : board.values()) {
                if(v.getType() == CommonGame.LINE9.L9_7_VALUE){
                    list.removeIf(slot -> slot.getK() == CommonGame.LINE9.L9_7_VALUE);
                    break;
                }
            }
        } else if (gameType == CommonGame.GameType.BAIBIAN_XIAOMALI) {
            int scatterC = 0;
            for (SlotModel v : board.values()) {
                if(v.isScatter()){
                    scatterC++;
                }
            }
            if (scatterC > 1) {
                list.removeIf(slot -> slot.isScatter());
            }
        }else if (gameType == CommonGame.GameType.SHAOLIN_ZUQIU) {
            //免费处理
            if(free){
                int randomType = RandomUtil.randomInt(CommonGame.FOOTBALL.FT_MONK1_VALUE,CommonGame.FOOTBALL.FT_MONK5_VALUE+1);
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
    public List<Slot> checkPos(List<Slot> list,int x){
        Predicate<Slot> filter = new Predicate<Slot>() {
            @Override
            public boolean test(Slot slot) {
                PosType posType = slot.getPosType();
                if(PosType.ALL == posType){
                    return false;
                }
                if (PosType.Y == posType){
                    if(!slot.containsPos(x)){
                        return true;
                    }
                } else if (PosType.N == posType) {
                    if(slot.containsPos(x)){
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
    public List<Slot> checkCount(Table<Integer,Integer,SlotModel> board,List<Slot> list,int x){
        Set<Integer> onlySet = new HashSet<>(list.size());
        for (Slot slot : list) {
            if(slot.isOnly()){
                onlySet.add(slot.getK());
            }
        }
        if(onlySet.isEmpty()) return list;
        Set<Integer> delSet = new HashSet<>(list.size());
        for (SlotModel m : board.row(x).values()) {
            if(onlySet.contains(m.getType())){
               delSet.add(m.getType());
            }
        }
        if(delSet.isEmpty()) return list;
        list.removeIf(e->delSet.contains(e.getK()));
        return list;
    }
    /**
     * 高级玩法随机符号
     *
     * @param slots    所有牌
     * @param goals    中奖的牌
     * @param x        第几排
     *                 RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *                 高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *                 低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     * @return
     */
    public Slot randomHigher(CommonGame.GameType gameType,Map<Integer, Slot> slots, Collection<Integer> goals, int x,int winC,int totalC) {
        List<Slot> list = new ArrayList<>(slots.values());
        if(x != 0){ //炸弹只留在外层循环
            if(gameType == CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER){
                list.removeIf(slot -> slot.getK() == CommonGame.MaliHigher.H_DINAMITE_VALUE);
            } else if (gameType == CommonGame.GameType.SHUIHUZHUAN_HIGHER) {
                list.removeIf(slot -> slot.getK() == CommonGame.WMHigher.WH_EXIT_VALUE);
            }
        }
        Map<Integer,Integer> map = new HashMap<>();

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
        if(x == 0){
            if(winC>5 || totalC>10){
                if(totalC%5 ==0){
                    map.forEach((k,v)->{
                        if(k!=CommonGame.MaliHigher.H_DINAMITE_VALUE){
                            map.put(k,v/2);
                        }
                    });
                }
            } else if (winC>10 || totalC>15) {
                if(totalC%3 ==0){
                    map.forEach((k,v)->{
                        if(k!=CommonGame.MaliHigher.H_DINAMITE_VALUE){
                            map.put(k,0);
                        }
                    });
                }

            }
        }
        log.info("winC:"+winC+" totalC:"+totalC);
        map.forEach( (k,v)->{

            log.info(k+"----->"+v);
        });
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

    public void print(Table<Integer, Integer, SlotModel> board, CommonGame.GameType gameType, CommonGame.RoomType roomType,long roomId,long uid) {
        board.rowKeySet().forEach(x -> {
            StringJoiner sj = new StringJoiner(" ").add("gameType:"+gameType).add("roomType:"+roomType).add("roomId:"+roomId).add("uid:"+uid);
            board.row(x).forEach((y, m) -> {
                sj.add(m.getType() + "x" + x + "y" + y+ "t:"+m.getChangeType()+"g:"+m.isGold());
            });
            log.info(sj.toString());
        });
    }

    public List<Game.Spot> allToModelTable(Table<Integer, Integer, SlotModel> board) {
        List<Game.Spot> list = new ArrayList<>();
        board.values().forEach(e -> {
            if (e != null) {
                list.add(Game.Spot.newBuilder().setSymbol(e.getType())
                        .setX(e.getX()).setY(e.getY())
                        .setGold(e.isGold()).setChangeType(e.getChangeType()).build());
            }
        });
        return list;
    }

    public SlotModel toModel(Slot s,int x,int y) {
       return SlotModel.builder().type(s.getK()).x(x).y(y).gold(s.isGold()).baida(s.isBaida())
                .bonus(s.isBonus()).only(s.isOnly()).scatter(s.isScatter()).quit(s.isQuit()).build();
    }

}

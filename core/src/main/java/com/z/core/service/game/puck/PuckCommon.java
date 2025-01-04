package com.z.core.service.game.puck;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 麻将
 * 通用变量
 */
public enum PuckCommon {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 连续中奖的区间（最小值，最大值）
     */
    public static final int RWD_C_MIN = 1, RWD_C_MAX = 6;

    public static final int diffW1 = 10, diffW2 = 10;

    public static final int W1_MAX = 1000000, W2_MAX = 10000;

    List<CommonGame.MJ> symbols = new ArrayList();

    PuckCommon() {
        init();
    }

    public void init() {
        initSymbol();
    }

    public void initSymbol() {
        symbols.clear();
        for (CommonGame.MJ value : CommonGame.MJ.values()) {
            if (value == CommonGame.MJ.UNRECOGNIZED) continue;
            if (value.getNumber() < 1) continue;
            symbols.add(value);
        }
    }

    /**
     * 随机符号
     *
     * @return
     */
    public CommonGame.MJ random(List<CommonGame.MJ> list) {
        Collections.shuffle(list);
        return list.get(0);
    }

    public CommonGame.MJ random(List<CommonGame.MJ> list, Set<CommonGame.MJ> excludes) {
        List<CommonGame.MJ> list1 = new ArrayList<>(list);
        list1.removeAll(excludes);
        Collections.shuffle(list1);
        return list1.get(0);
    }

    /**
     * 随机符号
     *
     * @param slots 所有牌
     * @param goals 中奖的牌
     * @param x     第几排
     *              RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *              高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *              低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     *
     *              //每天的收益
     *
     * @return
     */
    public Slot random(Table<Integer, Integer, SlotModel> board, Map<Integer, Slot> slots, Set<Integer> goals, int x, boolean hu, boolean free,int winC,int lianxuC) {
        List<Slot> list = new ArrayList<>(slots.values());
        boolean isAbleGold = x != 0 && x != 4;//2,3,4排才能显示金色
        // 使用 ListIterator 倒序遍历
        Iterator<Slot> iter = list.iterator();
        while (iter.hasNext()) {
            Slot slot = iter.next();
            boolean del = false;
            if (x == 0) {//第一列不显示百搭
                if (slot.getK() == CommonGame.MJ.BAIDA.getNumber()) {
                    del = true;
                }
            }
            if (slot.getK() == CommonGame.MJ.HU.getNumber()) {
                if (hu || free) {
                    del = true;
                }
            }
            if (del) {
                iter.remove();
            }
        }

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
        if(winC%7 < 2 ){
            if(x == 1){
                for (SlotModel m : board.row(0).values()) {
                    if(m.getType() != CommonGame.MJ.HU.getNumber()){
                        map.put(m.getType(),slots.get(m.getType()).getW1()/10);
                    }
                }
            } else if (x == 2) {
                for (SlotModel m : board.row(1).values()) {
                    if(m.getType() != CommonGame.MJ.HU.getNumber()){
                        map.put(m.getType(),slots.get(m.getType()).getW1()/10);
                    }
                }
            }
        }
        if(lianxuC>8){
            if(x == 1){
                for (SlotModel m : board.row(0).values()) {
                    if(m.getType() != CommonGame.MJ.HU.getNumber()){
                        map.put(m.getType(),0);
                    }
                }
            } else if (x == 2) {
                for (SlotModel m : board.row(1).values()) {
                    if(m.getType() != CommonGame.MJ.HU.getNumber()){
                        map.put(m.getType(),10);
                    }
                }
            }
        }else if(lianxuC%3==0){
            if(x == 1){
                for (SlotModel m : board.row(0).values()) {
                    if(m.getType() != CommonGame.MJ.HU.getNumber()){
                        map.put(m.getType(),100);
                    }
                }
            } else if (x == 2) {
                for (SlotModel m : board.row(1).values()) {
                    if(m.getType() != CommonGame.MJ.HU.getNumber()){
                        map.put(m.getType(),300);
                    }
                }
            }
        }

        int totalW1 = 0;
        for (Integer v : map.values()) {
            totalW1 += v;
        }
        // 生成一个随机数
        int randomW1 = RandomUtil.randomInt(1, totalW1);
        int randomW2 = RandomUtil.randomInt(0, W2_MAX * 2);
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
        //HU 或者百搭 不做金色牌处理
        boolean b_gold = false;
        if (target.getK() != CommonGame.MJ.HU.getNumber() && target.getK() != CommonGame.MJ.BAIDA.getNumber()) {
            if (isAbleGold) {
                if (target.getW2() > randomW2) {
                    b_gold = true;
                    target.subW2(diffW2);
                } else {
                    target.addW2(diffW2);
                }
            }
        }
        target.setGold(b_gold);
        target.addC1();
        return target;
    }



    /**
     * 使用加权随机数选择符号
     */
    private Slot selectRandomSlot(List<Slot> list, int totalW1) {
        // 通过权重选择符号
        int randomW1 = RandomUtil.randomInt(1, totalW1);
        int curW1 = 0;
        for (Slot s : list) {
            curW1 += s.getW1();
            if (randomW1 < curW1) {
                return s;
            }
        }
        // 默认返回最后一个符号
        return list.get(list.size() - 1);
    }

    public List<Game.MjModel> allToModel(List<List<SlotModel>> board) {
        List<Game.MjModel> list = new ArrayList<>();
        for (List<SlotModel> row : board) {
            for (SlotModel e : row) {
                list.add(Game.MjModel.newBuilder().setType(CommonGame.MJ.forNumber(e.getType())).setX(e.getX())
                        .setY(e.getY()).setGold(e.isGold()).build());
            }
        }
        return list;
    }

    public List<Game.MjModel> allToModelTable(Table<Integer, Integer, SlotModel> board) {
        List<Game.MjModel> list = new ArrayList<>();
        board.values().forEach(e -> {
            if (e != null) {
                list.add(Game.MjModel.newBuilder().setType(CommonGame.MJ.forNumber(e.getType())).setX(e.getX())
                        .setY(e.getY()).setGold(e.isGold()).build());
            }
        });
        return list;
    }

    public void print(List<List<SlotModel>> board) {
        for (List<SlotModel> row : board) {
            StringJoiner sj = new StringJoiner(" ");
            for (SlotModel tile : row) {
                sj.add(tile.getType() + "x" + tile.getX() + "y" + tile.getY());
            }
            log.info(sj.toString());
        }
    }

    public void printTable(Table<Integer, Integer, SlotModel> board) {

        for (int x = 0; x < 5; x++) {
            int size = x == 0 || x == 4 ? 4 : 5;
            StringJoiner sj = new StringJoiner(" ");
            for (int y = 0; y < size; y++) {
                SlotModel m = board.get(x, y);
                if (m == null) {
                    sj.add("nullx" + x + "y" + y);
                } else {
                    sj.add(m.getType() + "x" + x + "y" + y + "g:" + (m.isGold() ? "t" : "f"));
                }
            }
            log.info(sj.toString());
        }
    }

}

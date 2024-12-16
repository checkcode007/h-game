package com.z.core.service.game.majiang;

import cn.hutool.core.util.RandomUtil;
import com.z.model.bo.Slot;
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
public enum MJCommon {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 连续中奖的区间（最小值，最大值）
     */
    public static final int RWD_C_MIN = 1, RWD_C_MAX = 6;

    public static final int diffW1 = 100, diffW2 = 10;

    public static final int W1_MAX = 1000000, W2_MAX = 10000;

    List<CommonGame.MJ> symbols = new ArrayList();

    MJCommon() {
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
     * @param slots    所有牌
     * @param excludes 排除的牌
     * @param goals    中奖的牌
     * @param x        第几排
     *                 RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *                 高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *                 低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     * @return
     */
    public Slot random(Map<Integer, Slot> slots, Set<CommonGame.MJ> excludes, Set<Integer> goals, int x, boolean hu) {
        List<Slot> list = new ArrayList<>(slots.values());
        if (excludes != null) {
            list.removeIf(slot -> excludes.contains(CommonGame.MJ.forNumber(slot.getK())));
        }
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
            if (hu && slot.getK() == CommonGame.MJ.HU.getNumber()) {
                del = true;
            }
            if (del) {
                iter.remove();
            }
        }

        int totalW1 = 0;
        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            if (isGoal) {
                s.subW1(diffW1);
            } else {
                s.addW2(diffW1);
            }
            if (isAbleGold) {
                if (isGoal && s.isGold()) {
                    s.subW2(diffW2);
                } else {
                    s.addW2(diffW2);
                }
            }
            totalW1 += s.getW1();
        }

        // 生成一个随机数
        int randomW1 = RandomUtil.randomInt(1, totalW1);
        int randomW2 = RandomUtil.randomInt(1, W2_MAX * 2);
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
        //HU 或者百搭 不做金色牌处理
        if (target.getK() != CommonGame.MJ.HU.getNumber() && target.getK() != CommonGame.MJ.BAIDA.getNumber()) {
            if (isAbleGold) {
                if (target.getW2() > randomW2) {
                    target.setGold(true);
                    target.subW2(diffW2);
                } else {
                    target.addW2(diffW2);
                }
            }
        }
        return target;
    }


    public List<Game.MjModel> allToModel(List<List<SlotModel<CommonGame.MJ>>> board) {
        List<Game.MjModel> list = new ArrayList<>();
        for (List<SlotModel<CommonGame.MJ>> row : board) {
            for (SlotModel<CommonGame.MJ> e : row) {
                list.add(Game.MjModel.newBuilder().setType(e.getType()).setX(e.getX())
                        .setY(e.getY()).setGold(e.isGold()).build());
            }
        }
        return list;
    }

    public void print(List<List<SlotModel<CommonGame.MJ>>> board) {
        for (List<SlotModel<CommonGame.MJ>> row : board) {
            StringJoiner sj = new StringJoiner(" ");
            for (SlotModel<CommonGame.MJ> tile : row) {
                sj.add(tile.getType().getNumber() + "x" + tile.getX() + "y" + tile.getY());
            }
            log.info(sj.toString());
        }
    }
}

package com.z.core.service.game.wm;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 百变玛丽
 * 通用变量
 */
public enum WMCommon {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());
    public static final int W1_MAX = 1000000, W2_MAX = 10000;
    public static final int diffW1 = 100, diffW2 = 10;
    public static final int BASE = 9;
    /**
     * 轮子个数
     */
    public static final int REEL_SIZE = 5;
    /**
     * 每个轮子显示个数
     */
    public static final int SYMBOL_SIZE = 3;
    /**
     * 小玛丽高级玩法 3 个20倍
     */
    public static final int HIGH_3_RATE = 20;
    /**
     * 小玛丽高级玩法 4 个500倍
     */
    public static final int HIGH_4_RATE = 500;

    WMCommon() {
        init();
    }

    public void init() {
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
    public Slot random(Map<Integer, Slot> slots, Set<CommonGame.Mali> excludes, Set<Integer> goals, int x) {
        List<Slot> list = new ArrayList<>(slots.values());
        if (excludes != null) {
            list.removeIf(slot -> excludes.contains(CommonGame.MJ.forNumber(slot.getK())));
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
            totalW1 += s.getW1();
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

    public void print(Table<Integer, Integer, SlotModel> board) {
        board.rowKeySet().forEach(x -> {
            StringJoiner sj = new StringJoiner(" ");
            board.row(x).forEach((y, m) -> {
                sj.add(m.getType() + "x" + x + "y" + y);
            });
            log.info(sj.toString());
        });
    }

}

package com.z.common.game;

import java.util.Random;

/**
 * 鱼
 * 通用变量
 */
public enum FishCommon {
    ins;
    // 随机数生成器
    private final Random RANDOM = new Random();

    /**
     * 计算捕获某种鱼的最终概率
     * @param fish 鱼的类型
     * @param bullet 炮弹的类型
     * @return 最终捕获概率
     */
    public double calculateCatchProbability(int fish, int bullet) {
        // 获取鱼的基础概率
        Double fishProbability = fish*1d/10000;
        // 获取炮弹的概率加成
        Double bulletModifier = bullet*1d/10000;
        // 计算最终捕获概率
        return fishProbability * (1 + bulletModifier);
    }

    /**
     * 判断是否成功捕获
     * @param fish 鱼的类型
     * @param bullet 炮弹的类型
     * @return 是否捕获成功
     */
    public boolean isCaught(int fish, int bullet) {
        // 计算最终概率
        double finalProbability = calculateCatchProbability(fish, bullet);
        // 生成随机数 [0, 1]
        double randomValue = RANDOM.nextDouble();
        // 如果随机数小于等于最终概率，则捕获成功
        return randomValue <= finalProbability;
    }
}

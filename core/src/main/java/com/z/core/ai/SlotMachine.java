package com.z.core.ai;

public class SlotMachine {

    private static final int MAX_LOSE_COUNT = 5;  // 连续亏损次数阈值
    private int loseCount = 0;  // 当前亏损次数
    private boolean mustWinTriggered = false;  // 是否触发了必须中奖机制
    private double roomC3 = 0.2;  // 房间状态因子的系数
    private double roomC4 = 0.001;  // 房间状态因子的系数

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
        // 房间状态因子 = C3 * (loseCount - winCount) + C4 * (betAmount - winAmount)
        return roomC3 * (loseCount - winCount) + roomC4 * (betAmount - winAmount);
    }

    /**
     * 模拟玩家下注，并计算是否触发必须中奖的机制
     *
     * @param betAmount 本轮下注金额
     * @param winAmount 本轮赢钱金额
     * @param winCount 房间内的玩家赢的次数
     * @param loseCount 房间内的玩家输的次数
     * @param betAmount 房间内的玩家投注金额
     * @param winAmount 房间内的玩家赢得金额
     * @return 本轮是否中奖
     */
    public boolean playRound(double betAmount, double winAmount, long winCount, long loseCount, double betAmountAll, double winAmountAll) {
        boolean isWinner = false;

        // 如果没有赢钱，增加连续亏损次数
        if (winAmount == 0) {
            loseCount++;
        } else {
            // 如果赢钱，重置连续亏损次数
            loseCount = 0;
        }

        // 检查是否触发了必须中奖的机制
        if (loseCount >= MAX_LOSE_COUNT && !mustWinTriggered) {
            // 计算房间状态因子
            double roomStateFactor = calculateRoomStateFactor(winCount, loseCount, betAmountAll, winAmountAll);

            // 强制中奖，并调整中奖概率
            isWinner = calculateWinProbability(roomStateFactor);
            mustWinTriggered = true;  // 标记为已触发
            System.out.println("触发了必须中奖机制！");
        } else {
            // 正常的中奖概率计算
            isWinner = calculateWinProbability(0);
        }

        // 更新状态：如果本轮中奖了，重置亏损次数
        if (isWinner) {
            loseCount = 0;
        }

        return isWinner;
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

    public static void main(String[] args) {
        SlotMachine slotMachine = new SlotMachine();

        // 假设房间内的输赢数据
        long winCount = 10;
        long loseCount = 150;
        double betAmountAll = 5000.0;
        double winAmountAll = 2000000.0;

        // 模拟玩家进行10轮游戏
        for (int i = 0; i < 10; i++) {
            double betAmount = 100;  // 每轮下注100
            double winAmount = 0;  // 初始化本轮赢钱金额

            // 计算是否中奖
            boolean isWinner = slotMachine.playRound(betAmount, winAmount, winCount, loseCount, betAmountAll, winAmountAll);

            if (isWinner) {
                winAmount = 1000;  // 假设中奖金额为1000
                System.out.println("本轮中奖，赢得金额：" + winAmount);
            } else {
                System.out.println("本轮未中奖");
            }
        }
    }
}


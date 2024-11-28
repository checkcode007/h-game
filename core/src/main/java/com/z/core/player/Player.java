package com.z.core.player;

/**
 * 棋牌玩家
 */
public class Player {
    protected long uid;
    private boolean system;
    protected boolean banker;//是否是庄家

    protected long chips; // 玩家当前筹码
    protected long betAmount; // 当前下注金额
    private boolean robot;

    public Player(long uid, long chips,boolean banker,boolean robot) {
        this.uid = uid;
        this.chips = chips;
        this.banker = banker;
        this.robot = robot;
    }

    public void placeBet(long amount) {
        this.betAmount = amount;
        this.chips -= amount;
    }
    public int calculateHandValue() {
        // 实现手牌值的计算逻辑，返回“牛几”结果
        // ...
        return 0; // 示例
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isBanker() {
        return banker;
    }

    public void setBanker(boolean banker) {
        this.banker = banker;
    }

    public long getChips() {
        return chips;
    }

    public void setChips(long chips) {
        this.chips = chips;
    }

    public long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(long betAmount) {
        this.betAmount = betAmount;
    }

    public boolean isRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }
}

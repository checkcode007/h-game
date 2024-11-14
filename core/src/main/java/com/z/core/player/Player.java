package com.z.core.player;

import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;

/**
 * 棋牌玩家
 */
public class Player {
    private long uid;

    private int chips; // 玩家当前筹码
    private int betAmount; // 当前下注金额

    public Player(long uid, int chips) {
        this.uid = uid;
        this.chips = chips;
    }

    public void placeBet(int amount) {
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

    public int getChips() {
        return chips;
    }


    public int getBetAmount() {
        return betAmount;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }
}

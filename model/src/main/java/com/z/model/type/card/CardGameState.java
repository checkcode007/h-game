package com.z.model.type.card;

public enum CardGameState {
    WAITING_FOR_PLAYERS(0,"等待玩家"),
    BETTING(1,"下注阶段"),
    DEALING(2,"发牌阶段"),
    CALCULATING_RESULTS(3,"计算结果阶段"),
    GAME_OVER(4,"游戏结束");
    public int k;
    public String name;

    CardGameState(int k, String name) {
        this.k = k;
        this.name = name;
    }

}

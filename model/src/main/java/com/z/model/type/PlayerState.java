package com.z.model.type;

/**
 *玩家状态
 */
public enum PlayerState {
    READY(0,"准备状态"),INGAME(1,"游戏中"),QUIT(2,"退出状态")
    ;
    public int k;
    public String name;

    PlayerState(int k, String name) {
        this.k = k;
        this.name = name;
    }
}

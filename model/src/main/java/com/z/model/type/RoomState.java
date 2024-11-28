package com.z.model.type;

/**
 * 房间状态
 */
public enum RoomState {
    IDEL(1,"空闲"),
    BUSY(2,"占用"),
    FULL(3,"已满"),
    ;
    public int k;
    public String name;

    RoomState(int k, String name) {
        this.k = k;
        this.name = name;
    }
}

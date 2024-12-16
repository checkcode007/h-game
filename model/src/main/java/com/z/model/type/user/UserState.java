package com.z.model.type.user;

/**
 * 用户状态
 */
public enum UserState {
    DEFAULT(0,"正常状态"),
    DEL(1,"删除状态"),
    BAN(2,"禁用状态"),
    GAME(3,"游戏状态"),
    ;
    public int k;
    public String name;

    UserState(int k, String name) {
        this.k = k;
        this.name = name;
    }

    public static UserState getUserState(int k) {
        for (UserState state : UserState.values()) {
            if (state.k == k) {
                return state;
            }
        }
        return DEFAULT;
    }
}

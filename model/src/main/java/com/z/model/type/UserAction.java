package com.z.model.type;

/**
 * 用户行为
 */
public enum UserAction {
    REG(0),LOGIN(1),LOGOUT(2);

    ;
    public int k;

    UserAction(int k) {
        this.k = k;
    }
}

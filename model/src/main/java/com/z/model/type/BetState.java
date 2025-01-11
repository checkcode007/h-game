package com.z.model.type;
/**
 * 投注模式
 */
public enum BetState {
    LOW_BET(0),    // 低投注模式
    MEDIUM_BET(1), // 中投注模式
    HIGH_BET(2),   // 高投注模式
    SPECIAL_BET(3)    // 特殊模式
    ;
    int k;

    BetState(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }
    public static BetState getBetState(int k) {
        for (BetState state : BetState.values()) {
            if (state.getK() == k) {
                return state;
            }
        }
        return null;
    }
}

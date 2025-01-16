package com.z.model.type;
/**
 * 用户押注状态
 * 投注模式
 */
public enum SlotState {
    LOW_BET(0),    // 低投注模式
    MEDIUM_BET(1), // 中投注模式
    HIGH_BET(2),   // 高投注模式
    SPECIAL_BET(3)    // 特殊模式
    ;
    int k;

    SlotState(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }
    public static SlotState getBetState(int k) {
        for (SlotState state : SlotState.values()) {
            if (state.getK() == k) {
                return state;
            }
        }
        return null;
    }
}

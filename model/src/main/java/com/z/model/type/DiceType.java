package com.z.model.type;

import com.z.model.proto.CommonGame;

/**
 * 骰子类型
 */
public enum DiceType {
    SMALL(CommonGame.WMDice.WD_SMALL,2),
    TIE(CommonGame.WMDice.WD_TIE,6),
    BIG(CommonGame.WMDice.WD_BIG,2),
    ;
    CommonGame.WMDice k;
    int rate;
    DiceType(CommonGame.WMDice k,int rate) {
        this.k = k;
        this.rate = rate;
    }

    public CommonGame.WMDice getK() {
        return k;
    }

    public int getRate() {
        return rate;
    }
    public static DiceType getType(CommonGame.WMDice k) {
        for (DiceType v : values()) {
            if(v.getK()== k) {
                return v;
            }
        }
        return null;
    }


}

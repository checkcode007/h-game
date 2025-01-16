package com.z.core.ai.fish;

import com.z.model.BetParam;
import com.z.model.type.SlotState;

public class MidState extends FishState {

    public MidState(SlotState k) {
        super(k);
        C1 = 0.05;
        C3 = 0.1;
    }

    @Override
    public boolean catchFish(BetParam param, int fishType,double fish, double bullet) {
        if(fishType>18){
            C1 = 0.01;
            C3 = 0.05;
        }

        return  super.catchFish(param, fishType,fish, bullet);
    }

}

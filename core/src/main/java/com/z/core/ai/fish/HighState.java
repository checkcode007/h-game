package com.z.core.ai.fish;

import com.z.model.BetParam;
import com.z.model.type.SlotState;

public class HighState  extends FishState  {

    public HighState(SlotState k) {
        super(k);
        C1 = 0.1;
        C3 = 0.5;
    }

    @Override
    public boolean catchFish(BetParam param, int fishType,double fish, double bullet) {
        long winC = param.getWinC();
        long totalC = param.getTotalC();
        if(fishType>18){
            C1 = 0.01;
            C3 = 0.05;
        }

        return  super.catchFish(param, fishType,fish, bullet);
    }
}

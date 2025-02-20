package com.z.core.ai.fish;

import com.z.model.BetParam;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FishLowState extends FishState {
    private static final Log log = LogFactory.getLog(FishLowState.class);
    public FishLowState(SlotState k) {
        super(k);
        C1 = 0.01;
        C3 = 0.05;
    }

    @Override
    public boolean catchFish(BetParam param, int fishType,double fish, double bullet) {
        long winC = param.getWinC();
        long totalC = param.getTotalC();
        long radio = (winC*100)/totalC;
        if(fishType>18){
            return false;
        }else if(fishType>14){
            C1 = 0.02;
            C3 = 0.05;
        }
        return  super.catchFish(param, fishType,fish, bullet);
    }


}
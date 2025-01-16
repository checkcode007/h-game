package com.z.core.service.game.fish;

import com.z.core.ai.fish.*;
import com.z.model.BetParam;
import com.z.model.type.SlotState;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 通用变量
 */
public enum FishCommon {
    ins;
    // 随机数生成器
    private final Random RANDOM = new Random();

    Map<SlotState, FishState> betStateMap = new HashMap<>();
    FishCommon() {
        init();
    }
    public void init() {
        betStateMap.put(SlotState.LOW_BET,new LowState(SlotState.LOW_BET));
        betStateMap.put(SlotState.MEDIUM_BET,new MidState(SlotState.MEDIUM_BET));
        betStateMap.put(SlotState.HIGH_BET,new HighState(SlotState.HIGH_BET));
        betStateMap.put(SlotState.SPECIAL_BET,new SpecialState(SlotState.SPECIAL_BET));
    }


    /**
     * 判断是否成功捕获
     * @param fish 鱼的类型
     * @param bullet 炮弹的类型
     * @return 是否捕获成功
     */
    public boolean isCaught(BetParam param,int fishType,double fish, double bullet) {
        FishState betState = betStateMap.get(SlotState.getBetState(param.getState()));// 计算最终概率
        return betState.catchFish(param,fishType,fish*1.0d/10000,bullet*1.0d/10000);
    }
}

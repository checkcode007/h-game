package com.z.core.ai.clear;

import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MidState extends ClearState {
    public MidState(SlotState k) {
        super(k);
        C1 =0.3f;
        C2= 0.1f;
        C3=0.01f;
        C4=0f;
    }


    @Override
    public Map<Integer, Integer> weight(Map<Integer, Slot> slots, List<Slot> list, Set<Integer> goals, BetParam param) {
        Map<Integer, Integer> map = new HashMap();
        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            // 动态调整权重变化：目标符号增加的幅度比非目标符号小
            int adjustFactor = isGoal ? diffW1 : (int) (diffW1 * 0.5); // 目标符号调整幅度小于非目标符号
            if (s.isBaida()|| s.isBonus()) {
                adjustFactor = isGoal ? diffW1 * 2 : diffW1 / 2;
            }else if (s.isScatter()){
                adjustFactor = isGoal ? diffW1 * 3 : diffW1 /3 ;
            }
            if (isGoal) {
                s.subW1(adjustFactor);
            } else {
                s.addW1(adjustFactor);
            }
            map.put(s.getK(), s.getW1());
        }
        freeWeight( map,slots, param);
        return map;
    }

    @Override
    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_0(slots, board, list, param);
        list.removeIf(e->e.getK()<6);
    }
}

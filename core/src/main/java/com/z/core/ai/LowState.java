package com.z.core.ai;

import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LowState extends SuperState {
    private static final Log log = LogFactory.getLog(LowState.class);

    public LowState(SlotState k) {
        super(k);
        C1 =0.2f;
        C2= 0.1f;
        C3=0f;
        C4=0f;
        roomC3 = 0.05;  // 房间输赢次数差的权重
        roomC4 = 0.001;  // 房间输赢金额差的权重
    }

    @Override
    void betStateFilter(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        int x =  param.getX();
        if(param.getBonus()>1){
            list.removeIf(e -> e.isBonus());
        }
        if (param.getScatter()>1) {
            list.removeIf(e -> e.isScatter());
        }
        if(x<4){
            list.removeIf(e -> e.isBaida());
        }
    }


    @Override
    public Map<Integer, Integer> weight(Map<Integer, Slot> slots, List<Slot> list, Set<Integer> goals, BetParam param) {
        Map<Integer, Integer> map = new HashMap();
        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            // 动态调整权重变化：目标符号增加的幅度比非目标符号小
            int adjustFactor = isGoal ? diffW1 : (int) (diffW1 * 0.2); // 目标符号调整幅度小于非目标符号
            if (isGoal) {
                s.subW1(adjustFactor);
            } else {
                s.addW1(adjustFactor);
            }
            map.put(s.getK(), s.getW1());
        }
        freeWeight(map, slots, param);
        return map;
    }

    @Override
    public Slot winSlot(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots, List<Slot> list, BetParam param) {
        return null;
    }
}

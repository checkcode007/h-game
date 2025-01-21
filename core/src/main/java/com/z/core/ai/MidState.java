package com.z.core.ai;

import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;

import java.util.*;

public class MidState extends SuperState {
    public MidState(SlotState k) {
        super(k);
        C1 =0.3f;
        C2= 0.2f;
        C3=0.01f;
        C4=0f;
    }

    @Override
    void betStateFilter(Table<Integer,Integer, SlotModel> board, List<Slot> list, BetParam param) {
        int x = param.getX();
        list.removeIf(e-> e.isBonus() || e.isBaida() );
        if(x<1) return;
        if( x==3){//不让符号连接的太多,最多三个连接
            Set<Integer> set1 = new HashSet<>();
            for (SlotModel m : board.row(0).values()) {
                set1.add(m.getK());
            }
            Set<Integer> set2 = new HashSet<>();
            for (SlotModel m : board.row(1).values()) {
                set2.add(m.getK());
            }
            set2.retainAll(set1);
            if(!set2.isEmpty()){
                Set<Integer> set3 = new HashSet<>();
                for (SlotModel m : board.row(2).values()) {
                    set3.add(m.getK());
                }
                set3.retainAll(set2);
                if(!set3.isEmpty()){
                    list.removeIf(e->set3.contains(e.getK()));
                }
            }
        }
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
}

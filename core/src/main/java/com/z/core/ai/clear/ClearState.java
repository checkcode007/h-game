package com.z.core.ai.clear;

import com.google.common.collect.Table;
import com.z.core.ai.SuperState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;

import java.util.List;
import java.util.Map;

public class ClearState extends SuperState {


    public ClearState(SlotState k) {
        super(k);
    }

    @Override
    public void checkCol(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        int x = param.getX();
        boolean b_baida =false;
        for (SlotModel m : board.row(x).values()) {
            if (m.isBonus()){
                b_baida = true;
                break;
            }
        }
        if(b_baida){
            list.removeIf(e->e.isBonus());
        }

        super.checkCol(slots, board, list, param);
    }
}

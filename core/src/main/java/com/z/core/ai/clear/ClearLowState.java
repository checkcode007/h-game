package com.z.core.ai.clear;

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

public class ClearLowState extends ClearState {
    private static final Log log = LogFactory.getLog(ClearLowState.class);

    public ClearLowState(SlotState k) {
        super(k);
        C1 =0.2f;
        C2=0.03f;
        C3=0f;
        C4=0f;
        roomC3 = 110;  // 房间输赢次数差的权重
        roomC4 = 0.0005;  // 房间输赢金额差的权重
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
    public void checkCol(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        list.removeIf(Slot::isBaida);
        interruptSameY(board,list,param.getX());
        int scatter=0,bonus= 0;
        for (SlotModel m : board.values()) {
            if(m.isScatter()){
                scatter++;
            } else if (m.isBonus()) {
                bonus++;
            }
        }
        if(scatter>1) {
            list.removeIf(Slot::isScatter);
        }
        if (bonus>1) {
            list.removeIf(Slot::isBonus);
        }
        super.checkCol(slots, board, list, param);
    }

    @Override
    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_0(slots, board, list, param);
        int x = param.getX();
        if(param.isFree()  || param.getContinueC()>0) {
            interrupt(board,list,x);
        }else{
            list.removeIf(e->e.getK()>5);
        }
    }

    @Override
    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_1(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<1){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void col_2(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_2(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<1){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void col_3(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_3(slots, board, list, param);
        int x = param.getX();
        interrupt(board,list,x);
    }
    @Override
    public void col_4(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_4(slots, board, list, param);
        int x = param.getX();
        interrupt(board,list,x);
    }
}

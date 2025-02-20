package com.z.core.ai.clear.puck;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.core.ai.clear.ClearState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PuckMidState extends ClearState {
    public PuckMidState(SlotState k) {
        super(k);
        C1 =0.3f;
        C2= 0.1f;
        C3=0.01f;
        C4=0f;
        roomC3 = 160;  // 房间输赢次数差的权重
        roomC4 = 0.001;  // 房间输赢金额差的权重

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
    public void checkCol(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.checkCol(slots, board, list, param);
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
        if (bonus>2) {
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
            list.removeIf(e->e.getK()>8);
        }
    }
    @Override
    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        interrupt(board,list,x);
    }


    @Override
    public int bigWild(BetParam param) {
        if(param.isFree()) return 0;
        //运动员划过的线(2,3,4轴)
        long loss = param.getTotalC()-param.getWinC();
        float radio = loss * 1f/param.getWinC();
        radio = radio*0.5f;
        if( RandomUtil.randomDouble()<radio){
            return RandomUtils.nextInt(1, 4);
        }
        return 0;
    }
}

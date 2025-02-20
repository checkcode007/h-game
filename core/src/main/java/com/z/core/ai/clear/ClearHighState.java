package com.z.core.ai.clear;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;

public class ClearHighState extends ClearState {

    public ClearHighState(SlotState k) {
        super(k);
        C1 =0.5f;
        C2=0.1f;
        C3=0.05f;
        C4=0.01f;
        roomC3 = 200;  // 房间输赢次数差的权重
        roomC4 = 0.002;  // 房间输赢金额差的权重
    }
    @Override
    public Map<Integer, Integer> weight(Map<Integer, Slot> slots, List<Slot> list, Set<Integer> goals, BetParam param) {
        Map<Integer, Integer> map = new HashMap();
        for (Slot s : list) {
            // 降低的概率
            boolean isGoal = goals != null && goals.contains(s.getK());
            // 动态调整权重变化：目标符号增加的幅度比非目标符号小
            int adjustFactor = isGoal ? diffW1 : (int) (diffW1 * 0.5); // 目标符号调整幅度小于非目标符号
            if (s.isScatter()){
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
        interruptSameY(board,list,param.getX());
        int scatter=0,bonus= 0;
        for (SlotModel m : board.values()) {
            if(m.isScatter()){
                scatter++;
            } else if (m.isBonus()) {
                bonus++;
            }
        }
        if(scatter>2) {
            list.removeIf(Slot::isScatter);
        }
        if (bonus>3) {
            list.removeIf(Slot::isBonus);
        }
        super.checkCol(slots, board, list, param);
    }
    @Override
    public void interruptSameY(Table<Integer, Integer, SlotModel> board, List<Slot> list, int index) {
//        log.info("index:"+index);
        Collection<SlotModel> tmpList=board.row(index).values();
        Map<Integer,Integer> map = new HashMap();
        for (SlotModel i : tmpList) {
            map.put(i.getK(),map.getOrDefault(i.getK(),0)+1);
        }
        map.forEach((k,v)->{
            if(v>3){
                list.removeIf(e->e.getK() == k);
            }
        });
    }
    @Override
    public int bigWild(BetParam param) {
        if(param.isFree()) return 0;
        //运动员划过的线(2,3,4轴)
        long loss = param.getTotalC()-param.getWinC();
        float radio = loss * 1f/param.getWinC();
        radio = radio*0.7f;
        if( RandomUtil.randomDouble()<radio){
            return RandomUtils.nextInt(1, 4);
        }
        return 0;
    }

}

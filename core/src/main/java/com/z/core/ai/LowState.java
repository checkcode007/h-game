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
    void betStateWight(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots,BetParam param) {

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
    public void col_0(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        boolean free = param.isFree();
        int continueC = param.getContinueC();
        long winC = param.getWinC();
        long totalC = param.getTotalC();
        list.removeIf(e -> e.getK()>7);

    }
    @Override
    public void col_1(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        int x = param.getX();
        boolean free = param.isFree();
        int continueC = param.getContinueC();
        long winC = param.getWinC();
        long totalC = param.getTotalC();
        long radio = (winC*100)/totalC;
        boolean b = false;
        if(radio>95){
            b = winC%50!=0;
        } else if (radio>80) {
            b = winC%40!=0;
        }else if (radio>50) {
            b = winC%30!=0;
        }else {
            b = winC%10!=0;
        }
        if(continueC>0){
            b = true;
        }
        if(b){
            log.info("=====>interrupt: winC"+winC +" totalC:"+totalC+" radio:"+radio);
            interrupt(board,list,x);
        }else{
            log.info("----->interrupt: winC"+winC +" totalC:"+totalC+" radio:"+radio);
        }
    }

    @Override
    public void col_2(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        int x = param.getX();
        boolean free = param.isFree();
        int continueC = param.getContinueC();
        long winC = param.getWinC();
        long totalC = param.getTotalC();
        long radio = (winC*100)/totalC;
        boolean b = false;
        if(radio>95){
            b = winC%50!=0;
        } else if (radio>80) {
            b = winC%40!=0;
        }else if (radio>50) {
            b = winC%30!=0;
        }else {
            b = winC%10!=0;
        }
        if(continueC>0){
            b = true;
        }
        if(b){
            log.info("====>interrupt: winC"+winC +" totalC:"+totalC+" radio:"+radio);
            interrupt(board,list,x);
        }else {
            log.info("----->interrupt: winC"+winC +" totalC:"+totalC+" radio:"+radio);
        }
    }

}

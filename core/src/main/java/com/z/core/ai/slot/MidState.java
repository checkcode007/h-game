package com.z.core.ai.slot;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.model.BetParam;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.LineType;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class MidState extends CommonState{
    private static final Log log = LogFactory.getLog(MidState.class);

    public MidState(SlotState k) {
        super(k);
        C1 =0.3f;
        C2= 0.2f;
        C3=0.01f;
        C4=0f;
        LINELIMIT = 0.5f;
    }

    @Override
    void betStateFilter(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
//        list.removeIf(e -> e.getK()<4);
    }

    @Override
    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_0(slots, board, list, param);
        int x = param.getX();
        if(param.isFree()) {
            interrupt(board,list,x);
        }else{
            list.removeIf(Slot::isScatter);
        }
    }

    @Override
    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_1(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void col_2(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_2(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void col_3(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_3(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()){
            return;
        }
        interrupt(board,list,x);
    }
    @Override
    public void col_4(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_4(slots, board, list, param);
        int x = param.getX();
        interrupt(board,list,x);
    }
    @Override
    public List<Rewardline> getRandomline(Map<LineType, List<Rewardline>> lineMap, BetParam param) {
        double r = RandomUtil.randomDouble();
        if( r > 0.4){
            return null;
        }
        if(RandomUtil.randomInt(10)%4 ==0){
            List<Rewardline> lines = lineMap.get(LineType.MID);
            int index = RandomUtil.randomInt(lines.size());
            Rewardline line =  lines.get(index);
            return Collections.singletonList(line);
        }else{
            List<Rewardline> lines = lineMap.get(LineType.LOW);
            int index = RandomUtil.randomInt(lines.size());
            Rewardline line =  lines.get(index);
            return Collections.singletonList(line);
        }
    }
}

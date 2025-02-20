package com.z.core.ai.slot;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.core.ai.SuperState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.LineType;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class HighState extends CommonState {
    private static final Log log = LogFactory.getLog(LowState.class);

    public HighState(SlotState k) {
        super(k);
        C1 =0.8f;
        C2=0.5f;
        C3=0.1f;
        C4=0.01f;
        LINELIMIT = 0.3f;

    }

    @Override
    void betStateFilter(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {

    }

    @Override
    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_0(slots, board, list, param);
        int x = param.getX();
        if(param.isFree() ) {
            interrupt(board,list,x);
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
        if(!param.isFree() ){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void col_3(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_3(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree() ){
            return;
        }
        interrupt(board,list,x);
    }
    @Override
    public void col_4(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_4(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree() ){
            return;
        }
        interrupt(board,list,x);
    }
    @Override
    public List<Rewardline> getRandomline(Map<LineType, List<Rewardline>> lineMap, BetParam param) {
        double r = RandomUtil.randomDouble();
        log.info("r---->"+r);
        if( r> 0.6){
            return null;
        }
        Map<Integer, Rewardline> map = new HashMap<>();
        List<Rewardline> lines = lineMap.get(LineType.HIGH);
        lines.addAll(lineMap.get(LineType.LOW));
        lines.addAll(lineMap.get(LineType.MID));
        for (int i = 0; i < 3; i++) {
            int index = RandomUtil.randomInt(lines.size());
            Rewardline line =  lines.get(index);
            map.put(line.getLineId(), line);
        }
        return new ArrayList<>(map.values());
    }
}

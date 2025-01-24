package com.z.core.ai.clear;

import com.google.common.collect.Table;
import com.z.core.ai.SuperState;
import com.z.core.service.game.slot.SlotCommon;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ClearState extends SuperState {
    private static final Log log = LogFactory.getLog(ClearState.class);

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
            list.removeIf(Slot::isBonus);
        }
        if (param.isFree()){
            list.removeIf(e->e.isScatter()||e.isBonus()|| e.isBaida());
        }
        super.checkCol(slots, board, list, param);
    }

    @Override
    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_0(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        Set<Integer> set = board.row(x + 1).keySet();
        list.removeIf(e->set.contains(e.getK()));
    }

    @Override
    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_1(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        Set<Integer> set = board.row(0).keySet();
        list.removeIf(e->set.contains(e.getK()));
    }

    @Override
    public void col_2(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_2(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        Set<Integer> set1 = board.row(x-1).keySet();
        Set<Integer> set2 = board.row(x+1).keySet();
        list.removeIf(e->set1.contains(e.getK())|| set2.contains(e.getK()));
    }

    @Override
    public void col_3(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_3(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        interrupt(board,list,x);
    }
    @Override
    public void col_4(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_4(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void interrupt(Table<Integer, Integer, SlotModel> board, List<Slot> list, int index) {
//        SlotCommon.ins.print(board,null,null,0,0);
//        print(list,"interrupt--->start:"+index);
        if(index==0){
            Collection<SlotModel> tmpList=board.row(index+1).values();
            Set<Integer> set = new HashSet<>();
            for (SlotModel i : tmpList) {
                set.add(i.getK());
            }
            list.removeIf(e->set.contains(e.getK()));
        }else if(index==4){
            Collection<SlotModel> tmpList=board.row(index-1).values();
            Set<Integer> set = new HashSet<>();
            for (SlotModel i : tmpList) {
                set.add(i.getK());
            }
            list.removeIf(e->set.contains(e.getK()));
        }else{
            Collection<SlotModel> tmpList=board.row(index-1).values();
            Set<Integer> set = new HashSet<>();
            for (SlotModel i : tmpList) {
                set.add(i.getK());
            }
            tmpList=board.row(index+1).values();
            for (SlotModel i : tmpList) {
                set.add(i.getK());
            }
            list.removeIf(e->set.contains(e.getK()));
        }
//        print(list,"interrupt--->end:"+index);
    }

    /**
     * 同一排不能相同太多
     * @param board
     * @param list
     * @param index
     */
    public void interruptSameY(Table<Integer, Integer, SlotModel> board, List<Slot> list, int index) {
//        log.info("index:"+index);
        Collection<SlotModel> tmpList=board.row(index).values();
        Map<Integer,Integer> map = new HashMap();
        for (SlotModel i : tmpList) {
            map.put(i.getK(),map.getOrDefault(i.getK(),0)+1);
        }
        map.forEach((k,v)->{
            if(v>2){
                list.removeIf(e->e.getK() == k);
            }
        });
    }
}

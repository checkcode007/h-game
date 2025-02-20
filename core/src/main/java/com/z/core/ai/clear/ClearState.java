package com.z.core.ai.clear;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.core.ai.SuperState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.SlotState;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ClearState extends SuperState {
    private static final Log log = LogFactory.getLog(ClearState.class);

    public ClearState(SlotState k) {
        super(k);
    }
    @Override
    public void checkContinue(Table<Integer, Integer, SlotModel> board, List<Slot> list, int x, int continueC) {
        if (x < 1) return;
        if (continueC < 2) return;
        list.removeIf(e -> e.isScatter() || e.isBonus() || e.isBaida());
        if (continueC < 4) return;
        if (x == 1) {//不让符号连接的太多,最多三个连接
            Set<Integer> set1 = new HashSet<>();
            for (SlotModel m : board.row(0).values()) {
                set1.add(m.getK());
            }
            Set<Integer> set2 = new HashSet<>();
            for (SlotModel m : board.row(1).values()) {
                set2.add(m.getK());
            }
            set2.retainAll(set1);
            if (!set2.isEmpty()) {
                if (!set2.isEmpty()) {
                    list.removeIf(e -> set2.contains(e.getK()));
                }
            }
        }

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
        interrupt(board,list,x);
    }

    @Override
    public void col_1(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_1(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        interrupt(board,list,x);
    }

    @Override
    public void col_2(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_2(slots, board, list, param);
        int x = param.getX();
        if(!param.isFree()  && param.getContinueC()<2){
            return;
        }
        interrupt(board,list,x);
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
    @Override
    public int bigWild(BetParam param) {
        if(param.isFree()) return 0;
        //运动员划过的线(2,3,4轴)
        long loss = param.getTotalC()-param.getWinC();
        float radio = loss * 1f/param.getWinC();
        radio = radio*0.2f;
        if( RandomUtil.randomDouble()<radio){
            return RandomUtils.nextInt(1, 4);
        }
        return 0;
    }
}

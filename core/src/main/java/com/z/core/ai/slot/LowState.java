package com.z.core.ai.slot;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Table;
import com.z.core.ai.SuperState;
import com.z.core.ai.clear.ClearLowState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.type.LineType;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class LowState extends CommonState {
    private static final Log log = LogFactory.getLog(LowState.class);

    public LowState(SlotState k) {
        super(k);
        C1 =0.2f;
        C2= 0.1f;
        C3=0f;
        C4=0f;
        roomC3 = 0.05;  // 房间输赢次数差的权重
        roomC4 = 0.001;  // 房间输赢金额差的权重
        LINELIMIT = 0.8f;
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
    public Slot winSlot(Table<Integer, Integer, SlotModel> board, Map<Integer, Integer> map, Map<Integer, Slot> slots, List<Slot> list, BetParam param) {
        return null;
    }

    @Override
    public void col_0(Map<Integer, Slot> slots, Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param) {
        super.col_0(slots, board, list, param);
        int x = param.getX();
        if(param.isFree()) {
            interrupt(board,list,x);
        }else{
            list.removeIf(e->e.getK()>6);
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
        double d = calculateAdjustedWinProbability(param.getWinC(),param.getTotalC(),param.getRoomWinC(),param.getRoomTotalC(),baseProbability);
        // 生成一个0到1之间的随机数
        double r = RandomUtil.randomDouble();
        log.info("r---->"+r);
        if( r> 0.4){
            return null;
        }

        Map<Integer, Rewardline> map = new HashMap<>();
        List<Rewardline> lines = lineMap.get(LineType.LOW);
        for (int i = 0; i < 2; i++) {
            int index = RandomUtil.randomInt(lines.size());
            Rewardline line =  lines.get(index);
            map.put(line.getLineId(), line);
        }
        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        LowState superState = new LowState(null);
        int winCount = 228;
        int totaC = 578;
        double betAmount = 1787200;
        double winAmount = 135505810;
        long winC = 228;
        long totalC = 1078;
        long roomWinC = 5;
        long roomTotalC = 100;
        double f1 = superState.calculateAdjustedWinProbability(winCount,totaC,roomWinC,roomTotalC,1);
        System.err.println(f1);

    }
}

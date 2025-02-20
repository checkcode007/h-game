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

public abstract class CommonState extends SuperState {
    private static final Log log = LogFactory.getLog(CommonState.class);
    protected  float LINELIMIT = 0.5f;
    // 假设基础中奖概率为 10%
    protected double baseProbability = 0.1;
    public CommonState(SlotState k) {
        super(k);
        C1 =0.8f;
        C2=0.5f;
        C3=0.1f;
        C4=0.01f;
    }

    abstract void betStateFilter(Table<Integer, Integer, SlotModel> board, List<Slot> list, BetParam param);

    @Override
    public Map<Integer, Integer> weight(Map<Integer, Slot> slots, List<Slot> list, Set<Integer> goals, BetParam param) {
        Map<Integer, Integer> map = new HashMap();
        for (Slot s : list) {
            map.put(s.getK(), s.getW1());
        }
        freeWeight( map,slots, param);
        return map;
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
    public void interrupt(Table<Integer, Integer, SlotModel> board, List<Slot> list, int index) {
        Set<Integer> lastSet = new HashSet<>();
        for (int i = 0; i < index; i++) {
            Set<Integer> set = new HashSet<>();
            for (SlotModel m : board.row(i).values()) {
                set.add(m.getK());
            }
            if (i == 0) {
                lastSet.addAll(set);
            } else {
                lastSet.retainAll(set);
            }
        }
        list.removeIf(slot -> lastSet.contains(slot.getK()));
    }
    @Override
    public List<Rewardline> getRandomline(Map<LineType,List<Rewardline>> lineMap, BetParam param) {
        double d = calculateAdjustedWinProbability(param.getWinC(),param.getTotalC(),param.getRoomWinC(),param.getRoomTotalC(),baseProbability);
        // 生成一个0到1之间的随机数
        double randomValue = Math.random();
        boolean win = isWin(d,randomValue);
        if(!win){
            return null;
        }
        Map<Integer, Rewardline> map = new HashMap<>();
        List<Rewardline> lines = lineMap.get(LineType.MID);
        for (int i = 0; i < 10; i++) {
            int index = RandomUtil.randomInt(lines.size());
            Rewardline line =  lines.get(index);
            map.put(line.getLineId(), line);
        }
        return new ArrayList<>(map.values());
    }
    /**
     * 计算调整后的中奖概率，考虑玩家和房间的输赢情况
     *
     * @param playerWinCount 玩家赢的次数
     * @param playerTotalCount 玩家zong的次数
     * @param roomWinCount 房间内所有玩家赢的次数
     * @param roomTotalCount 房间内所有玩家zong的次数
     * @param baseProbability 基础中奖概率
     * @return 调整后的中奖概率
     */
    protected double calculateAdjustedWinProbability(long playerWinCount, long playerTotalCount,
                                                   long roomWinCount, long roomTotalCount,
                                                   double baseProbability) {
        long playerLoseCount = playerTotalCount-playerWinCount;
        long roomLoseCount = roomTotalCount-roomWinCount;
        // 每次输增加 0.5% 的中奖概率，最多提高到100%
        double playerLoseImpact = playerLoseCount * 0.00025;  // 每输 1 次，增加 0.5%
        double playerWinImpact = playerWinCount * 0.00023;    // 每赢 1 次，减少 0.5%

        // 房间内的输赢影响
        double roomLoseImpact = roomLoseCount * 0.013;  // 房间内输的次数对中奖概率的影响
        double roomWinImpact = roomWinCount * 0.001;    // 房间内赢的次数对中奖概率的影响

        // 计算调整后的中奖概率
        double adjustedProbability = baseProbability + playerLoseImpact - playerWinImpact
                + roomLoseImpact - roomWinImpact;

        // 确保概率值在 0 到 1 之间
        adjustedProbability = Math.max(0, Math.min(adjustedProbability, 1.0));

        return adjustedProbability;
    }

    /**
     * 计算是否中奖，基于调整后的中奖概率和生成的随机数
     *
     * @param adjustedProbability 调整后的中奖概率
     * @param randomValue 随机生成的值，通常为 0 到 1 之间
     * @return 是否中奖
     */
    protected boolean isWin(double adjustedProbability, double randomValue) {

        log.info("adjustedProbability:"+adjustedProbability+",randomValue:"+randomValue);
        return randomValue < adjustedProbability;
    }

}

package com.z.core.service.game.slot;

import com.google.common.collect.Table;
import com.z.core.ai.LowState;
import com.z.core.ai.SpecialState;
import com.z.core.ai.SuperState;
import com.z.core.ai.fish.HighState;
import com.z.core.ai.fish.MidState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.type.SlotState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 百变玛丽
 * 通用变量
 */
public enum SlotCommon {
    ins;
    private static final Log log = LogFactory.getLog(SlotCommon.class);

    public static final int diffW1 = 100, diffW2 = 10;

    public static final int diffHighW1 = 5;

    Map<SlotState, SuperState> betStateMap = new HashMap<>();


    SlotCommon() {
        init();
    }

    public void init() {
        betStateMap.put(SlotState.LOW_BET,new LowState(SlotState.LOW_BET));
        betStateMap.put(SlotState.MEDIUM_BET,new MidState(SlotState.MEDIUM_BET));
        betStateMap.put(SlotState.HIGH_BET,new HighState(SlotState.HIGH_BET));
        betStateMap.put(SlotState.SPECIAL_BET,new SpecialState(SlotState.SPECIAL_BET));
    }

    /**
     * 随机符号
     *
     * @param slots    所有牌
     * @param goals    中奖的牌
     *                 RTP = 玩家回报 / 玩家投入。例如设定 RTP = 95%，则：
     *                 高 RTP 情况：符号池中低稀有度符号比例更高，增加中奖概率；
     *                 低 RTP 情况：符号池稀有符号比例增加，减少普通中奖
     * @return
     */
    public Slot random(CommonGame.GameType gameType, Table<Integer,Integer,SlotModel> board, Map<Integer, Slot> slots, Set<Integer> goals, BetParam param) {
        SuperState betState = betStateMap.get(SlotState.getBetState(param.getState()));
        if(gameType == CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER || gameType == CommonGame.GameType.SHUIHUZHUAN_HIGHER){
            return randomHigher(gameType,slots,goals,param);
        }
        return betState.random(gameType, board, slots, goals, param);
    }
    public Slot randomHigher(CommonGame.GameType gameType,Map<Integer, Slot> slots, Collection<Integer> goals, BetParam param) {
        SuperState betState = betStateMap.get(SlotState.SPECIAL_BET);
        return betState.randomHigher(gameType,slots,goals,param);
    }
    public void print(Table<Integer, Integer, SlotModel> board, CommonGame.GameType gameType, CommonGame.RoomType roomType,long roomId,long uid) {
        board.rowKeySet().forEach(x -> {
            StringJoiner sj = new StringJoiner(" ").add("gType:"+gameType).add("rType:"+roomType.getNumber()).add("rId:"+roomId).add("uid:"+uid);
            board.row(x).forEach((y, m) -> {
                sj.add(m.getK() + "=x" + x + "y" + y+ "t:"+m.getChangeType()+"g:"+(m.isGold()?"t":"f"));
            });
            log.info(sj.toString());
        });
    }

    public List<Game.Spot> allToModelTable(Table<Integer, Integer, SlotModel> board) {
        List<Game.Spot> list = new ArrayList<>();
        board.values().forEach(e -> {
            if (e != null) {
                list.add(Game.Spot.newBuilder().setSymbol(e.getK())
                        .setX(e.getX()).setY(e.getY())
                        .setGold(e.isGold()).setChangeType(e.getChangeType()).build());
            }
        });
        return list;
    }

    public SlotModel toModel(Slot s,int x,int y) {
       return SlotModel.builder().k(s.getK()).x(x).y(y).gold(s.isGold()).baida(s.isBaida())
                .bonus(s.isBonus()).only(s.isOnly()).scatter(s.isScatter()).quit(s.isQuit()).build();
    }

}

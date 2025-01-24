package com.z.core.service.game.slot;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.ai.LowState;
import com.z.core.ai.SpecialState;
import com.z.core.ai.SuperState;
import com.z.core.ai.clear.ClearHighState;
import com.z.core.ai.clear.ClearLowState;
import com.z.core.ai.clear.ClearMidState;
import com.z.core.ai.fish.FishState;
import com.z.core.ai.fish.HighState;
import com.z.core.ai.fish.MidState;
import com.z.model.BetParam;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.mysql.cfg.CSlot;
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

    Table<CommonGame.GameType,SlotState, SuperState>  table= HashBasedTable.create();

    SlotCommon() {
        init();
    }

    public void init() {
        //默认
        table.put(CommonGame.GameType.GAME_DEFUALT,SlotState.LOW_BET,new LowState(SlotState.LOW_BET));
        table.put(CommonGame.GameType.GAME_DEFUALT,SlotState.MEDIUM_BET,new MidState(SlotState.MEDIUM_BET));
        table.put(CommonGame.GameType.GAME_DEFUALT,SlotState.HIGH_BET,new HighState(SlotState.HIGH_BET));
        table.put(CommonGame.GameType.GAME_DEFUALT,SlotState.SPECIAL_BET,new SpecialState(SlotState.SPECIAL_BET));

        //捕鱼
        table.put(CommonGame.GameType.FISH,SlotState.LOW_BET,new com.z.core.ai.fish.LowState(SlotState.LOW_BET));
        table.put(CommonGame.GameType.FISH,SlotState.MEDIUM_BET,new com.z.core.ai.fish.MidState(SlotState.MEDIUM_BET));
        table.put(CommonGame.GameType.FISH,SlotState.HIGH_BET,new com.z.core.ai.fish.HighState(SlotState.HIGH_BET));
        table.put(CommonGame.GameType.FISH,SlotState.SPECIAL_BET,new com.z.core.ai.fish.SpecialState(SlotState.SPECIAL_BET));

        //麻将2
        table.put(CommonGame.GameType.MAJIANG_2,SlotState.LOW_BET,new ClearLowState(SlotState.LOW_BET));
        table.put(CommonGame.GameType.MAJIANG_2,SlotState.MEDIUM_BET,new ClearMidState(SlotState.MEDIUM_BET));
        table.put(CommonGame.GameType.MAJIANG_2,SlotState.HIGH_BET,new ClearHighState(SlotState.HIGH_BET));
        table.put(CommonGame.GameType.MAJIANG_2,SlotState.SPECIAL_BET,new com.z.core.ai.clear.SpecialState(SlotState.SPECIAL_BET));


        //冰球
        table.put(CommonGame.GameType.BINGQIUTUPO,SlotState.LOW_BET,new ClearLowState(SlotState.LOW_BET));
        table.put(CommonGame.GameType.BINGQIUTUPO,SlotState.MEDIUM_BET,new ClearMidState(SlotState.MEDIUM_BET));
        table.put(CommonGame.GameType.BINGQIUTUPO,SlotState.HIGH_BET,new ClearHighState(SlotState.HIGH_BET));
        table.put(CommonGame.GameType.BINGQIUTUPO,SlotState.SPECIAL_BET,new com.z.core.ai.clear.SpecialState(SlotState.SPECIAL_BET));
    }


    public SuperState getSuperState(CommonGame.GameType gameType,SlotState slotState) {
        SuperState state =  table.get(gameType,slotState);
        if(state == null){
            state = table.get(CommonGame.GameType.GAME_DEFUALT,slotState);
        }
        return state;

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
        SuperState betState = getSuperState(gameType,SlotState.getBetState(param.getState()));
        if(gameType == CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER || gameType == CommonGame.GameType.SHUIHUZHUAN_HIGHER){
            return randomHigher(gameType,slots,goals,param);
        }
        return betState.random(gameType, board, slots, goals, param);
    }
    public Slot randomHigher(CommonGame.GameType gameType,Map<Integer, Slot> slots, Collection<Integer> goals, BetParam param) {
        SuperState betState =  getSuperState(gameType,SlotState.SPECIAL_BET);
        return betState.randomHigher(gameType,slots,goals,param);
    }
    public void print(Table<Integer, Integer, SlotModel> board, CommonGame.GameType gameType, CommonGame.RoomType roomType,long roomId,long uid) {
        board.rowKeySet().forEach(x -> {
            StringJoiner sj = new StringJoiner(" ").add("gType:"+gameType).add("rType:"+roomType).add("rId:"+roomId).add("uid:"+uid);
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

    public SlotModel toModel(CSlot s, int x, int y) {
        return SlotModel.builder().k(s.getSymbol()).x(x).y(y).baida(s.isBaida())
                .bonus(s.isBonus()).only(s.isOnly()).scatter(s.isScatter()).quit(s.isQuit()).build();
    }


    /**
     * 判断是否成功捕获
     * @param fish 鱼的类型
     * @param bullet 炮弹的类型
     * @return 是否捕获成功
     */
    public boolean isCaught(BetParam param,int fishType,double fish, double bullet) {
        FishState betState =(FishState) table.get(CommonGame.GameType.FISH,SlotState.getBetState(param.getState()));// 计算最终概率
        return betState.catchFish(param,fishType,fish*1.0d/10000,bullet*1.0d/10000);
    }

}

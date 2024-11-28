package com.z.core.service.game.card;

import com.z.core.player.Player;
import com.z.model.proto.CommonGame;
import com.z.model.type.PlayerState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 棋牌玩家
 */
public class CardPlayer extends Player {
    private PlayerState playerState;
    private Map<CommonGame.CardSuit,Long> betMap = new ConcurrentHashMap<>();
    private long gold;//输赢金额
    public CardPlayer(long uid, int chips,boolean banker,boolean robot) {
        super(uid, chips,banker,robot);
        this.playerState = PlayerState.READY;
    }

    public void placeBet(CommonGame.CardSuit suit, long amount) {
       betMap.put(suit,betMap.getOrDefault(suit,0L)+amount);
    }
    public void reset(){
        this.betMap.clear();
        this.gold=0;
        this.playerState = PlayerState.READY;
    }
    @Override
    public int calculateHandValue() {
        // 实现手牌值的计算逻辑，返回“牛几”结果
        // ...
        return 0; // 示例
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public Map<CommonGame.CardSuit, Long> getBetMap() {
        return betMap;
    }

    public long getGold() {
        return gold;
    }
}

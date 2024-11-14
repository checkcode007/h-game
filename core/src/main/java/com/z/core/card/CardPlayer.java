package com.z.core.card;

import com.z.core.player.Player;
import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;
/**
 * 棋牌玩家
 */
public class CardPlayer extends Player {
    private List<CommonGame.Card> hand; // 手中的牌

    public CardPlayer(long uid, int chips) {
        super(uid, chips);
        this.hand = new ArrayList<>();
    }

    public void receiveCard(CommonGame.Card card) {
        hand.add(card);
    }
    @Override
    public int calculateHandValue() {
        // 实现手牌值的计算逻辑，返回“牛几”结果
        // ...
        return 0; // 示例
    }
    public List<CommonGame.Card> getHand() {
        return hand;
    }

}

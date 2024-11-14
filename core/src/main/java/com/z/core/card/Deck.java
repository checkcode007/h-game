package com.z.core.card;

import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 牌组结构
 */
public class Deck {
    static final int NUM=13;
    List<CommonGame.Card> cards = new ArrayList<>();
    public Deck() {
        cards = new ArrayList<>();

        for (CommonGame.CardSuit suit : CommonGame.CardSuit.values()) {
            if(CommonGame.CardSuit.BigJoker == suit) continue;
            if(CommonGame.CardSuit.SmallJoker == suit) continue;
            for (int i = 1; i <= NUM; i++) {
                CommonGame.Card card = CommonGame.Card.newBuilder().setId(i).setSuit(suit).build();
                cards.add(card);
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards); // 洗牌
    }

    public CommonGame.Card dealCard() {
        return cards.remove(cards.size() - 1); // 发一张牌
    }

}

package com.z.core.card;

import com.z.model.type.card.CardGameState;

import java.util.ArrayList;
import java.util.List;

public class CardGame {
    private Deck deck;
    private CardPlayer banker; // 庄家
    private List<CardPlayer> players; // 玩家列表
    private CardGameState state; // 当前游戏状态

    public CardGame(CardPlayer banker) {
        this.banker = banker;
        this.players = new ArrayList<>();
        this.deck = new Deck();
    }

    public void addPlayer(CardPlayer player) {
        players.add(player);
    }

    public void startGame() {
        deck.shuffle(); // 洗牌
        dealCards(); // 发牌
        calculateResults(); // 计算结果
    }

    private void dealCards() {
        for (CardPlayer player : players) {
            for (int i = 0; i < 5; i++) {
                player.receiveCard(deck.dealCard());
            }
        }
        for (int i = 0; i < 5; i++) {
            banker.receiveCard(deck.dealCard());
        }
    }

    private void calculateResults() {
        int bankerValue = banker.calculateHandValue();
        for (CardPlayer player : players) {
            int playerValue = player.calculateHandValue();
            if (playerValue > bankerValue) {
                // 玩家赢得筹码
                int winnings = player.getBetAmount() * getMultiplier(playerValue);
                player.setChips(player.getChips() + winnings);
            }
            // 处理庄家赢的情况
            // ...
        }
    }

    private int getMultiplier(int playerValue) {
        // 根据牌型返回不同的赔率倍数
        // ...
        return 1; // 示例
    }

    // getter & setter
}

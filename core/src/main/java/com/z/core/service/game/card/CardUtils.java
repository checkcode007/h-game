package com.z.core.service.game.card;

import com.z.model.bo.card.CardNN;
import com.z.model.bo.card.NiuResult;
import com.z.model.proto.CommonGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardUtils {
    private static final Logger logger = LogManager.getLogger(CardUtils.class);
    /**
     * 是否有相同的点数
     * 将点数排序，然后比较相邻的元素是否相同。如果相同，就表示存在相同点数的牌
     *
     * @param points
     * @return
     */
    public static boolean hasSamePoint(List<Integer> points) {
        Collections.sort(points);
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).equals(points.get(i - 1))) {
                return true; // 表示有相同点数
            }
        }
        return false; // 没有相同点数
    }

    /**
     * 计算手牌的牛几值
     *
     * @param cards 玩家手牌（5张Card对象）
     * @return NiuResult 计算结果，包括牌型和具体牌组
     */
    public static NiuResult calculateNiuNiu(List<CardNN> cards) {
        if (cards == null || cards.size() != 5) {
            throw new IllegalArgumentException("手牌必须是5张！");
        }
        // 遍历所有三张牌组合
        for (int i = 0; i < cards.size() - 2; i++) {
            for (int j = i + 1; j < cards.size() - 1; j++) {
                for (int k = j + 1; k < cards.size(); k++) {
                    int sumThree = cards.get(i).getValue() + cards.get(j).getValue() + cards.get(k).getValue();
                    if (sumThree % 10 == 0) {
                        // 找到符合条件的三张牌，计算剩余两张牌点数和
                        List<CardNN> threeCards = new ArrayList<>();
                        threeCards.add(cards.get(i));
                        threeCards.add(cards.get(j));
                        threeCards.add(cards.get(k));

                        List<CardNN> remainingCards = new ArrayList<>();
                        for (int m = 0; m < cards.size(); m++) {
                            if (m != i && m != j && m != k) {
                                remainingCards.add(cards.get(m));
                            }
                        }

                        int remainingSum = remainingCards.stream().mapToInt(CardNN::getValue).sum();
                        int niuValue = remainingSum % 10;

                        // 使用枚举类型返回结果
                        CommonGame.NiuType niuType = CommonGame.NiuType.forNumber(niuValue == 0 ? 10 : niuValue);
                        return new NiuResult(niuType, threeCards, remainingCards);
                    }
                }
            }
        }

        // 无牛的情况
        return new NiuResult(CommonGame.NiuType.NIU_ZERO, null, null);
    }

    public static void main1(String[] args) {
        // 示例测试
        List<CardNN> hand1 = List.of(
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Spades).setId(10).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Hearts).setId(10).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Diamonds).setId(1).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Clubs).setId(9).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Hearts).setId(10).build())
        );

        List<CardNN> hand2 = List.of(
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Spades).setId(1).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Hearts).setId(2).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Diamonds).setId(3).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Clubs).setId(4).build()),
                new CardNN(CommonGame.Card.newBuilder().setSuit(CommonGame.CardSuit.Hearts).setId(5).build())
        );
        NiuResult h1 = calculateNiuNiu(hand1);
        NiuResult h2 = calculateNiuNiu(hand2);
        System.out.println(h1); // 输出: 牛九
        System.out.println(h2); // 输出: 牛四

        System.out.println("hand1--->"+h1.getType()+"-->"+h1.getType().getNumber());
        System.out.println("hand2--->"+h2.getType()+"-->"+h2.getType().getNumber());
        System.out.println(h1.getType().getNumber()>h2.getType().getNumber());
    }

}

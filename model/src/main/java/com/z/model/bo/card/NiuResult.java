package com.z.model.bo.card;

import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.List;

@Data
public class NiuResult {
    private CommonGame.NiuType type;           // 结果，如 "牛九", "无牛", "牛牛"
    private List<CardNN> threeCards;   // 构成10倍数的三张牌
    private List<CardNN> remainingCards; // 剩余的两张牌

    public NiuResult(CommonGame.NiuType type, List<CardNN> threeCards, List<CardNN> remainingCards) {
        this.type = type;
        this.threeCards = threeCards;
        this.remainingCards = remainingCards;
    }

    @Override
    public String toString() {
        return "结果: " + type +
                ", 三张牌: " + threeCards +
                ", 剩余牌: " + remainingCards;
    }
}

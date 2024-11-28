package com.z.model.bo.card;


import com.z.model.proto.CommonGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardNN {
    CommonGame.Card card;

    public int getValue() {
        return Math.min(card.getId(), 10); // J/Q/K 计作 10，其他按点数
    }
}

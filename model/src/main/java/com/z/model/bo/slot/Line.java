package com.z.model.bo.slot;

import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Line {
    /**
     * 游戏类型
     * @see CommonGame.GameType
     */
    CommonGame.GameType type;
    /**
     * 下标
     */
    int lineId;

    List<SlotModel> points;//坐标

    int k;
    int c;
    int rate;

    public Line(CommonGame.GameType type, int lineId, int k, int c, int rate) {
        this.type = type;
        this.lineId = lineId;
        this.points = new ArrayList<>();
        this.k = k;
        this.c = c;
        this.rate = rate;
    }

    public void addPoints(List<SlotModel> points) {
        this.points.addAll(points);
    }

}

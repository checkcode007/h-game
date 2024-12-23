package com.z.model.bo.slot;

import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Payline {

    int id;
    /**
     * 游戏类型
     * @see com.z.model.proto.CommonGame.GameType
     */
    CommonGame.GameType type;
    /**
     * 下标
     */
    int lineId;

    List<Point> points;//坐标

    public Payline(int id, CommonGame.GameType type, int lineId) {
        this.id = id;
        this.type = type;
        this.lineId = lineId;
        this.points = new ArrayList<>();
    }

    public void addPoints(List<Point> points) {
        this.points.addAll(points);
    }

}

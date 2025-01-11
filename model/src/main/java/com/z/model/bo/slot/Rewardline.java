package com.z.model.bo.slot;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Rewardline {
    /**
     * 符号
     * @see com.z.model.proto.CommonGame.Mali
     *
     */
    int k;
    boolean baida;
    /**
     * 下标
     */
    int lineId;

    List<Point> points;//坐标
    /**
     * 倍率
     */
    int rate = 0;
    /**
     * 获取的金币
     */
    long gold = 0;
    /**
     * 特殊连续符号的个数
     */
    int specialC=0;

    boolean hadBaida = false;


    public Rewardline(int k, int lineId) {
        this.k = k;
        this.lineId = lineId;
        this.points = new ArrayList<>();
    }

    public void addPoints(List<Point> points) {
        this.points.addAll(points);
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }
    public void addSpecicalC() {
        this.specialC++;
    }
    public void addSpecicalC(int specialC) {
        this.specialC+=specialC;
    }
    @Override
    public String toString() {
        return "line{" +
                "k=" + k +
                ", line=" + lineId +
                ", points=" + points +
                '}';
    }
}

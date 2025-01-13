package com.z.core;

public class RankScore {
    private double score;
    private double diamond;
    private double pt;
    private double pay;

    public RankScore(double score, double diamond, double pt, double pay) {
        this.score = score;
        this.diamond = diamond;
        this.pt = pt;
        this.pay = pay;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getDiamond() {
        return diamond;
    }

    public void setDiamond(double diamond) {
        this.diamond = diamond;
    }

    public double getPt() {
        return pt;
    }

    public void setPt(double pt) {
        this.pt = pt;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    @Override
    public String toString() {
        return "RankScore{" +
                "score=" + score +
                ", diamond=" + diamond +
                ", pt=" + pt +
                ", pay=" + pay +
                '}';
    }
}

package com.z.model.bo.majiang;

import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MjGoalTest {

    CommonGame.MJ mj;
    //连接列个数
    int c;
    int rate;
    List<MJBo> points;

    public MjGoalTest(CommonGame.MJ mj, int c, int rate) {
        this.mj = mj;
        this.c = c;
        this.rate = rate;
        this.points = new ArrayList<>();
    }

    public void addC(){
        this.c++;
    }
    public void addPoint(MJBo point){
        this.points.add(point);
    }
    public void addPoint(List<MJBo> points){
        this.points.addAll(points);
    }

    @Override
    public String toString() {
        return "Bo{" +
                "mj=" + mj.getNumber() +
                ", c=" + c +
                ", r=" + rate +
                ", p=" + points.toString() +
                '}';
    }
}

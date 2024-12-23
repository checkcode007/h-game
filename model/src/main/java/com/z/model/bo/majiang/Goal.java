package com.z.model.bo.majiang;

import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Data
public class Goal {

    CommonGame.MJ mj;
    /**
     * 连接列个数
     */
    int c=1;
    /**
     * 倍率
     */
    int rate;
    /**
     * 免费次数
     */
    int free;
    List<SlotModel> points;
    public Goal(CommonGame.MJ mj, int c, int rate) {
        this.mj = mj;
        this.c = c;
        this.rate = rate;
        this.points = new ArrayList<>();
    }
    public Goal(CommonGame.MJ mj, int c, int rate,int free) {
        this.mj = mj;
        this.c = c;
        this.rate = rate;
        this.points = new ArrayList<>();
        this.free = free;
    }

    public void addC(){
        this.c++;
    }
    public void addPoint(List<SlotModel> points){
        this.points.addAll(points);
    }

    public void addPoint(SlotModel p){
        this.points.add(p);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" ");
        for (SlotModel p : points) {
            sj.add(p.getType()+"x"+p.getX()+"y"+p.getY());
        }
        return "goal{" + "c=" + c + ", r=" + rate + ", p=" + sj+ '}';
    }
}

package com.z.model.bo.majiang;

import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Data
public class MjGoal {

    CommonGame.MJ mj;
    /**
     * 连接列个数
     */
    int c=1;
    /**
     * 倍率
     */
    int rate;

    List<SlotModel<CommonGame.MJ>> points;

    public MjGoal(CommonGame.MJ mj, int c, int rate) {
        this.mj = mj;
        this.c = c;
        this.rate = rate;
        this.points = new ArrayList<>();
    }

    public void addC(){
        this.c++;
    }
    public void addPoint(List<SlotModel<CommonGame.MJ>> points){
        this.points.addAll(points);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" ");
        for (SlotModel<CommonGame.MJ> p : points) {
            sj.add(p.getType().getNumber()+"x"+p.getY()+"y"+p.getY());
        }
        return "goal{" + "c=" + c + ", r=" + rate + ", p=" + sj+ '}';
    }
}

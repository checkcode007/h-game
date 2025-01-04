package com.z.model.bo.slot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每一个slot对象
 * @param
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotModel{
    int type; //符号
    int x; // 第几列
    int y; //列的第几个
    boolean gold;//是否是金色牌
    int changeType;//变化的类型
    int wildIndex;//运动员划过的线
    boolean baida;
    boolean only;//每轴只能有一个
    boolean bonus;
    boolean scatter;
    boolean quit;//是否是退出图标
    /**
     * 个数
     */
    int c;

    public void  addC(int c){
        this.c+=c;
    }

    @Override
    public String toString() {
        return "m{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", g=" + (gold?1:0) +
                ", cType=" + changeType +
                ", wi=" + wildIndex +
                ", c=" + c +
                '}';
    }
}

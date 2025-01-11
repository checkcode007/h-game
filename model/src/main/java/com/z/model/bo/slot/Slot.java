package com.z.model.bo.slot;

import com.z.model.type.PosType;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Slot {
    int k;//符号的number
    int c1;//普通出现的次数
    int c2;//金色普通出现的次数
    int rate1; // 倍率最小倍率
    int rate2; // 倍率最大倍率
    int free; //免费次数
    int w1;//权重1
    int w2;//权重2
    boolean gold;//是否是金色
    int w;
    PosType posType;
    Set<Integer> pos = new HashSet<>();
    boolean baida;
    boolean only;//每轴只能有一个
    boolean bonus;
    boolean scatter;
    boolean quit;//是否是退出图标


    public Slot(int w) {
        this.w = w;
    }

    public void addC1(){
        this.c1++;
    }
    public void addC2(){
        this.c2++;
    }
    public void addW1(int w1) {
        this.w1 += w1;
        this.w1 = Math.max(this.w1, w*2);
    }
    public  void subW1(int w1) {
        this.w1 -= w1;
        this.w1 = Math.max(w1, w/10);
    }
    public void addW2(int w2) {
        this.w2 += w2;
        if(this.w2>10000){
            this.w2=10000;
        }
    }

    public  void subW2(int w2) {
        this.w2 -= w2;
        if (w2 < 10) {
            this.w2 = 10;
        }
    }
    public void addPos(int pos) {
        this.pos.add(pos);
    }
    public boolean containsPos(int pos) {
        return this.pos.contains(pos);
    }
 }

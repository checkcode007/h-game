package com.z.model.bo;

import lombok.Data;

@Data
public class Slot {
    int k;//符号的number
    int c1;//普通出现的次数
    int c2;//金色普通出现的次数
    int rate; // 倍率
    int free; //免费次数
    int w1;//权重1
    int w2;//权重2
    boolean gold;//是否是金色

    public void addC1(){
        this.c1++;
    }
    public void addC2(){
        this.c2++;
    }
    public void addW1(int w1) {
        this.w1 += w1;
        if (this.w1 > 1000000) {
            this.w1 = 1000000;
        }
    }
    public  void subW1(int w1) {
        this.w1 -= w1;
        if (w1 < 100) {
            this.w1 = 100;
        }
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
 }

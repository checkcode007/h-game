package com.z.model.bo.mali;

/**
 * 支付线
 */
public class Payline {
    int index;
    int[] pos;  // 每个滚轮在支付线上的位置

    public Payline(int index,int[] pos) {
        this.index = index;
        this.pos = pos;
    }

    public int getPos(int i) {
        return this.pos[i];
    }

    public int getIndex() {
        return index;
    }

    public int[] getPos() {
        return pos;
    }
}

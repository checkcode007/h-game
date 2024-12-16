package com.z.model.bo.mali;

import com.z.model.proto.CommonGame;

/**
 * 中奖结果
 */
public class WinOneLine {
    int index;//中奖线下标
    CommonGame.Symbol symbol;//中奖的图标
    int c;//中奖的个数
    int rate;

    public WinOneLine(int index, CommonGame.Symbol symbol, int c) {
        this.index = index;
        this.symbol = symbol;
        this.c = c;
    }

    public int getIndex() {
        return index;
    }

    public CommonGame.Symbol getSymbol() {
        return symbol;
    }

    public int getC() {
        return c;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}

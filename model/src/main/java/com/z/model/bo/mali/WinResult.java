package com.z.model.bo.mali;

import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;

/**
 * 中奖结果
 */
public class WinResult {
    /**
     * 五个轮子上显示的符号
     */
    List<List<CommonGame.Symbol>> reels = new ArrayList<>();
    List<WinOneLine> wins= new ArrayList<>(6);

    public WinResult(List<List<CommonGame.Symbol>> reels) {
        this.reels = reels;
    }

    public void addWin(WinOneLine winOneLine) {

        wins.add(winOneLine);
    }

    public List<WinOneLine> getWins() {
        return wins;
    }

    public List<List<CommonGame.Symbol>> getReels() {
        return reels;
    }
}

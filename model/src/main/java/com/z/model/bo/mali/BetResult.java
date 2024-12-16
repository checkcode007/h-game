package com.z.model.bo.mali;

import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BetResult {
    long gold;//中奖的金额
    int rate;//中奖后的倍率
    /**
     * 五个轮子上显示的符号
     */
    List<List<CommonGame.Symbol>> reels = new ArrayList<>();
    List<WinOneLine> wins= new ArrayList<>(6);
}

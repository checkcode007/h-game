package com.z.model.bo.mali.test;

import java.util.List;

/**
 * 支付线
 */
public class PaylineTest {
    List<Integer> reelPositions;  // 每个滚轮在支付线上的位置

    PaylineTest(List<Integer> reelPositions) {
        this.reelPositions = reelPositions;
    }

    public List<Integer> getReelPositions() {
        return reelPositions;
    }
}

package com.z.model.bo.mali.test;

import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;

// 定义游戏状态（GameState）
public class GameStateTest {
    List<ReelTest> reelTests;              // 游戏中的滚轮
    List<List<Integer>> paylines; // 支付线定义
    int balance;                   // 玩家余额
    int betAmount;                 // 当前下注金额
    int REEL_SIZE = 5;           //滚轮个数
    int SYMBOL_SIZE = 3;         //每个滚轮上显示的个数

    GameStateTest(List<ReelTest> reelTests, List<List<Integer>> paylines, int balance, int betAmount) {
        this.reelTests = reelTests;
        this.paylines = paylines;
        this.balance = balance;
        this.betAmount = betAmount;
        REEL_SIZE = reelTests.size();
    }

    // 模拟旋转，返回每个滚轮的3个符号
    public List<List<CommonGame.Symbol>> spinReels() {
        List<List<CommonGame.Symbol>> reelDisplays = new ArrayList<>();
        for (ReelTest reelTest : reelTests) {
            List<CommonGame.Symbol> list = new ArrayList<>(REEL_SIZE);
            for (int i = 0; i < SYMBOL_SIZE; i++) {
                list.add(reelTest.spin());
            }
            reelDisplays.add(list);
        }
        return reelDisplays;
    }

    // 检查支付线是否中奖
    public void checkPaylines(List<List<CommonGame.Symbol>> reelDisplays) {
        for (int i = 0; i < paylines.size(); i++) {
            List<Integer> payline = paylines.get(i);
            List<CommonGame.Symbol> lineSymbols = new ArrayList<>();
            for (int j = 0; j < payline.size(); j++) {
                lineSymbols.add(reelDisplays.get(j).get(payline.get(j))); // 根据支付线位置取符号
            }

            if (isWinningLine(lineSymbols)) {
                System.out.println("Payline " + (i + 1) + " wins with symbols: " + lineSymbols);
            }
        }

    }

    private boolean isWinningLine(List<CommonGame.Symbol> lineSymbols) {
        CommonGame.Symbol first = lineSymbols.get(0);
        int index = 0;
        for (CommonGame.Symbol symbol : lineSymbols) {
            if (symbol != first) {
                return false;
            }
            if(++index>2){
                return true;
            }
        }
        return true;
    }

//    // 比较支付线并打印出最大奖励
//    private void comparePaylines(List<CBaibianMali> spinResults) {
////        int maxPayout = 0;
//        Payline bestPayline = null;
//
//        for (Payline payline : paylines) {
//            int linePayout = 0;
//            // 遍历每个滚轮位置并计算该支付线的奖励
//            for (int i = 0; i < payline.reelPositions.size(); i++) {
//                int reelIndex = payline.reelPositions.get(i);
//                CBaibianMali symbol = spinResults.get(reelIndex);
//                linePayout += symbol.getRadio();
//            }
//
////            // 如果当前支付线的奖励更大，更新最大奖励
////            if (linePayout > maxPayout) {
////                maxPayout = linePayout;
////                bestPayline = payline;
////            }
//        }
//
//        // 输出最大奖励支付线
//        if (bestPayline != null) {
//            System.out.println("Best Payline: " + bestPayline.reelPositions);
////            System.out.println("Max Payout: " + maxPayout);
//        } else {
//            System.out.println("No winning paylines.");
//        }
//    }
}

package com.z.model.bo.mali;

import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;

// 定义游戏状态（GameState）
public class GameStateTest {
    List<Reel> reelTests;              // 游戏中的滚轮
    List<Payline> paylines; // 支付线定义
    int balance;                   // 玩家余额
    int betAmount;                 // 当前下注金额
    int REEL_SIZE = 5;           //滚轮个数
    int SYMBOL_SIZE = 3;         //每个滚轮上显示的个数

    GameStateTest(List<Reel> reelTests, List<Payline> paylines, int balance, int betAmount) {
        this.reelTests = reelTests;
        this.paylines = paylines;
        this.balance = balance;
        this.betAmount = betAmount;
        REEL_SIZE = reelTests.size();
    }

    // 模拟旋转，返回每个滚轮的3个符号
    public List<List<CommonGame.Symbol>> spinReels() {
        List<List<CommonGame.Symbol>> reelDisplays = new ArrayList<>();
        for (Reel reelTest : reelTests) {
            List<CommonGame.Symbol> list = new ArrayList<>(REEL_SIZE);
            for (int i = 0; i < SYMBOL_SIZE; i++) {
                list.add(reelTest.spin());
            }
            reelDisplays.add(list);
        }
        return reelDisplays;
    }

    // 检查支付线是否中奖
    public WinResult checkPaylines(List<List<CommonGame.Symbol>> reelDisplays) {
        WinResult ret = new WinResult(reelDisplays);
        for (Payline payline: paylines) {
            int[] line = payline.getPos();
            List<CommonGame.Symbol> lineSymbols = new ArrayList<>();
            for (int j = 0; j < line.length; j++) {
                lineSymbols.add(reelDisplays.get(j).get(line[j])); // 根据支付线位置取符号
            }
            System.err.println("---->"+lineSymbols);
            int winC = lineWinCount(lineSymbols);
            if(winC<2){
                continue;
            }
            CommonGame.Symbol symbol = lineSymbols.get(0);
            WinOneLine oneLine =new WinOneLine(payline.getIndex(),symbol,winC);
            ret.addWin(oneLine);
        }
        return ret;

    }

    /**
     * 每条线上中奖的个数
     * @param lineSymbols
     * @return
     */
    private int lineWinCount(List<CommonGame.Symbol> lineSymbols) {
        CommonGame.Symbol first = lineSymbols.get(0);
        int index = 0;
        for (CommonGame.Symbol symbol : lineSymbols) {
            if (symbol != first) {
                return index;
            }
            index++;
        }
        return index;
    }
}

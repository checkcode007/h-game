package com.z.model.bo.mali;

import cn.hutool.json.JSONUtil;
import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;

public class SlotMachineDemo {

    public static void main(String[] args) {
        // 创建5个滚轮，每个滚轮有固定的符号池
        List<CommonGame.Symbol> list = new ArrayList();
        for (CommonGame.Symbol value : CommonGame.Symbol.values()) {
            if(value == CommonGame.Symbol.UNRECOGNIZED) continue;
            if(value.getNumber()<11) continue;
            list.add(value);
        }
        List<Reel> reelTests = List.of(
                new Reel(list),
                new Reel(list),
                new Reel(list),
                new Reel(list),
                new Reel(list)
        );

        List<Payline> paylines = initLines();

        // 初始化游戏状态
        GameStateTest gameStateTest = new GameStateTest(reelTests, paylines,10000,10);

        // 模拟旋转
        List<List<CommonGame.Symbol>> reelDisplays = gameStateTest.spinReels();

        // 打印滚轮结果
        for (int i = 0; i < reelDisplays.size(); i++) {
            System.out.println("Reel " + (i + 1) + ": " + reelDisplays.get(i));
        }

        // 检查支付线
        WinResult winResult = gameStateTest.checkPaylines(reelDisplays);
        for (WinOneLine win : winResult.getWins()) {
            System.err.println(JSONUtil.toJsonStr(win));
        }
    }


    public static List<Payline> initLines(){

        int[] line1 = {0, 0, 0, 0, 0};
        int[] line2 = {1, 1, 1, 1, 1};
        int[] line3 = {2, 2, 2, 2, 2};

        int[] line4 = {0, 1, 2, 1, 0};
        int[] line5 = {2, 1, 0, 1, 2};

        int[] line6 = {0, 0, 1, 2, 2};
        int[] line7 = {2, 2, 1, 0, 0};

        int[] line8 = {1, 0, 1, 2, 1};
        int[] line9 = {1, 2, 1, 0, 1};
        List<Payline> paylines = new ArrayList<>();
        paylines.add(new Payline(1,line1));
        paylines.add(new Payline(2,line2));
        paylines.add(new Payline(3,line3));
        paylines.add(new Payline(4,line4));
        paylines.add(new Payline(5,line5));
        paylines.add(new Payline(6,line6));
        paylines.add(new Payline(7,line7));
        paylines.add(new Payline(8,line8));
        paylines.add(new Payline(9,line9));
        return paylines;
    }
}

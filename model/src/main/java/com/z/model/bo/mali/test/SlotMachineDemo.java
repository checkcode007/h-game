package com.z.model.bo.mali.test;

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
        List<ReelTest> reelTests = List.of(
                new ReelTest(list),
                new ReelTest(list),
                new ReelTest(list),
                new ReelTest(list),
                new ReelTest(list)
        );

        // 定义5条支付线
        List<List<Integer>> paylines = List.of(
                List.of(0, 0, 0, 0, 0),  // 第一行
                List.of(1, 1, 1, 1, 1),  // 第二行
                List.of(2, 2, 2, 2, 2),  // 第三行
                List.of(0, 1, 2, 1, 0),  // 对角线1
                List.of(2, 1, 0, 1, 2)   // 对角线2
        );

        // 初始化游戏状态
        GameStateTest gameStateTest = new GameStateTest(reelTests, paylines,10000,10);

        // 模拟旋转
        List<List<CommonGame.Symbol>> reelDisplays = gameStateTest.spinReels();

        // 打印滚轮结果
        for (int i = 0; i < reelDisplays.size(); i++) {
            System.out.println("Reel " + (i + 1) + ": " + reelDisplays.get(i));
        }

        // 检查支付线
        gameStateTest.checkPaylines(reelDisplays);
    }
}

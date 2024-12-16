package com.z.model.bo.mali.test;

import com.z.model.proto.CommonGame;

import java.util.List;
import java.util.Random;

/**
 * 定义滚轮，每个滚轮上的符号
 */
public class ReelTest {
    List<CommonGame.Symbol> symbols; // 符号列表
    Random random = new Random();
    int size = 0;
    public ReelTest(List<CommonGame.Symbol> symbols) {
        this.symbols = symbols;
        this.size  = symbols.size();
    }
    public CommonGame.Symbol spin(){
        return symbols.get(random.nextInt(size));
    }
    // 模拟滚轮旋转并返回显示的3个符号
    public List<CommonGame.Symbol> spinThree() {
        int i1 = random.nextInt(size);
        int i2 = random.nextInt(size);
        int i3 = random.nextInt(size);
        return List.of(
                symbols.get(i1 % size),       // 第1个符号
                symbols.get(i2 % size),       // 第2个符号
                symbols.get(i3 % size)        // 第3个符号
        );
    }
}

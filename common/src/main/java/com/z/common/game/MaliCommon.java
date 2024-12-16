package com.z.common.game;

import com.z.model.bo.mali.Payline;
import com.z.model.bo.mali.Reel;
import com.z.model.proto.CommonGame;

import java.util.ArrayList;
import java.util.List;

/**
 * 百变玛丽
 * 通用变量
 */
public enum MaliCommon {
    ins;
    /**
     * 轮子个数
     */
    public static final int REEL_SIZE = 5;
    /**
     * 每个轮子显示个数
     */
    public static final int SYMBOL_SIZE = 3;
    List<Reel> reels;
    List<Payline> paylines = new ArrayList<>();
    List<CommonGame.Symbol> symbols = new ArrayList();
    MaliCommon() {
        init();
    }
    public void init(){
        initSymbol();
        initReels();
        initLines();
    }
    public void initSymbol(){
        symbols.clear();
        for (CommonGame.Symbol value : CommonGame.Symbol.values()) {
            if (value == CommonGame.Symbol.UNRECOGNIZED) continue;
            if (value.getNumber() < 8) continue;
            symbols.add(value);
        }
    }

    public void initLines(){
        int[] line1 = {0, 0, 0, 0, 0};
        int[] line2 = {1, 1, 1, 1, 1};
        int[] line3 = {2, 2, 2, 2, 2};

        int[] line4 = {0, 1, 2, 1, 0};
        int[] line5 = {2, 1, 0, 1, 2};

        int[] line6 = {0, 0, 1, 2, 2};
        int[] line7 = {2, 2, 1, 0, 0};

        int[] line8 = {1, 0, 1, 2, 1};
        int[] line9 = {1, 2, 1, 0, 1};
        paylines.clear();
        paylines.add(new Payline(1,line1));
        paylines.add(new Payline(2,line2));
        paylines.add(new Payline(3,line3));
        paylines.add(new Payline(4,line4));
        paylines.add(new Payline(5,line5));
        paylines.add(new Payline(6,line6));
        paylines.add(new Payline(7,line7));
        paylines.add(new Payline(8,line8));
        paylines.add(new Payline(9,line9));
    }
    public void initReels(){
        // 创建5个滚轮，每个滚轮有固定的符号池
        List<CommonGame.Symbol> list = new ArrayList();
        for (CommonGame.Symbol value : CommonGame.Symbol.values()) {
            if(value == CommonGame.Symbol.UNRECOGNIZED) continue;
            if(value.getNumber()<11) continue;
            list.add(value);
        }
        reels = List.of(
                new Reel(list),
                new Reel(list),
                new Reel(list),
                new Reel(list),
                new Reel(list)
        );
    }



    public List<Payline> getPaylines() {
        return paylines;
    }

    public List<CommonGame.Symbol> getSymbols() {
        return symbols;
    }

    public List<Reel> getReels() {
        return reels;
    }
}

package com.z.model;

import com.z.model.proto.CommonGame;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BetParam {
    public BetParam() {
    }

    public BetParam(CommonGame.GameType gameType, int state, long uid, int x, boolean free, int continueC, long winC, long totalC, int scatter, int bonus, int baida) {
        this.gameType = gameType;
        this.state = state;
        this.uid = uid;
        this.x = x;
        this.free = free;
        this.continueC = continueC;
        this.winC = winC;
        this.totalC = totalC;
        this.scatter = scatter;
        this.bonus = bonus;
        this.baida = baida;
    }
    CommonGame.GameType gameType;
    /**
     * 下注状态
     */
    int state;
    /**
     * 用户id
     */
    long uid;
    /**
     *  第几排
     */
    int x;
    int y;
    /**
     * 是否免费次数
     */
    boolean free;
    /**
     * 免费的次数
     */
    int freeC;
    /**
     * 连续几次
     */
    int continueC;
    /**
     * 赢的总次数
     */
    long winC;
    /**
     * 总下注次数
     */
    long totalC;

    /**
     * 房间里赢的总次数
     */
    long roomWinC;
    /**
     * 房间里总下注次数
     */
    long roomTotalC;

    /**
     * 房间里下注的总金额
     */
    long roomBetGold;
    /**
     * 房间里赢的金额
     */
    long roomWinGold;


    /**
     * scatter 个数
     */
    int scatter;
    /**
     * bonus 个数
     */
    int bonus;
    /**
     * 百搭 个数
     */
    int baida;
    /**
     * 冰球大wild 个数（每次房间内统计）
     */
    int bigWildC;


    public void  addBigWildC() {
        bigWildC++;
    }
    Set<Integer> lineSet = new HashSet<>();
    public void addLine(int type){
        lineSet.add(type);
    }
    public void clearLine(){
        lineSet.clear();
    }


    public void addScatter(){
        this.scatter++;
    }
    public void addBonus(){
        this.bonus++;
    }
    public void addBaida(){
        this.baida++;
    }

}

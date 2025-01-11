package com.z.model;

import com.z.model.proto.CommonGame;
import lombok.Data;

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

package com.z.model.es;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩家数据
 */
@Data
public class EsPlayer implements Serializable {
    private long id;
    private String name;
    //当前筹码
    private double total_chips;
    //胜利次数
    private int total_wins;
    //失败次数
    private int total_losses;
    //胜率
    private double win_rate;
    //今日赢得金额
    private long daily_win;
    //今日输掉金额
    private long daily_loss;

    //当前净输赢
    private long net_win;

    /**
     * @see com.z.model.type.UserAction
     */
    private int action;
    private int day;
    /**
     * 操作时间
     */
    private long t;
    private String d;
}

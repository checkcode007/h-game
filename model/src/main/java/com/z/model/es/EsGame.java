package com.z.model.es;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩家数据
 */
@Data
public class EsGame implements Serializable {
    private String id;
    private long roomId; // 关联房间ID
    private long gameId; // 游戏ID
    private long round; // 轮次
    private long dealerId; // 庄家ID
    private String players;
    private int cout;//下注人数
    private long bet; // 总下注金额
    private int day;
    private long t;
    private String d;


}

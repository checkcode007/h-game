package com.z.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩家数据
 */
@Data
public class GameBo implements Serializable {
    private String id;
    private long gameId; // 游戏ID
    private long roomId; // 关联房间ID
    private long dealerId; // 庄家ID
    private int cout;//下注人数
    private long bet; // 总下注金额


}

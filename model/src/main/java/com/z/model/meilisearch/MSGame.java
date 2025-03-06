package com.z.model.meilisearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MSIndex(uid = "game_log", primaryKey = "id")
public class MSGame implements Serializable {
    @MSFiled(openFilter = true, key = "id", openSort = true)
    private String id;
    @MSFiled(openFilter = true, key = "roomId", openSort = true)
    private long roomId; // 关联房间ID
    @MSFiled(openFilter = true, key = "gameId", openSort = true)
    private long gameId; // 游戏ID
    private long round; // 轮次
    private long dealerId; // 庄家ID
    private String players;
    private int cout;//下注人数
    private long bet; // 总下注金额
    @MSFiled(openFilter = true, key = "day", openSort = true)
    private int day;
    @MSFiled(openFilter = true, key = "t", openSort = true)
    private long t;
    @MSFiled(openFilter = true, key = "d", openSort = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date d;


}

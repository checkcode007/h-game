package com.z.model.mysql.cfg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CRoom {
    private int id; // 房间ID
    private String name; // 房间名称
    /**
     * 房间类型: 1-初级 2-终极 3-高级
     * @see com.z.model.proto.CommonGame.RoomType
     */
    private int type;
    /**
     * 游戏类型
     * @see com.z.model.proto.CommonGame.GameType
     */
    private int gameType;
    private int maxPlayers; // 最大人数
    private long minBalance; // 门槛-最低入场资金
    private int betMin; // 底分（最小下注）
    private int betMax; // 最大下注
    private int base; // 下注的分母
}

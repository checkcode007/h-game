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
    private long minBet; // 底分
    private int betRadio; // 倍率
    private long jackpot; // 奖池金额
}

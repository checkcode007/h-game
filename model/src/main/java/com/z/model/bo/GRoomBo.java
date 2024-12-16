package com.z.model.bo;

import com.z.model.proto.CommonGame;
import com.z.model.type.RoomState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GRoomBo {

    private long id; // 房间ID
    private long uid;
    private int cfgId; // 房间配置id
    private int curPlayers; // 当前人数
    private int maxPlayers;//最大人数
    private long dealerId; // 庄家ID
    private long jackpot; // 奖池金额
    private int betRadio; // 倍率
    private long minBalance; // 门槛-最低入场资金
    private long minBet; // 底分
    CommonGame.GameType gameType;
    CommonGame.RoomType type;
    /**
     * 房间状态
     */
    RoomState state;
}

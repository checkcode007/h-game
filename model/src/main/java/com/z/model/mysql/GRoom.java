package com.z.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GRoom {
    private long id; // 房间ID
    private int cfgId; // 房间配置id
    private int curPlayers; // 当前人数
    private long dealerId; // 庄家ID
    private long jackpot; // 奖池金额
    private int betRadio; // 倍率
    /**
     * 房间状态: 0-空闲 1-进行中
     * @see com.z.model.type.RoomState
     */
    private int state;
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

}

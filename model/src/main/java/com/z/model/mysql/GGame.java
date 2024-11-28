package com.z.model.mysql;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 游戏数据
 */
@Data
public class GGame implements Serializable {
    private long id;
    private Long roomId; // 关联房间ID
    private Long dealerId; // 庄家ID
    private long bet; // 总下注金额
    private long totalGold; // 总支付金额
    /**
     * @see  com.z.model.proto.CommonGame.GameState
     */
    private int state;

    private int day;
    /**
     * 开始时间
     */
    private Date d1;

    /**
     * 结束时间
     */
    private Date d2;

    /**
     * 开始时间
     */
    private Date createTime;

    /**
     * 结束时间
     */
    private Date updateTime;
}

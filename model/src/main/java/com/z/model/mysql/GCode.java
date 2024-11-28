package com.z.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 点卡
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GCode {
    //唯一id
    private long id;
    private long fromId;
    private long targetId;
    private long gold;
    private String code;
    private int state;
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 使用时间
     */
    private Date useTime;
    /**
     * 过期时间
     */
    private Date lastTime;


}

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
public class GCode extends Model {
    //唯一id
    private long fromId;
    private long targetId;
    private long gold;
    private String code;
    private int state;

    /**
     * 使用时间
     */
    private Date useTime;
    /**
     * 过期时间
     */
    private Date lastTime;


}

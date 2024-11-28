package com.z.model.mysql;

import com.z.model.common.ExcludeCreateTime;
import com.z.model.common.ExcludeUpdateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 点卡 赠送日志
 */
@ExcludeCreateTime
@ExcludeUpdateTime
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GCodeSendLog {
    //唯一id
    private long id;
    private long fromId;
    private long targetId;
    private int cout;
    /**
     * 时间
     */
    private Date lastTime;


}

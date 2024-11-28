package com.z.model.mysql;

import com.z.model.common.ExcludeCreateTime;
import com.z.model.common.ExcludeUpdateTime;
import lombok.Data;

import java.util.Date;
@ExcludeUpdateTime
@ExcludeCreateTime
@Data
public class GAgent {
    private long id;
    private long agentId;
    private Date lastTime;
}

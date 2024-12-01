package com.z.model.mysql;

import com.z.model.common.ExcludeCreateTime;
import com.z.model.common.ExcludeUpdateTime;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ExcludeCreateTime
@ExcludeUpdateTime
public class GBankLog extends Model{
    private int type;
    private long uid;
    private long targetId;
    private long transferId;
    private long gold;
    private long gold1;
    private long gold2;
    private boolean state;
    private Date lastTime;

}

package com.z.model.mysql;

import com.z.model.common.ExcludeCreateTime;
import com.z.model.common.ExcludeUpdateTime;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ExcludeCreateTime
@ExcludeUpdateTime
public class GLine9Rank extends Model{
    private Long id;
    private long gold;
    private Date lastTime;
}

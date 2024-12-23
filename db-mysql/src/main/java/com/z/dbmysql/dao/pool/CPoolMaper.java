package com.z.dbmysql.dao.pool;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.GPool;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CPoolMaper extends IMapper<GPool, Integer> {
}

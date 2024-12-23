package com.z.dbmysql.dao.slot;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CPayline;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CPaylineMaper extends IMapper<CPayline, Integer> {
}

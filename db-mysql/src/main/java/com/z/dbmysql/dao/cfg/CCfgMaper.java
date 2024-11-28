package com.z.dbmysql.dao.cfg;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CCfg;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CCfgMaper extends IMapper<CCfg, Integer> {
}

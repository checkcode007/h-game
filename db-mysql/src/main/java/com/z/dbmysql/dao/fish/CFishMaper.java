package com.z.dbmysql.dao.fish;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CFish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CFishMaper extends IMapper<CFish, Integer> {
}

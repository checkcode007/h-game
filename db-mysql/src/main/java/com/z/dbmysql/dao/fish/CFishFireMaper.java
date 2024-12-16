package com.z.dbmysql.dao.fish;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CFish;
import com.z.model.mysql.cfg.CFishFire;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CFishFireMaper extends IMapper<CFishFire, Integer> {
}

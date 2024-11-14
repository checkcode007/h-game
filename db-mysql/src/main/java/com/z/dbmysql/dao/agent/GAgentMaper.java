package com.z.dbmysql.dao.agent;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GAgent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GAgentMaper extends IMapper<GAgent, Long> {
}

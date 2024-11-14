package com.z.dbmysql.dao.user;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GUserMaper extends IMapper<GUser, Long> {
}

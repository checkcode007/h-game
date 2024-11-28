package com.z.dbmysql.dao.email;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GEmail;
import com.z.model.mysql.GUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GEmailMaper extends IMapper<GEmail, Long> {
}

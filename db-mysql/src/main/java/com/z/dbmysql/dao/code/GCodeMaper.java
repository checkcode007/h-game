package com.z.dbmysql.dao.code;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GCodeMaper extends IMapper<GCode, Long> {
}

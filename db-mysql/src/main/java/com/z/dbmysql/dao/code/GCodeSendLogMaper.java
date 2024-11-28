package com.z.dbmysql.dao.code;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GCode;
import com.z.model.mysql.GCodeSendLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GCodeSendLogMaper extends IMapper<GCodeSendLog, Long> {
}

package com.z.dbmysql.dao.mali;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CMali;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CMaliMaper extends IMapper<CMali, Long> {
}

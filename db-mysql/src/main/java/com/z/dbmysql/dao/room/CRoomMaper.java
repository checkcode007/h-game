package com.z.dbmysql.dao.room;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GRoom;
import com.z.model.mysql.cfg.CRoom;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CRoomMaper extends IMapper<CRoom, Integer> {
}

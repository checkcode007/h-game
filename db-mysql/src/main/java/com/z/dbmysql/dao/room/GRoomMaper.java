package com.z.dbmysql.dao.room;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GGame;
import com.z.model.mysql.GRoom;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GRoomMaper extends IMapper<GRoom, Long> {
}

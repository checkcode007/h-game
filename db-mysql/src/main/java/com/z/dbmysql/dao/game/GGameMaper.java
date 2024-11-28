package com.z.dbmysql.dao.game;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GGame;
import com.z.model.mysql.GUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GGameMaper extends IMapper<GGame, Long> {
}

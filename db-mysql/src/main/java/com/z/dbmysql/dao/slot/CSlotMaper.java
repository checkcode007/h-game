package com.z.dbmysql.dao.slot;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CMali;
import com.z.model.mysql.cfg.CSlot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CSlotMaper extends IMapper<CSlot, Integer> {
}

package com.z.dbmysql.dao.walletlog;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GBankLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GBankLogMaper extends IMapper<GBankLog, Long> {
}

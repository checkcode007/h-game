package com.z.dbmysql.dao.banktransfer;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GBankTransfer;
import com.z.model.mysql.GEmail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GBankTransferMaper extends IMapper<GBankTransfer, Long> {
}

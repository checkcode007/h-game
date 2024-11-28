package com.z.dbmysql.dao.wallet;

import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GUser;
import com.z.model.mysql.GWallet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GWalletMaper extends IMapper<GWallet, Long> {
}

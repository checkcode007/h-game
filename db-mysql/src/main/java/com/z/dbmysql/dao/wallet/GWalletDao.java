package com.z.dbmysql.dao.wallet;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.dbmysql.dao.user.GUserMaper;
import com.z.model.mysql.GUser;
import com.z.model.mysql.GWallet;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GWalletDao extends AbstractMapperService<GWallet,Long> {

    @Autowired
    GWalletMaper maper;

    @Override
    public String cacheNamespace() {
        return "g_wallet";
    }

    @Override
    protected IMapper<GWallet, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return "g_wallet";
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{"g_wallet"};
    }
    public List<GWallet> getAll(){
        return super.getAll(null);
    }

    public GWallet findById(long uid){
        return super.findById(uid);
    }

    public GWallet save(GWallet user){
        return super.save(user);
    }
    public GWallet update(GWallet wallet){
        return super.update(wallet);
    }

}

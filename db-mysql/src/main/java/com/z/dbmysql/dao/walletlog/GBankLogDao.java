package com.z.dbmysql.dao.walletlog;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.model.mysql.GBankLog;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GBankLogDao extends AbstractMapperService<GBankLog,Long> {

    @Autowired
    GBankLogMaper maper;

    @Override
    public String cacheNamespace() {
        return "g_bank_log";
    }

    @Override
    protected GBankLogMaper getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return "g_bank_log";
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{"g_bank_log"};
    }
    public List<GBankLog> getAll(){
        return super.getAll(null);
    }

    public GBankLog findById(long uid){
        return super.findById(uid);
    }


    public GBankLog save(GBankLog user){
        return super.save(user);
    }

    @Override
    public GBankLog update(GBankLog gBankLog) {
        return super.update(gBankLog);
    }

    public List<GBankLog> findByUid(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("uid",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public GBankLog findByTransfer(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("transfer_id",uid);
        return super.findByOneByParam(wheres);
    }


    public List<GBankLog> findByDeviceId(String deviceId){
        return this.findByMultiByWhere(" and device_id= '"+deviceId+"' and state !="+UserState.DEL.k,100);
    }
}

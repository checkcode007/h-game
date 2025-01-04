package com.z.dbmysql.dao.banktransfer;

import com.z.common.util.MysqlTables;
import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.bean.Pager;
import com.z.model.mysql.GBankTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GBankTransferDao extends AbstractMapperService<GBankTransfer,Long> {

    @Autowired
    GBankTransferMaper maper;

    static final String TABLE_NAME = "g_bank_transfer";

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<GBankTransfer, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return  new String[]{TABLE_NAME};
    }
    public List<GBankTransfer> getAll(){
        return super.getAll(null);
    }

    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #id",unless = "#result == null")
    public GBankTransfer findById(long id){
        return super.findById(id);
    }

    @CacheEvict(cacheNames = "commonCache",  key = "getTarget().cacheNamespace() + ':'  + #e.id")
    public GBankTransfer save(GBankTransfer e){
        return super.save(e);
    }

    public List<GBankTransfer> findByFrom(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("from_id",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public List<GBankTransfer> findByTarget(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("target_id",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public Pager<GBankTransfer> page(long uid, int page, int pageSize) {
        // 构造查询条件
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("from_id", uid);
        Pager<GBankTransfer> pager = new Pager<>(page,pageSize);
        pager.setParams(wheres);
        List<String> orders = new ArrayList<>();
        orders.add("create_time|desc");
        pager.setOrders(orders);
        return super.page(pager);
    }



    public List<GBankTransfer> find(long uid,int state){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("uid",uid);
        wheres.put("state",state);
        return super.findByMultiByParam(wheres,1000);
    }

    @Override
    public GBankTransfer update(GBankTransfer gBankTransfer) {
        return super.update(gBankTransfer);
    }
}

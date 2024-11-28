package com.z.dbmysql.dao.code;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.bean.Pager;
import com.z.model.mysql.GCodeSendLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GCodeSendLogDao extends AbstractMapperService<GCodeSendLog,Long> {

    @Autowired
    GCodeSendLogMaper maper;

    static final String TABLE_NAME = "g_code_send_log";

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<GCodeSendLog, Long> getMapper() {
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
    public List<GCodeSendLog> getAll(){
        return super.getAll(null);
    }


    public void add(long fromId,long targetId,int count){
        GCodeSendLog record = GCodeSendLog.builder().fromId(fromId).targetId(targetId).cout(count).lastTime(new Date()).build();
        save(record);
    }


    public GCodeSendLog findById(long id){
        return super.findById(id);
    }

    public GCodeSendLog save(GCodeSendLog e){
        return super.save(e);
    }

    public List<GCodeSendLog> findByFrom(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("from_id",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public List<GCodeSendLog> findByTarget(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("target_id",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public Pager<GCodeSendLog> page(long uid, int page, int pageSize) {
        // 构造查询条件
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("from_id", uid);
        Pager<GCodeSendLog> pager = new Pager<>(page,pageSize);
        pager.setParams(wheres);
        List<String> orders = new ArrayList<>();
        orders.add("create_time|desc");
        pager.setOrders(orders);
        return super.page(pager);
    }
}

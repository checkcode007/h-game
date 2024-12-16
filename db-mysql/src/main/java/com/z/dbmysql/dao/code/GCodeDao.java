package com.z.dbmysql.dao.code;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.bean.Pager;
import com.z.model.mysql.GCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GCodeDao extends AbstractMapperService<GCode,Long> {

    @Autowired
    GCodeMaper maper;

    static final String TABLE_NAME = "g_code";

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<GCode, Long> getMapper() {
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
    public List<GCode> getAll(){
        return super.getAll(null);
    }

    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #id",unless = "#result == null")
    public GCode findById(long id){
        return super.findById(id);
    }

    @CacheEvict(cacheNames = "commonCache",  key = "getTarget().cacheNamespace() + ':'  + #e.id")
    public GCode save(GCode e){
        return super.save(e);
    }

    public List<GCode> findByFrom(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("from_id",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public List<GCode> findByTarget(long uid){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("target_id",uid);
        return super.findByMultiByParam(wheres,1000);
    }
    public List<GCode> findByCode(String code){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("code",code);
        return super.findByMultiByParam(wheres,1000);
    }
    public Pager<GCode> page(long uid, int page, int pageSize) {
        // 构造查询条件
        Map<String, Object> wheres = new HashMap<>();
        wheres.put("from_id", uid);
        Pager<GCode> pager = new Pager<>(page,pageSize);
        pager.setParams(wheres);
        List<String> orders = new ArrayList<>();
        orders.add("create_time|desc");
        pager.setOrders(orders);
        return super.page(pager);
    }


    public List<GCode> find(long uid, int state){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("uid",uid);
        wheres.put("state",state);
        return super.findByMultiByParam(wheres,100);
    }
}

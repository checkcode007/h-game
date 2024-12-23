package com.z.dbmysql.dao.pool;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.model.mysql.cfg.GPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GPoolDao extends AbstractMapperService<GPool,Long> {

    static final String TABLE_NAME = "g_pool";

    @Autowired
    CPoolMaper maper;

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected CPoolMaper getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long aLong) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{TABLE_NAME};
    }

    public List<GPool> getAll(){
        return super.getAll(null);
    }
    public GPool findById(Long id){
        return super.findById(id);
    }

    public GPool update(GPool recored){
        return super.update(recored);
    }

 }

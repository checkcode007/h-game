package com.z.dbmysql.dao.cfg;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CCfgDao extends AbstractMapperService<CCfg,Integer> {

    @Autowired
    CCfgMaper maper;

    @Override
    public String cacheNamespace() {
        return "c_cfg";
    }

    @Override
    protected IMapper<CCfg, Integer> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Integer id) {
        return "c_cfg";
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{"c_cfg"};
    }

    public List<CCfg> getAll(){
        return super.getAll(null);
    }

    public CCfg findById(Integer uid){
        return super.findById(uid);
    }

    public List<CCfg> findAll(){
        return super.getAll(null);
    }

}

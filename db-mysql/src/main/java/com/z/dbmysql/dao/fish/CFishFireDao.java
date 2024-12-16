package com.z.dbmysql.dao.fish;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CFishFire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CFishFireDao extends AbstractMapperService<CFishFire,Integer> {

    static final String TABLE_NAME = "c_fish_fire";

    @Autowired
    CFishFireMaper maper;

    @Override
    public String cacheNamespace() {
        return TABLE_NAME;
    }

    @Override
    protected IMapper<CFishFire, Integer> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Integer aLong) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{TABLE_NAME};
    }
    public List<CFishFire> getAll(){
        return super.getAll(null);
    }
//    @Cacheable(cacheNames = "commonCache", key = "getTarget().cacheNamespace() + ':'  + #uid",unless = "#result == null")
    public CFishFire findById(int uid){
        return super.findById(uid);
    }

}

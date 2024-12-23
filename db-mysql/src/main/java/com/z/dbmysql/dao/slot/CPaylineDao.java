package com.z.dbmysql.dao.slot;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CPayline;
import com.z.model.mysql.cfg.CSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CPaylineDao extends AbstractMapperService<CPayline, Integer> {

    static final String NAME = "c_payline";

    @Autowired
    CPaylineMaper maper;

    @Override
    public String cacheNamespace() {
        return NAME;
    }

    @Override
    protected IMapper<CPayline, Integer> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Integer aLong) {
        return NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{NAME};
    }
    public List<CPayline> getAll(){
        return super.getAll(null);
    }
    public CPayline findById(Integer id){
        return super.findById(id);
    }

}

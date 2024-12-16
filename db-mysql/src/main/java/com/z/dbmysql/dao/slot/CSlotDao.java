package com.z.dbmysql.dao.slot;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.dbmysql.dao.mali.CMaliMaper;
import com.z.model.mysql.cfg.CMali;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.type.user.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CSlotDao extends AbstractMapperService<CSlot, Integer> {

    static final String NAME = "c_slot";

    @Autowired
    CSlotMaper maper;

    @Override
    public String cacheNamespace() {
        return NAME;
    }

    @Override
    protected IMapper<CSlot, Integer> getMapper() {
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
    public List<CSlot> getAll(){
        return super.getAll(null);
    }
    public CSlot findById(Integer id){
        return super.findById(id);
    }

}

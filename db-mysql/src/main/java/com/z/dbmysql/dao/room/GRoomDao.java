package com.z.dbmysql.dao.room;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GRoom;
import com.z.model.type.RoomState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GRoomDao extends AbstractMapperService<GRoom,String> {
    @Autowired
    GRoomMaper maper;

    static final String TABLE_NAME="g_room";

    @Override
    public String cacheNamespace() {
        return "g_room";
    }

    @Override
    protected IMapper<GRoom, String> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(String s) {
        return TABLE_NAME;
    }
    @Override
    protected String[] getAllTableName() {
        return new String[]{TABLE_NAME};
    }
    public List<GRoom> getAll(){
        return super.getAll(null);
    }

    public GRoom findById(String id){
        return super.findById(id);
    }


    public GRoom save(GRoom game){
        return super.save(game);
    }
    public GRoom update(GRoom game){
        return super.update(game);
    }

    public List<GRoom> findByCfgId(int cfgId){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("cfg_id",cfgId);
        return super.findByMultiByParam(wheres,10);
    }
    public List<GRoom> findNotFull(int cfgId){
        return this.findByMultiByWhere(" and cfg_id= "+cfgId +" and state !="+ RoomState.FULL.k,10);
    }

}

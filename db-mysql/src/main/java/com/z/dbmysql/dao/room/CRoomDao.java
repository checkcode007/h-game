package com.z.dbmysql.dao.room;

import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CRoomDao extends AbstractMapperService<CRoom,Integer> {
    @Autowired
    CRoomMaper maper;
    static final String TABLE_NAME="c_room";

    @Override
    public String cacheNamespace() {
        return "c_room";
    }

    @Override
    protected IMapper<CRoom, Integer> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Integer id) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
        return new String[]{TABLE_NAME};
    }
    public List<CRoom> getAll(){
        return super.getAll(null);
    }

    public CRoom findById(int id){
        return super.findById(id);
    }


    public CRoom save(CRoom room){
        return super.save(room);
    }
    public CRoom update(CRoom room){
        return super.update(room);
    }

    public CRoom find(CommonGame.GameType gameType, CommonGame.RoomType roomType){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("game_type",gameType.getNumber());
        wheres.put("type",roomType.getNumber());
        return super.findByOneByParam(wheres);
    }
    public List<CRoom> find(CommonGame.GameType gameType){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("game_type",gameType.getNumber());
        return super.findByMultiByParam(wheres,5);
    }

}

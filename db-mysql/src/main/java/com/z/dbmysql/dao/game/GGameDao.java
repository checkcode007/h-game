package com.z.dbmysql.dao.game;

import com.z.common.util.MysqlTables;
import com.z.dbmysql.common.AbstractMapperService;
import com.z.dbmysql.common.IMapper;
import com.z.model.mysql.GGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GGameDao extends AbstractMapperService<GGame,Long> {
    @Autowired
    GGameMaper maper;
    static final String TABLE_NAME="g_game";

    @Override
    public String cacheNamespace() {
        return "g_game";
    }

    @Override
    protected IMapper<GGame, Long> getMapper() {
        return maper;
    }

    @Override
    protected String getTableName(Long id) {
        return TABLE_NAME;
    }

    @Override
    protected String[] getAllTableName() {
//        String[] tables = new String[10];
//        for (int i = 0; i < 10; i++) {
//            tables[i] =  MysqlTables.modular_10((long)i,TABLE_NAME);
//        }
//        return tables;
        return new String[]{TABLE_NAME};
    }
    public List<GGame> getAll(){
        return super.getAll(null);
    }

    public GGame findById(long id){
        return super.findById(id);
    }


    public GGame save(GGame game){
        return super.save(game);
    }

    public GGame findByRoomId(long roomId){
        Map<String, Object> wheres =new HashMap<>();
        wheres.put("room_id",roomId);
        return super.findByOneByParam(wheres);
    }
    public GGame update(GGame game){
        return super.update(game);
    }
}

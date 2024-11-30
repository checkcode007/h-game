package com.z.core.service.game.room;

import com.z.dbmysql.dao.room.CRoomDao;
import com.z.dbmysql.dao.room.GRoomDao;
import com.z.model.mysql.GRoom;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.type.RoomState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * 房间管理类
 */
@Service
public class RoomBizService {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GRoomDao dao;
    @Autowired
    CRoomDao cDao;

    /**
     * 进入房间
     */
    public List<CRoom> into(long uid, CommonGame.GameType gameType) {
        StringJoiner sj = new StringJoiner(",").add("uid:" + uid).add("gameType:" + gameType);
        log.info(sj.toString());
        List<CRoom> cRooms = cDao.find(gameType);
        if (cRooms == null || cRooms.isEmpty()){
            log.error(sj.add("croom null").toString());
            return null;
        }

        log.info(sj.add("size:"+cRooms.size()).add("success").toString());
        return cRooms;
    }

        /**
         * 进入房间
         */
    public GRoom intoGameRoom(long uid, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("gameType:"+gameType).add("roomType:"+roomType);
        log.info(sj.toString());
        CRoom cRoom = cDao.find(gameType, roomType);
        if(cRoom == null) {
            log.error(sj.add("croom null").toString());
            return null;
        }
        int cfgId = cRoom.getId();
        sj.add("cfgId:"+cfgId);
        List<GRoom> list = dao.findNotFull(cfgId);
        GRoom gRoom = null;
        if(list == null || list.isEmpty()) {
            gRoom = create(cfgId);
        }else{
            gRoom = list.get(0);
        }
        log.info(sj.add("success").toString());
        return gRoom;

    }
    public GRoom create(int cfgId){
        Date now = new Date();
        GRoom room =GRoom.builder().cfgId(cfgId)
                .jackpot(0).state(RoomState.IDEL.k).curPlayers(0).createTime(now).updateTime(now).build();
        room = dao.save(room);
        return room;
    }
    @Scheduled(cron = "*/10 * * * * ?" )
    public void exe(){
        log.debug("----------->start");
        List<CRoom> list = cDao.getAll();
        for (CRoom gRoom : list) {
            log.debug("-------->"+gRoom);
        }
    }

}

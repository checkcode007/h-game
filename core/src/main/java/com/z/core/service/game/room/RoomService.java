package com.z.core.service.game.room;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.z.core.service.game.aladdin.AladdinRoom;
import com.z.core.service.game.corpse.CorpseRoom;
import com.z.core.service.game.fish.FishRoom;
import com.z.core.service.game.football.BallRoom;
import com.z.core.service.game.line9.Line9Room;
import com.z.core.service.game.majiang.MaJiangRoom;
import com.z.core.service.game.mali.MaliHigherRoom;
import com.z.core.service.game.mali.MaliRoom;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.game.pig.PigRoom;
import com.z.core.service.game.puck.PuckRoom;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.game.wm.WMRoom;
import com.z.core.util.SpringContext;
import com.z.dbmysql.dao.room.CRoomDao;
import com.z.model.common.MsgId;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 房间管理类
 */
public enum RoomService {
    ins;
    protected Logger log = LoggerFactory.getLogger(getClass());

    Map<Long, SuperRoom> map = new ConcurrentHashMap<>();
    Table<CommonGame.GameType, CommonGame.RoomType, CRoom> table = HashBasedTable.create();

    RoomService() {
        init();
    }

    public void init() {
        reloadCfg();
    }

    public void reloadCfg() {
        Table<CommonGame.GameType, CommonGame.RoomType, CRoom> table1 = HashBasedTable.create();
        CRoomDao dao = SpringContext.getBean(CRoomDao.class);
        List<CRoom> list = dao.getAll();
        for (CRoom e : list) {
            table1.put(CommonGame.GameType.forNumber(e.getGameType()), CommonGame.RoomType.forNumber(e.getType()), e);
        }
        table = table1;
    }

    /**
     * 获取空闲房间
     *
     * @param gameType
     * @param roomType
     * @return
     */
    public synchronized SuperRoom getIdelRoom(CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        // 使用 Iterator 遍历
        Iterator<Map.Entry<Long, SuperRoom>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SuperRoom> entry = iterator.next();
            Long key = entry.getKey();
            SuperRoom value = entry.getValue();
            if (value.getCurC() < value.getMaxC()) return value;
        }
        return null;
    }

    public synchronized SuperRoom addRoom(long uid, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        CRoom cRoom = getCfg(gameType,roomType);
        if (cRoom == null) return null;
        if (gameType == CommonGame.GameType.BAIBIAN_XIAOMALI || gameType == CommonGame.GameType.JINGDIAN_XIAOMALI) {
            MaliRoom room = new MaliRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        } else if (gameType == CommonGame.GameType.JIUXIANLAWANG) {
            Line9Room room = new Line9Room(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.MAJIANG_2) {
            MaJiangRoom room = new MaJiangRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.FISH) {
            FishRoom room = new FishRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.BAIBIAN_XIAOMALI_HIGHER) {
            MaliHigherRoom room = new MaliHigherRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.SHAOLIN_ZUQIU) {
            BallRoom room = new BallRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.ALADING) {
            AladdinRoom room = new AladdinRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.BINGQIUTUPO) {
            PuckRoom room = new PuckRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.SHUIHUZHUAN) {
            WMRoom room = new WMRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.JINZHUSONGFU) {
            PigRoom room = new PigRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }else if (gameType == CommonGame.GameType.JIANGSHIXINNIANG) {
            CorpseRoom room = new CorpseRoom(cRoom,uid);
            map.put(room.getId(), room);
            room.init(cRoom);
            return room;
        }
        return null;
    }
    /**
     * 进入房间->显示房间列表
     */
    public AbstractMessageLite intoGame(long uid, CommonGame.GameType gameType) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add("gameType:"+gameType);
        log.info(sj.toString());
        Map<CommonGame.RoomType,CRoom> map = getCfg(gameType);

        Game.S_20002.Builder b = Game.S_20002.newBuilder();
        if(map!=null){
            map.forEach((k,v)->{
                Game.Room.Builder room = Game.Room.newBuilder();
                room.setId(v.getId()).setType(k).setMinBalance(v.getMinBalance()).setMinBet(v.getMinBet());
                b.addRooms(room.build());
            });
        }
        MyMessage.MyMsgRes.Builder res =MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_INTOGAME);
        res.setOk(true).addMsg(ByteString.copyFrom(b.build().toByteArray()));
        return res.build();

    }

    public void exe() {
        reloadCfg();
    }

    public CRoom getCfg(CommonGame.GameType gameType, CommonGame.RoomType roomType  ) {
        return table.get(gameType,roomType);
    }
    public Map<CommonGame.RoomType,CRoom> getCfg(CommonGame.GameType gameType) {
        return table.row(gameType);
    }

    public synchronized SuperRoom getRoom( long id) {
        return map.get(id);
    }
    public synchronized  void removeRoom( long id) {
        map.remove(id);
    }

    //todo 退出房间
    public void update(long now){
        List<SuperRoom>  list = new ArrayList<>(map.values());
        if(list == null || list.isEmpty()) return;
        for (SuperRoom room : list) {
            room.update(now);
        }
    }


}

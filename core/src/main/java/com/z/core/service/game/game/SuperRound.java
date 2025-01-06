package com.z.core.service.game.game;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.user.UserService;
import com.z.model.bo.user.User;
import com.z.model.common.MsgResult;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public abstract class SuperRound implements IRound {
    protected Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 第几轮
     */
    protected long id;
    /**
     * 游戏类型
     */
    protected CommonGame.GameType gameType;
    /**
     * 房间类型
     */
    protected CommonGame.RoomType roomType;
    /**
     * 下注 （uid,type,gold）
     */
    protected Table<Long, Integer, Long> table = HashBasedTable.create();

    protected int  ROW_SIZE = 4;
    protected int  COL_SIZE = 5;

    protected long uid;

    protected User user;

    public SuperRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        this.id = id;
        this.gameType = gameType;
        this.roomType = roomType;
    }
    public void init(long uid,int rowSize, int colSize) {
        this.ROW_SIZE = rowSize;
        this.COL_SIZE = colSize;
        this.uid = uid;
        user = UserService.ins.get(uid);
    }

    @Override
    public MsgResult bet(long uid, int type, long gold, boolean free) {
        Object obj = table.get(uid, type);
        long v = gold;
        if (obj != null) {
            v += (long) obj;
        }
        table.put(uid, type, v);
        return new MsgResult(true);
    }

    @Override
    public MsgResult end() {
        settle();
        return new MsgResult(true);
    }

    @Override
    public MsgResult settle() {
        return new MsgResult(true);
    }

    @Override
    public Table<Long, Integer, Long> getAllBet() {
        return table;
    }

    @Override
    public Map<Integer, Long> getBet(long uid) {
        return table.row(uid);
    }

    @Override
    public long getBet(long uid, int type) {
        Long v = table.get(uid, type);
        return v == null ? 0 : v;
    }

    @Override
    public void out(long uid) {
        user =null;
        table.remove(uid, 0);
    }

    @Override
    public long getId() {
        return id;
    }

    public static void main(String[] args) {
        User user =new User();
        user.setId(1);
        Map<Long ,User> map =new HashMap<>();
        map.put(user.getId(),user);
        map.values().forEach(System.err::println);
        user = null;
        System.err.println("-----------");
        map.values().forEach(System.err::println);
    }
}

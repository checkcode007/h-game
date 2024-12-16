package com.z.core.service.game.game;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.model.common.MsgResult;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public SuperRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        this.id = id;
        this.gameType = gameType;
        this.roomType = roomType;
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
        table.remove(uid, 0);
    }

    @Override
    public long getId() {
        return id;
    }
}

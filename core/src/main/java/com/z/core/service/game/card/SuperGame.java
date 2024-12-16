package com.z.core.service.game.card;

import com.z.model.proto.CommonGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuperGame {
    private static final Logger logger = LogManager.getLogger(SuperGame.class);
    protected long id;
    protected long roomId;
    protected long round;
    protected CommonGame.RoomType roomType;
    protected CommonGame.GameType gameType;
    protected CommonGame.GameState state; // 当前游戏状态

    public SuperGame(long id, long roomId, CommonGame.RoomType roomType, CommonGame.GameType gameType) {
        this.id = id;
        this.roomId = roomId;
        this.round = 0;
        this.roomType = roomType;
        this.gameType = gameType;
    }

    public void addRound(){
        round++;
    }

    public long getId() {
        return id;
    }

    public long getRoomId() {
        return roomId;
    }

    public long getRound() {
        return round;
    }

    public CommonGame.GameState getState() {
        return state;
    }
    public int getNextTime(){
        return 0;
    }

    public CommonGame.RoomType getRoomType() {
        return roomType;
    }

    public CommonGame.GameType getGameType() {
        return gameType;
    }
}

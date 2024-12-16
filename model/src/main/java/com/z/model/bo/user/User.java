package com.z.model.bo.user;

import com.z.model.mysql.GUser;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.type.user.UserState;
import lombok.Data;

@Data
public class User {
    long id;
    GUser user;
    /**
     * 房间id
     */
    long roomId;
    /**
     * 当前轮数id
     */
    long roundId;
    /**
     * 房间配置id
     */
    int cfgId;
    CommonGame.GameType gameType;
    CommonGame.RoomType roomType;
    private CommonUser.UserType type;
    private UserState state;
    boolean change;
    /**
     * 游戏的免费次数
     */
    int free;

    public void enter(CommonGame.GameType gameType, CommonGame.RoomType roomType,int cfgId,long roomId) {
        this.gameType = gameType;
        this.roomType = roomType;
        this.cfgId = cfgId;
        this.roomId = roomId;
        user.setGame(gameType.getNumber());
        user.setRoom(roomType.getNumber());
    }
    public void out(){
        this.gameType = CommonGame.GameType.GAME_DEFUALT;
        this.roomType = CommonGame.RoomType.ONE;
        user.setGame(gameType.getNumber());
        user.setRoom(roomType.getNumber());
    }

    public long getRoundId() {
        return roundId;
    }

    public void setRoundId(long roundId) {
        this.roundId = roundId;
    }
}

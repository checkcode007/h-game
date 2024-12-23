package com.z.model.bo.user;

import com.z.model.mysql.GUser;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.type.user.UserState;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class User {
    protected Logger log = LoggerFactory.getLogger(getClass());

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

    int freeBetGold;
    /**
     * 高级玩法次数
     */
    int highC;




    public int addFree(int c) {
        this.free += c;
        log.info("id:"+id+",c:"+c+",free:"+free);
        return free;

    }
    public  void subFree(){
        this.free--;
        this.free = Math.max(0,this.free);
        log.info("id:"+id+",free:"+free);
    }

    public void enter(CommonGame.GameType gameType, CommonGame.RoomType roomType,int cfgId,long roomId) {
        this.gameType = gameType;
        this.roomType = roomType;
        this.cfgId = cfgId;
        this.roomId = roomId;
        user.setGame(gameType.getNumber());
        user.setRoom(roomType.getNumber());

    }
    public void out(){
        if(this.gameType != CommonGame.GameType.BAIBIAN_XIAOMALI){
            highC = 0;
        }
        this.gameType = CommonGame.GameType.GAME_DEFUALT;
        this.roomType = CommonGame.RoomType.ONE;
        user.setGame(gameType.getNumber());
        user.setRoom(roomType.getNumber());
        free= 0;
        freeBetGold = 0;
        log.info("id:"+id);

    }

    public long getRoundId() {
        return roundId;
    }

    public void setRoundId(long roundId) {
        this.roundId = roundId;
    }

    public void subHigherC(){
        this.highC--;
    }
    public void addHighC(int c) {
        this.highC += c;
    }
}

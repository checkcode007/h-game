package com.z.core.service.game.game;

import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class SuperRoom implements IRoom{

    protected Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 房间id
     */
    protected long id;
    /**
     * 房间配置id
     */
    protected int cfgId;
    /**
     * 游戏类型
     */
    protected CommonGame.GameType gameType;
    /**
     * 房间类型
     */
    protected CommonGame.RoomType roomType;
    /**
     * 当前人数
     */
    protected int curC;
    /**
     * 最大人数
     */
    protected int maxC;
    /**
     *  门槛-最低入场资金
     */
    protected long minBalance;
    /**
     * 底分
     */
    protected long minBet;

    /**
     * 当前轮
     */
    protected IRound curRound;
    /**
     * 奖池倍率
     */
    protected Map<Integer,Integer> radioMap = new HashMap<>();

    protected Map<Long, IRound> roundMap = new ConcurrentHashMap<>();
    protected AtomicInteger roundIndex = new AtomicInteger(0);

    static AtomicLong  atomicLong = new AtomicLong(0);



    public SuperRoom(CRoom cRoom) {
        this.gameType = CommonGame.GameType.forNumber(cRoom.getGameType());
        this.roomType = CommonGame.RoomType.forNumber(cRoom.getType());
        this.id = atomicLong.incrementAndGet();
        init(cRoom);
    }


    @Override
    public void init(CRoom cRoom) {
        this.cfgId= cRoom.getId();
        this.maxC =cRoom.getMaxPlayers();
        this.minBet =cRoom.getMinBet();
        this.minBalance = cRoom.getMinBalance();
    }


    @Override
    public MsgResult check(long uid,long curGold) {
        if(curC>maxC){
            return new MsgResult("房间已满");
        }
        if(curGold<minBalance){
            return new MsgResult("门槛条件不满足");
        }
        return new MsgResult(true);
    }

    @Override
    public MsgResult enter(long uid) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid);
        log.info(sj.toString());
        Wallet wallet = WalletService.ins.get(uid);
        if(wallet == null ){
            log.error(sj.add("wallet null").toString());
            return new MsgResult("金额不足");
        }
        MsgResult ret = check(uid,wallet.getGold());
        if(!ret.isOk()){
            log.error(sj.add("check fail").toString());
            return ret;
        }
        if(!UserService.ins.enter(uid,gameType,roomType,cfgId,id)){
            log.error(sj.add("user change cache fail").toString());
            return ret;
        }
        this.curC++;
        MsgResult afterRet = afterEnter(uid);
        if(!afterRet.isOk()){
            log.error(sj.add("after fail:"+afterRet.getMessage()).toString());
            return ret;
        }
        return new MsgResult(true);
    }

    @Override
    public MsgResult afterEnter(long uid) {
        log.info("uid:"+uid);
        return new MsgResult(true);
    }

    @Override
    public MsgResult out(long uid) {
        if(curRound!=null){
            curRound.out(uid);
        }
        UserService.ins.out(uid);
        return new MsgResult(true);
    }

    /**
     * 结算-根据下注用户
     * @return
     */
    public MsgResult settle(long uid){
        return new MsgResult(true);
    }
    /**
     * 结算
     * @return
     */
    public MsgResult settle(){
        return new MsgResult(true);
    }
    @Override
    public int getRadio(int type) {
        return radioMap.getOrDefault(type,0);
    }
    @Override
    public boolean isFull(long id) {
        return false;
    }

    @Override
    public boolean isIDle(long id) {
        return false;
    }

    public int getCurC() {
        return curC;
    }

    public int getMaxC() {
        return maxC;
    }

    public long getMinBalance() {
        return minBalance;
    }

    public long getMinBet() {
        return minBet;
    }

    @Override
    public long getId() {
        return id;
    }

    public int getCfgId() {
        return cfgId;
    }

    public CommonGame.GameType getGameType() {
        return gameType;
    }

    public CommonGame.RoomType getRoomType() {
        return roomType;
    }
}

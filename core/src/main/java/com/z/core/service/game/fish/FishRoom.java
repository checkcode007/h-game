package com.z.core.service.game.fish;


import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.game.slot.SlotCommon;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CFish;
import com.z.model.mysql.cfg.CFishFire;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 房间
 */
public class FishRoom extends SuperRoom {
    private static final Log log = LogFactory.getLog(FishRoom.class);
    CFishService cFishService;
    Wallet wallet;
    /**
     * 房间里赢的总次数
     */
    long roomWinC;
    /**
     * 房间里总下注次数
     */
    long roomTotalC;

    public FishRoom(CRoom cRoom,long uid) {
        super(cRoom,uid);
    }

    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
        cFishService = SpringContext.getBean(CFishService.class);
        initParam();
    }

    @Override
    public MsgResult enter(long uid) {
        return super.enter(uid);
    }

    @Override
    public MsgResult out(long uid) {
        wallet =null;
        return super.out(uid);
    }

    @Override
    public MsgResult afterEnter(long uid) {
        super.afterEnter(uid);
        return new MsgResult(true);
    }

    public void reloadParam() {
        if(wallet == null){
            wallet = WalletService.ins.get(uid);
        }
        param.setWinC(wallet.getWins());
        param.setTotalC(wallet.getBetC());
        param.setRoomWinC(roomWinC);
        param.setRoomTotalC(roomTotalC);
    }

    public Game.S_20204.Builder fishCatch(long uid, CFishFire cFishFire, List<Game.Fish> fishList) {
        StringJoiner sj = new StringJoiner(",");
        sj.add("cfg:" + cFishFire.getId()).add("gold:" + cFishFire.getGold());
        Map<Long, CFish> goalMap = new HashMap<>();
        sj.add("goal:");
        reloadParam();
        if(fishList != null && !fishList.isEmpty()) {
            for (Game.Fish fish : fishList) {
                CFish cFish = cFishService.get(fish.getType(),roomType);
                if(cFish == null){
                    log.error("cFish null:"+fish.getType());
                    continue;
                }
                if(SlotCommon.ins.isCaught(param,cFish.getType(),cFish.getRadio(),cFishFire.getRadio())){
                    goalMap.put(fish.getId(),cFish);
                    sj.add(fish.getId()+"");
                    roomWinC++;
                }
                roomTotalC++;
            }
        }
        AtomicLong addGold = new AtomicLong(0);
        Game.S_20204.Builder b = Game.S_20204.newBuilder();
        goalMap.forEach((k,v)->{
            long rewardGold = v.getRate()*cFishFire.getGold();
            addGold.getAndAdd(rewardGold);
            b.addFishs(Game.FishGoal.newBuilder().setId(k).setType(CommonGame.FishType.valueOf(v.getType())).setGold(rewardGold).build());

        });
        walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, addGold.get(),gameType,roomType);
        Wallet wallet = WalletService.ins.get(uid);
        b.setLeaveGold(wallet.getGold());
        return b;
    }

}

package com.z.core.service.game.fish;


import cn.hutool.core.util.RandomUtil;
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
import org.apache.commons.lang3.RandomUtils;
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
        log.info(sj.toString());
        reloadParam();
        if(fishList != null && !fishList.isEmpty()) {
            for (Game.Fish fish : fishList) {
                CFish cFish = cFishService.get(fish.getType(),roomType);
                if(cFish == null){
                    log.error("cFish null:"+fish.getType());
                    continue;
                }
//                if(SlotCommon.ins.isCaught(param,cFish.getType(),cFish.getRadio(),cFishFire.getRadio())){
                if(calculateHitProbability(cFish.getRadio(),cFishFire.getRadio(),1)> RandomUtil.randomInt(101)){
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
            long rewardGold = v.getRate()*cFishFire.getGold()/10;
            addGold.getAndAdd(rewardGold);
            b.addFishs(Game.FishGoal.newBuilder().setId(k).setType(CommonGame.FishType.valueOf(v.getType())).setGold(rewardGold).build());

        });
        walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, addGold.get(),gameType,roomType);
        Wallet wallet = WalletService.ins.get(uid);
        b.setLeaveGold(wallet.getGold());
        return b;
    }
    /**
     * 计算最终命中概率
     *
     * @param fishProbability  鱼的初始概率（1-100）
     * @param bulletProbability 炮弹的初始概率（1-100）
     * @param frequency        发射频率（默认为1）
     * @return 最终命中概率（0-100）
     */
    public static int calculateHitProbability(int fishProbability, int bulletProbability, int frequency) {
        if (fishProbability < 1 || fishProbability > 100) {
            throw new IllegalArgumentException("鱼的初始概率必须在1到100之间");
        }
        if (bulletProbability < 1 || bulletProbability > 100) {
            throw new IllegalArgumentException("炮弹的初始概率必须在1到100之间");
        }
        if (frequency < 1) {
            frequency = 1;
        }

        // 取鱼和炮弹概率的最小值作为基础命中概率
        int baseProbability = Math.min(fishProbability, bulletProbability);

        // 根据发射频率调整命中概率
        // 这里假设每增加一次发射频率，命中概率增加k%
        double k = 0.1; // 调整系数，可以根据需求修改
        double adjustedProbability = baseProbability * (1 + k * (frequency - 1));

        // 确保最终概率不超过100%
        return (int) Math.min(100, Math.round(adjustedProbability));
    }

}

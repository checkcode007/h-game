package com.z.core.service.game.pig;


import cn.hutool.core.util.RandomUtil;
import com.google.protobuf.ByteString;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.service.game.PoolService;
import com.z.core.service.game.line9.Line9RankService;
import com.z.core.service.game.puck.PuckRound;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Line;
import com.z.model.bo.slot.Payline;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgId;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间
 */
public class PigRoom extends SlotRoom {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(PigRoom.class);

    Line9RankService line9Service;
    CSlotService service;


    public PigRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        line9Service = SpringContext.getBean(Line9RankService.class);
        service = SpringContext.getBean(CSlotService.class);
    }


    /**
     * 下注
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    public MsgResult<Game.PigMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        var b = Game.PigMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        b.setAddFreeC(freeC).setTotalFreeC(totalFreeC);
        Wallet wallet = WalletService.ins.get(uid);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
        addRecord(uid);
        PoolService.ins.add(gameType, gold);
        var ret = new MsgResult<Game.PigMsg>(true);
        ret.ok(b.build());
        return ret;
    }
    /**
     * 所有支付线
     */
    @Override
    public void checklines() {
        List<Rewardline> rewardlines = new ArrayList<>();
        for (Line payline : lineMap.values()) {
            Rewardline line = checkLine(payline);
            if (line == null) continue;
            rewardlines.add(line);
        }
        long base = getBetGold();
        for (Rewardline line : rewardlines) {
            CSlot cSlot = service.get(gameType, line.getK(), line.getPoints().size());
            if (cSlot == null) continue;
            int rate = cSlot.getRate();
            if(line.isHadBaida()){
                rate += rate * RandomUtil.randomInt(1,7);
            }
            line.setRate(rate);
            if (isPool(line.getK())) {
                poolLine(line);
            }else {
                line.setGold(base * rate);
            }
            line.setSpecialC(cSlot.getC1());
            this.rewardlines.add(line);
            highC +=cSlot.getC1();
            log.info(line.toString());
        }
    }

    @Override
    public boolean isPool(int type) {
        return type == CommonGame.LINE9.L9_BOX_VALUE;
    }
    /**
     * 奖池支付线处理
     */
    @Override
    public void poolLine(Rewardline line) {
        long poolGold = PoolService.ins.get(gameType);
        line.setGold(poolGold * line.getRate() / 10000);
        log.info("poolGold:" + poolGold +":"+ line.getGold());
    }

    public void addRecord(long uid) {
        long gold = 0L;
        for (Rewardline m : rewardlines) {
            if (m.getK() == CommonGame.LINE9.L9_BOX_VALUE) {
                gold += m.getGold();
            }
        }
        if (gold > 0) {
            line9Service.add(uid, gold);
        }
    }

    @Override
    public void update(long now) {
        super.update(now);
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_LINE9_BOX_POOL).setOk(true);
        res.addMsg(ByteString.copyFrom(Game.S_20304.newBuilder().setGold(PoolService.ins.get(gameType)).build().toByteArray()));
        UserChannelManager.sendMsg(uid, res.build());
    }
}

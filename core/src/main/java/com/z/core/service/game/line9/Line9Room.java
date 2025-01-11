package com.z.core.service.game.line9;


import com.google.protobuf.ByteString;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.service.game.PoolService;
import com.z.core.service.game.majiang.MaJiangRound;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgId;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 房间
 */
public class Line9Room extends SlotRoom {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(Line9Room.class);

    Line9RankService line9Service;
    CSlotService service;


    public Line9Room(CRoom cRoom, long uid) {
        super(cRoom, uid);
        line9Service = SpringContext.getBean(Line9RankService.class);
        service = SpringContext.getBean(CSlotService.class);
    }

    @Override
    public MsgResult enter(long uid) {
        sendPoolMsg();
        return super.enter(uid);
    }

    /**
     * 下注
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    public MsgResult<Game.Line9BetMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        Wallet wallet = WalletService.ins.get(uid);
        var b = Game.Line9BetMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        b.setFree(freeC > 0).setAddFreeC(freeC).setTotalFreeC(totalFreeC);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
        addRecord(uid);
        PoolService.ins.add(gameType, gold);
        var ret = new MsgResult<Game.Line9BetMsg>(true);
        ret.ok(b.build());
        return ret;
    }


    /**
     * 是否是宝箱连线
     * @param type
     * @return
     */
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

    @Override
    public boolean isSame(int i, int k1, int k2) {
        if (super.isSame(i, k1, k2)) {
            return true;
        }
        if (i != 0) {
            if (k2 == CommonGame.LINE9.L9_BAR_VALUE) {
                return true;
            }
        }
        return false;
    }

    public void addRecord(long uid) {
        long gold = 0L;
        for (Rewardline m : rewardlines) {
            if (m.getK() == CommonGame.LINE9.L9_BOX_VALUE) {
                gold += m.getGold();
            }
        }
        if (gold > 0) {
            try {
                line9Service.add(uid, gold);
            } catch (Exception e) {
                log.error("uid:"+uid+" id:"+id,e);
            }
        }
    }

    @Override
    public void update(long now) {
        super.update(now);
        sendPoolMsg();
    }
    public void sendPoolMsg(){
        MyMessage.MyMsgRes.Builder res = MyMessage.MyMsgRes.newBuilder().setId(MsgId.S_LINE9_BOX_POOL).setOk(true);
        res.addMsg(ByteString.copyFrom(Game.S_20304.newBuilder().setGold(PoolService.ins.get(gameType)).build().toByteArray()));
        UserChannelManager.sendMsg(uid, res.build());
    }
}

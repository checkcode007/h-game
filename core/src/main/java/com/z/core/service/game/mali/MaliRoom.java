package com.z.core.service.game.mali;


import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.slot.*;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 百变小玛丽房间
 */
public class MaliRoom extends SlotRoom {
    private static final Log log = LogFactory.getLog(MaliRoom.class);

    public MaliRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
    }

    /**
     * 下注
     * 免费次数不进入小玛丽
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    @Override
    public MsgResult<Game.MaliBetMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        Wallet wallet = WalletService.ins.get(uid);
        Game.MaliBetMsg.Builder b = Game.MaliBetMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
        b.setAddFreeC(freeC).setTotalFreeC(totalFreeC).setHighC(totalHighC);
        var ret = new MsgResult<Game.MaliBetMsg>(true);
        ret.ok(b.build());
        return ret;
    }

    @Override
    public void generate() {
        super.generate();
        List<SlotModel> origins = new ArrayList<>();
        for (SlotModel m : board.values()) {
            if (isOrigin(m.getK())) {
                origins.add(m);
            }
        }
        for (SlotModel m : origins) {
            int x = m.getX();
            int y = m.getY();
            m.setChangeType(getChangeType());
            SlotModel m1 = board.get(x - 1, y);
            if (m1 != null) {
                m1.setChangeType(getChangeType());
            }
            m1 = board.get(x + 1, y);
            if (m1 != null) {
                m1.setChangeType(getChangeType());
            }
            m1 = board.get(x, y - 1);
            if (m1 != null) {
                m1.setChangeType(getChangeType());
            }
            m1 = board.get(x, y + 1);
            if (m1 != null) {
                m1.setChangeType(getChangeType());
            }
        }
    }


    @Override
    public Rewardline checkHigher(Payline line) {
        Rewardline payline = null;
        for (Point p : line.getPoints()) {
            int x = p.getX();
            SlotModel m = board.get(x,p.getY());
            int type = m.getChangeType()>0 ?m.getChangeType():m.getK();
            if(!m.isBaida()) continue;
            if(payline==null){
                payline =new Rewardline(type,line.getLineId());
            }
            payline.addSpecicalC();
            payline.addPoint(p);
        }
        return payline;
    }

    @Override
    public int getChangeType() {
        return CommonGame.Mali.WILD_VALUE;
    }

    @Override
    public boolean isOrigin(int type) {
        return type == CommonGame.Mali.GOLD_WILD_VALUE;
    }

}

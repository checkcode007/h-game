package com.z.core.service.game.aladdin;


import com.z.core.service.game.PoolService;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 阿拉丁房间
 */
public class AladdinRoom extends SlotRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    CSlotService service;


    public AladdinRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        service = SpringContext.getBean(CSlotService.class);
        ROW_SIZE= 4;
        COL_SIZE =5;
    }
    public MsgResult<Game.AladdinMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        var b = Game.AladdinMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        Wallet wallet = WalletService.ins.get(uid);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
//        PoolService.ins.add(gameType, gold);
        var ret = new MsgResult<Game.AladdinMsg>(true);
        ret.ok(b.build());
        return ret;
    }
    @Override
    public void generate() {
        super.generate();
        boolean b_all_3 = false;
        int type =0;

        for (SlotModel m : board.row(2).values()) {
            if(type == 0){
                type = m.getType();
            } else if (type != m.getType()) {
                b_all_3 = false;
                break;
            }
        }
        if(b_all_3){
            for (SlotModel m : board.values()) {
                if(m.getX() == 2) continue;
                if(m.getType()< CommonGame.FOOTBALL.FT_MONK1_VALUE){
                    continue;
                }
                if(m.getType() == type) continue;
                m.setChangeType(type);
            }
        }

    }


}

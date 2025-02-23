package com.z.core.service.game.football;


import cn.hutool.core.util.RandomUtil;
import com.z.core.service.game.PoolService;
import com.z.core.service.game.line9.Line9RankService;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 房间
 */
public class BallRoom extends SlotRoom {
    private static final Log log = LogFactory.getLog(BallRoom.class);

//    protected Logger log = LoggerFactory.getLogger(getClass());

    Line9RankService line9Service;
    CSlotService service;


    public BallRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        line9Service = SpringContext.getBean(Line9RankService.class);
        service = SpringContext.getBean(CSlotService.class);
        ROW_SIZE= 4;
        COL_SIZE =5;
    }
    public MsgResult<Game.FootBallMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        var b = Game.FootBallMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        Wallet wallet = WalletService.ins.get(uid);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold()).setAddFreeC(freeC).setTotalFreeC(totalFreeC);
        PoolService.ins.add(gameType, gold);
        var ret = new MsgResult<Game.FootBallMsg>(true);
        ret.ok(b.build());
        return ret;
    }
    @Override
    public void generate() {
        super.generate();
        //第三列相同处理
        boolean b_all_3 = false;
        int type =0;
        for (SlotModel m : board.row(2).values()) {
            if(type == 0){
                type = m.getK();
            } else if (type != m.getK()) {
                b_all_3 = false;
                break;
            }
        }
        if(b_all_3){
            for (SlotModel m : board.values()) {
                if(m.getX() == 2) continue;
                if(m.getK()< CommonGame.FOOTBALL.FT_MONK1_VALUE){
                    continue;
                }
                if(m.getK() == type) continue;
                m.setChangeType(type);
            }
        }

    }

    @Override
    public void checkBonus() {
        int c = 0;
        for (SlotModel m : board.values()) {
            Slot slot = slots.get(m.getK());
            if (!slot.isBonus()) continue;
            c++;
        }
        if(c<1) return;
        CSlot cSlot = service.getBonus(gameType);
        if (cSlot == null) return;
        if(cSlot.getFreeMax()>0){
            freeC += RandomUtil.randomInt(cSlot.getFree(),cSlot.getFreeMax()+1);
        }else{
            freeC += cSlot.getFree();
        }
    }
}

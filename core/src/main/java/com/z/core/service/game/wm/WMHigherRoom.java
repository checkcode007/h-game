package com.z.core.service.game.wm;


import com.z.core.service.game.slot.SlotCommon;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 高级玩法房间
 */
public class WMHigherRoom extends SlotRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    int winC= 0;
    int totalC= 0;

    public WMHigherRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
    }

    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
    }

    void printslot(){
        for (Slot s : allSlots) {
            log.info(s.getK()+"------>"+s.getW1());
        }
    }

    /**
     * 下注
     *
     * @param uid
     */
    //todo 金额本地保存
    //todo限制无限循环
    public MsgResult<Game.WMHighMsg> bet(long uid, long gold) {
        printslot();
        nextRound();
        var round = createRound(uid, gold);
        StringJoiner sj = new StringJoiner(",").add("id:" + id).add("roundId:" + round.getId())
                .add("uid:" + uid).add("gold:" + gold).add("roundId:" + round.getId());
        log.info(sj.toString());
        var roundCheck = round.bet(uid, 0, 0, true);
        if (!roundCheck.isOk()) {
            log.error("roundCheck fail");
            return roundCheck;
        }
        betGold = gold;

        //生成符号
        generate();
        print();
        User user = UserService.ins.get(uid);
        int sameC = 0;
        int sameType = 0;
        boolean isDel = false;// 是否消除次数
        List<Integer> allList = allSymbol();
        for (Integer higher : allList) {
            if (sameType == 0) {
                sameType = higher;
                sameC++;
            } else if (sameType == higher) {
                sameC++;
            } else {
                break;
            }
            if (higher ==quitType) {
                isDel = true;
                break;
            }
        }
        sj.add("del:"+isDel);

        if(isDel){
            user.subHigherC();
        }
        int rate = 0;
        CSlot cSlot = service.get(gameType, sameType, sameC);
        if (cSlot != null) {
            rate = cSlot.getRate();
        }
        long rewardGold = betGold * rate;

        Game.WMHighMsg.Builder b = Game.WMHighMsg.newBuilder().setGold(rewardGold).setType(sameType).setGold(rate).setLeaveC(user.getHighC());
        for (int i = 1; i < 5; i++) {
            SlotModel m = board.get(i, 0);
            b.addPools(m.getType());
        }
        var ret = new MsgResult<Game.WMHighMsg>(true);
        if (rewardGold > 0) {
            winC++;
            walletBizService.changeGold(CommonUser.GoldType.GT_GAME, AddType.ADD, uid, rewardGold, gameType, roomType);
        }
        totalC++;
        Wallet wallet = WalletService.ins.get(uid);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold());
        ret.ok(b.build());
        log.info(sj.add("rewardGold:" + rewardGold).add("success").toString());
        return ret;
    }

    /**
     * 生成符号
     */
    @Override
    public void generate() {
        board.clear();
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                Slot slot = random(slots, i);
                SlotModel model =  SlotCommon.ins.toModel(slot,i,j);
                board.put(model.getX(), model.getY(), model);
            }
        }
    }
    @Override
    public Slot random(Map<Integer, Slot> slots, int i) {
        Set<Integer> goals = new HashSet<>();
        for (SlotModel m : board.values()) {
            goals.add(m.getType());
        }
        return SlotCommon.ins.randomHigher(gameType, slots,goals, i,winC,totalC);
    }

    public List<Integer>  allSymbol(){

        List<Integer> highers = new ArrayList<>();
        if(board.isEmpty())return highers;
        for (int i = 0; i < 5; i++) {
            int type = board.get(i,0).getType();
            highers.add(type);
        }
        return highers;
    }
}

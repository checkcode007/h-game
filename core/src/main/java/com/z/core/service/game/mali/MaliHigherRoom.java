package com.z.core.service.game.mali;


import com.z.core.service.game.slot.SlotCommon;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletService;
import com.z.model.bo.slot.Rewardline;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.CommonGame;
import com.z.model.proto.CommonUser;
import com.z.model.proto.Game;
import com.z.model.type.AddType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 高级玩法房间
 */
public class MaliHigherRoom extends SlotRoom {
    private static final Log log = LogFactory.getLog(MaliHigherRoom.class);

    int winC= 0;
    int totalC= 0;
    public MaliHigherRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        COL_SIZE = 5;
        ROW_SIZE = 1;
    }

    @Override
    public void generate() {
        board.clear();
        initParam();
        List<Slot> list = new ArrayList<>(slots.values());
        Map<Integer, Slot> slots1 = new HashMap<>();
        for (Slot slot : list) {
            slots1.put(slot.getK(),slot);
        }
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                SlotModel m = board.get(i, j);
                if (m != null) continue;
                param.setX(i);
                Slot slot = random(slots1);
                m = SlotCommon.ins.toModel(slot, i, j);
                if (i == 0) {
                    list.removeIf(e -> e.getK() == slot.getK());
                }
                board.put(m.getX(), m.getY(), m);
            }
        }
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
    public MsgResult<Game.MaliHighMsg> bet(long uid, long gold) {
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
        initParam();
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
//                sameC++;
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

        Game.MaliHighMsg.Builder b = Game.MaliHighMsg.newBuilder().setGold(rewardGold).setType(CommonGame.MaliHigher.forNumber(sameType)).setGold(rate).setLeaveC(user.getHighC());
        for (int i = 1; i < COL_SIZE; i++) {
            SlotModel m = board.get(i, 0);
            b.addPools(CommonGame.MaliHigher.forNumber(m.getK()));
        }
        var ret = new MsgResult<Game.MaliHighMsg>(true);
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

    @Override
    public Slot random(Map<Integer, Slot> slots) {
        Set<Integer> goals = new HashSet<>();
        for (SlotModel m : board.values()) {
            goals.add(m.getK());
        }
        return SlotCommon.ins.randomHigher(gameType, slots,goals,param);
    }

    public List<Integer>  allSymbol(){
        List<Integer> highers = new ArrayList<>();
        if(board.isEmpty())return highers;
        for (int i = 0; i < COL_SIZE; i++) {
            int type = board.get(i,0).getK();
            highers.add(type);
        }
        return highers;
    }
}

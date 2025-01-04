package com.z.core.service.game.aladdin;


import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.game.slot.SlotCommon;
import com.z.core.service.game.slot.SlotRoom;
import com.z.core.service.user.UserService;
import com.z.core.service.wallet.WalletService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.slot.SlotModel;
import com.z.model.bo.user.User;
import com.z.model.bo.user.Wallet;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import com.z.model.proto.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿拉丁房间
 */
public class AladdinRoom extends SlotRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 池子里所有的符号(上一轮)
     */
    protected Table<Integer, Integer, SlotModel> preBaida;

    public AladdinRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        service = SpringContext.getBean(CSlotService.class);
        ROW_SIZE = 4;
        COL_SIZE = 5;
        board = HashBasedTable.create();
        preBaida = HashBasedTable.create();
    }

    public MsgResult<Game.AladdinMsg> bet(long uid, int type, long gold, boolean free) {
        super.bet(uid, type, gold, free);
        putPre();
        var b = Game.AladdinMsg.newBuilder().setRoundId(id).addAllLines(payLines).addAllSpots(spots);
        Wallet wallet = WalletService.ins.get(uid);
        User user = UserService.ins.get(uid);
        b.setRate(rate).setGold(rewardGold).setLeaveGold(wallet.getGold()).setAddFreeC(freeC).setTotalFreeC(user.getFree());
        var ret = new MsgResult<Game.AladdinMsg>(true);
        ret.ok(b.build());
        return ret;
    }

    public void putPre() {
        if (!free) return;
        preBaida.clear();
        List<SlotModel> list = new ArrayList<>();
        for (int j = 0; j < ROW_SIZE; j++) {
            for (int i = 0; i < COL_SIZE; i++) {
                SlotModel m = board.get(i, j);
                Slot slot = slots.get(m.getType());
                if (!slot.isBaida()) continue;
                if (j == 0) {
                    list.add(m);
                } else {
                    int posY = RandomUtil.randomInt(0, COL_SIZE);
                    m.setY(posY);
                    int posX = j - 1;
                    m.setX(posX);
                    list.add(m);
                }
            }
        }
        for (SlotModel m : list) {
            int x = m.getX();
            int y = m.getY();
            SlotModel mm = preBaida.get(x, y);
            if (mm == null) {
                preBaida.put(x, y, m);
            } else {
                mm.addC(m.getC());
            }
        }
    }


    @Override
    public void generate() {
        board.clear();
        for (int i = 0; i < COL_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
                SlotModel model = preBaida.get(i, j);
                if (model == null) {
                    Slot slot = random(slots, i);
                    model = SlotCommon.ins.toModel(slot,i,j);
                }
                board.put(model.getX(), model.getY(), model);
            }
        }
    }

    @Override
    public Slot random(Map<Integer, Slot> slots, int i) {
        if (i != 4 && free) {//免费押注非最后一排不生成百搭
            Map<Integer, Slot> slots1 = new HashMap<>();
            for (Slot s : slots.values()) {
                if (s.isBaida()) continue;
                slots1.put(s.getK(), s);
            }
            return super.random(slots1, i);
        } else {
            if (free) { //免费选择第五排最少有一个百搭
                Slot slot = super.random(slots, i);
                if (slot.isBaida()) return slot;
                boolean hadBaida = false;
                for (SlotModel s : board.row(4).values()) {
                    if (slots.get(s.getType()).isBaida()) {
                        hadBaida = true;
                        break;
                    }
                }
                if (!hadBaida) {
                    return slots.get(CommonGame.Aladdin.A_BAIDA_VALUE);
                }
                return slot;
            } else {
                return super.random(slots, i);
            }
        }
    }


}

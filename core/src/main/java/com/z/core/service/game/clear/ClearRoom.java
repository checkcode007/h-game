package com.z.core.service.game.clear;


import com.z.common.util.PbUtils;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.game.slot.CSlotService;
import com.z.core.service.user.UserService;
import com.z.core.util.SpringContext;
import com.z.model.bo.slot.Slot;
import com.z.model.bo.user.User;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.mysql.cfg.CSlot;
import com.z.model.proto.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 消除游戏房间
 */
public class ClearRoom extends SuperRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());
    CSlotService service;
    /**
     * 选择的所有符号
     */
   protected Map<Integer, Slot> slots;

    /**
     * 是否免费轮
     */
    protected  boolean free;
    /**
     * 运动员划过的线
     */
   protected int wildIndex=0;


    public ClearRoom(CRoom cRoom, long uid) {
        super(cRoom,uid);
        service = SpringContext.getBean(CSlotService.class);
        slots = new HashMap<>();
    }

    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
        Map<Integer, List<CSlot>> map = service.getMap(gameType);
        for (List<CSlot> list : map.values()) {
            for (CSlot slot : list) {
                int k = slot.getSymbol();
                Slot s = slots.getOrDefault(k, new Slot(slot.getW1()));
                slots.putIfAbsent(k, s);
                BeanUtils.copyProperties(slot, s);
                s.setK(slot.getSymbol());
            }
        }
    }


    @Override
    public MsgResult enter(long uid) {
        return super.enter(uid);
    }

    @Override
    public MsgResult afterEnter(long uid) {
        super.afterEnter(uid);
        return new MsgResult(true);
    }

    /**
     * 下注
     * @param uid
     * @param gold
     */
    public MsgResult<Game.ClearGameMsg> bet(long uid, long gold,boolean free) {
        this.free = free;
        User user = UserService.ins.get(uid);
        ClearRound round =(ClearRound) createRound(uid);
        roundInit(uid,round);
        user.setRoundId(round.getId());
        log.info("uid:"+uid+" roundId:"+user.getRoundId()+"================>start");
        MsgResult<Game.ClearGameMsg> ret = round.bet(uid, 0, gold, free);
        log.info("uid:"+uid+" roundId:"+user.getRoundId()+"==>ret:"+ PbUtils.pbToJson(ret.getT()));
        return ret;
    }

    /**
     * 创建轮数
     * @param uid
     * @return
     */
    public IRound createRound(long uid) {//百变玛丽
        return  new ClearRound(roundIndex.incrementAndGet(),gameType,roomType);
    }
    public void roundInit(long uid,ClearRound round){
        roundMap.put(round.getId(), round);
        round.init(uid,ROW_SIZE, COL_SIZE);
        round.init(slots);
    }
}

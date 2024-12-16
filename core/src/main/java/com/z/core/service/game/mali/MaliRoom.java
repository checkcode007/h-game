package com.z.core.service.game.mali;


import com.z.common.game.MaliCommon;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.game.SuperRoom;
import com.z.model.bo.mali.BetResult;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 房间
 */
public class MaliRoom extends SuperRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public MaliRoom(CRoom cRoom) {
        super(cRoom);
    }

    /**
     * 下注
     *
     * @param uid
     * @param type 下注选中的池子类型
     * @param gold
     */
    public MsgResult<BetResult> bet(long uid, int type, long gold,boolean free) {
        IRound round = createRound(uid, gold);
        return round.bet(uid, 0, gold,free);
    }

    /**
     * 创建轮数
     * @param uid
     * @param gold
     * @return
     */
    public IRound createRound(long uid, long gold) {//百变玛丽
        MaliRound round = new MaliRound(roundIndex.incrementAndGet(),gameType,roomType);
        round.init(MaliCommon.ins.getPaylines(), MaliCommon.ins.getReels(), MaliCommon.SYMBOL_SIZE);
        roundMap.put(Long.valueOf(uid), round);
        return round;
    }

}

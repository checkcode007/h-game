package com.z.core.service.game.majiang;


import com.z.common.util.PbUtils;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.game.SuperRoom;
import com.z.core.service.user.UserService;
import com.z.model.bo.user.User;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 房间
 */
public class MaJiangRoom extends SuperRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public MaJiangRoom(CRoom cRoom) {
        super(cRoom);
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
    public MsgResult<Game.MjBetMsg> bet(long uid, long gold,boolean free) {
        User user = UserService.ins.get(uid);
        MaJiangRound round =(MaJiangRound) createRound(uid);
        user.setRoundId(round.getId());
        log.info("uid:"+uid+" roundId:"+user.getRoundId()+"================>start");
        MsgResult<Game.MjBetMsg> ret = round.bet(uid, 0, gold, free);
        log.info("uid:"+uid+" roundId:"+user.getRoundId()+"ret:"+ PbUtils.pbToJson(ret.getT()));
        log.info("uid:"+uid+" roundId:"+user.getRoundId()+"================>end");
        return ret;
    }

    /**
     * 创建轮数
     * @param uid
     * @return
     */
    public IRound createRound(long uid) {//百变玛丽
        IRound round = new MaJiangRound(roundIndex.incrementAndGet(),gameType,roomType);
        roundMap.put(round.getId(), round);
        return round;
    }


}

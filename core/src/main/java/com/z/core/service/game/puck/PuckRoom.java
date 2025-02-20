package com.z.core.service.game.puck;


import com.z.core.service.game.clear.ClearRoom;
import com.z.core.service.game.game.IRound;
import com.z.core.service.game.slot.SlotCommon;
import com.z.model.mysql.cfg.CRoom;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 冰球突破房间
 */
public class PuckRoom extends ClearRoom {
    private static final Log log = LogFactory.getLog(PuckRoom.class);


    public PuckRoom(CRoom cRoom, long uid) {
        super(cRoom, uid);
        ROW_SIZE = 3;
        COL_SIZE = 5;
    }

    /**
     * 创建轮数
     * @param uid
     * @return
     */
    public IRound createRound(long uid) {//百变玛丽
        PuckRound round =  new PuckRound(roundIndex.incrementAndGet(),gameType,roomType,base);
        bigWild(round.getId());
        round.setWildIndex(wildIndex);
        return round;
    }

    /**
     * 大wild处理
     */
    public void bigWild(long roundId){
        wildIndex = SlotCommon.ins.puckBigWild(gameType,param);
        if(wildIndex>0){
            param.addBigWildC();
        }
    }
}

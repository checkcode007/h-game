package com.z.core.service.game.puck;


import cn.hutool.core.util.RandomUtil;
import com.z.core.service.game.clear.ClearRoom;
import com.z.core.service.game.game.IRound;
import com.z.model.bo.slot.SlotModel;
import com.z.model.mysql.cfg.CRoom;
import com.z.model.proto.CommonGame;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 冰球突破房间
 */
public class PuckRoom extends ClearRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

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
        PuckRound round =  new PuckRound(roundIndex.incrementAndGet(),gameType,roomType);
        bigWild(round.getId());
        round.setWildIndex(wildIndex);
        return round;
    }

    /**
     * 大wild处理
     */
    public void bigWild(long roundId){
        wildIndex = 0;
        if(free) return;
        if(roundId % 10 < 3){
            return;
        }
//        if(RandomUtil.randomInt(0,10)>6){
//            return;
//        }
        //运动员划过的线(2,3,4轴)
        wildIndex = RandomUtils.nextInt(1, COL_SIZE-1);
        log.info("wildIndex----------->"+wildIndex);
    }
}

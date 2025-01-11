package com.z.core.service.game.fish;


import com.z.core.service.game.game.SuperRoom;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 房间
 */
public class FishRoom extends SuperRoom {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(FishRoom.class);

    public FishRoom(CRoom cRoom,long uid) {
        super(cRoom,uid);
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

}

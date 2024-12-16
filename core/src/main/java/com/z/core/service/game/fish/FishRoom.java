package com.z.core.service.game.fish;


import com.z.core.service.game.game.SuperRoom;
import com.z.model.common.MsgResult;
import com.z.model.mysql.cfg.CRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 房间
 */
public class FishRoom extends SuperRoom {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public FishRoom(CRoom cRoom) {
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

}

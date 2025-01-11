package com.z.core.service.game.majiang;


import com.z.core.service.game.clear.ClearRoom;
import com.z.core.service.game.slot.CSlotService;
import com.z.model.mysql.cfg.CRoom;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 房间
 */
public class MaJiangRoom extends ClearRoom {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(MaJiangRoom.class);

    CSlotService service;

    public MaJiangRoom(CRoom cRoom,long uid) {
        super(cRoom,uid);
    }

    @Override
    public void init(CRoom cRoom) {
        super.init(cRoom);
    }
}

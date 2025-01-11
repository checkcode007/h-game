package com.z.core.service.game.puck;

import com.z.core.service.game.clear.ClearRound;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PuckRound extends ClearRound {
    private static final Log log = LogFactory.getLog(PuckRound.class);

    public PuckRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType,int base) {
        super(id, gameType, roomType, base);
    }

    @Override
    protected void move() {
        super.move();
        if(wildIndex<1) return;
        //运动员划过的线(2,3,4轴)
        for (SlotModel m : board.row(wildIndex).values()) {
            m.setChangeType(CommonGame.Puck.P_BAIDA.getNumber());
        }
        log.info("wildIndex----------->"+wildIndex);
    }
}

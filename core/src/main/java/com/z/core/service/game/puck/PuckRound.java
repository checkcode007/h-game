package com.z.core.service.game.puck;

import com.z.core.service.game.clear.ClearRound;
import com.z.model.bo.slot.SlotModel;
import com.z.model.proto.CommonGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuckRound extends ClearRound {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public PuckRound(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        super(id, gameType, roomType);
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

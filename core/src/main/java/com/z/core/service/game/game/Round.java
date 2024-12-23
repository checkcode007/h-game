package com.z.core.service.game.game;

import com.z.model.proto.CommonGame;

public class Round extends SuperRound {
    public Round(long id, CommonGame.GameType gameType, CommonGame.RoomType roomType) {
        super(id, gameType, roomType);
    }
}

package com.z.model.es;

import lombok.Data;

@Data
public class EsGoldLog {
    long id;
    /**
     *
     */
    int type;
    int addType;
    long roomId;
    long gameId;
    /**
     * @see com.z.model.proto.CommonGame.RoomType
     */
    long roomType;
    /**
     * @see com.z.model.proto.CommonGame.GameType
     */
    long gameType;
    long g;
    long t;
    String d;

}

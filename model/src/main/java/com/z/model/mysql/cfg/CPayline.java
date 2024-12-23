package com.z.model.mysql.cfg;

import lombok.Data;

@Data
public class CPayline {

    int id;
    /**
     * 游戏类型
     * @see com.z.model.proto.CommonGame.GameType
     */
    int type;
    /**
     * 下标
     */
    int lineId;
    String points;//坐标

}

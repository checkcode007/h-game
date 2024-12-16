package com.z.model.mysql.cfg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CFishFire {
    private int id;
    /**
     * @see com.z.model.proto.CommonGame.FishFire
     */
    private int type;
    /**
     * @see com.z.model.proto.CommonGame.RoomType
     */
    private int roomType;//相同的个数
    private int gold; // 消耗的金币
    private int radio; // 概率

}

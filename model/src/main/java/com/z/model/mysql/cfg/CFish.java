package com.z.model.mysql.cfg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CFish {
    private int id;
    /**
     * @see com.z.model.proto.CommonGame.FishType
     */
    private int type;
    /**
     * @see com.z.model.proto.CommonGame.RoomType
     */
    private int roomType;//相同的个数
    private int rate; // 倍率

    private int radio; // 概率

}

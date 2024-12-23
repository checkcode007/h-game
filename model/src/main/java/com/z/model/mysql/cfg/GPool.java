package com.z.model.mysql.cfg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GPool {
    /**
     * @see com.z.model.proto.CommonGame.GameType
     */
    long id;
    int initGold;
    long gold;
    private Date lastTime;

}

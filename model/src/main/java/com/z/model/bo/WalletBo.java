package com.z.model.bo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class WalletBo {
    private long id;
    private long gold;
    private long bankGold;
    /**
     * 总胜利次数
     */
    private long wins;
    /**
     * 总失败次数
     */
    private long losses;
    /**
     * '累计下注金额'
     */
    private long betGold;
    /**
     * '累计赢得金额'
     */
    private long winGold;



}

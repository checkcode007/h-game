package com.z.model.mysql;

import lombok.Data;

import java.sql.Date;

@Data
public class GWalletLog {
    private long id;
    private int type;
    private long uid1;
    private long uid2;
    private long gold;
    private long gold1;
    private long gold2;
    private Date lastTime;

}

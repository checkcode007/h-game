package com.z.model.mysql;

import lombok.Data;

import java.sql.Date;

@Data
public class GWallet {
    private long id;
    private long gold;
    private long bankGold;
    private Date createTime;
    
    private Date updateTime;

}

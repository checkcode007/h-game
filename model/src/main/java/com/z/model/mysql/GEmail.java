package com.z.model.mysql;

import lombok.Data;

import java.sql.Date;

@Data
public class GEmail {
    private long id;
    private long uid;
    private long emailId;
    private int state;
    private Date lastTime;

}

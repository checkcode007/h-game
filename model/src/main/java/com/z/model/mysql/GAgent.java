package com.z.model.mysql;

import lombok.Data;

import java.util.Date;

@Data
public class GAgent {
    private long id;
    private long agentId;
    private Date lastTime;
}

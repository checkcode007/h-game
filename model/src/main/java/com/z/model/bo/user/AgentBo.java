package com.z.model.bo.user;

import com.z.model.mysql.GAgent;
import lombok.Data;

@Data
public class AgentBo {
    long id;
    GAgent agent;
    boolean change;
}

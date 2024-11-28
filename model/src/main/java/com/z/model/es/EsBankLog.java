package com.z.model.es;

import lombok.Data;


@Data
public class EsBankLog {
    private long id;
    /**
     * @see com.z.model.proto.CommonUser.BankType
     */
    private int type;
    private long uid;
    private long targetId;
    private long g;
    private long g1;
    private long g2;
    private long t;
    private int state;
    private String d;

}

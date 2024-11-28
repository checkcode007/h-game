package com.z.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GBankLog {
    private long id;
    private int type;
    private long uid;
    private long targetId;
    private long transferId;
    private long gold;
    private long gold1;
    private long gold2;
    private boolean state;
    private Date lastTime;

}

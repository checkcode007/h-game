package com.z.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 转账记录
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GBankTransfer {
    private long id;
    /**
     * @see  com.z.model.proto.CommonUser.TransferLogType
     */
    private int type;
    private long fromId;
    private long targetId;
    private long gold;
    private boolean state;
    private Date createTime;
    private Date updateTime;

}

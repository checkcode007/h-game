package com.z.model.mysql;

import lombok.*;

import java.util.Date;


/**
 * 转账记录
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GBankTransfer extends Model {
    /**
     * @see  com.z.model.proto.CommonUser.TransferLogType
     */
    private int type;
    private long fromId;
    private long targetId;
    private long gold;
    private long  mailId;
    private long tax;
    private long realGold;
    private boolean state;

}

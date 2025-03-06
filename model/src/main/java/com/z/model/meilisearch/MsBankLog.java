package com.z.model.meilisearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MSIndex(uid = "bank_log", primaryKey = "id")
public class MsBankLog {
    @MSFiled(openFilter = true, key = "id", openSort = true)
    private long id;
    /**
     * @see com.z.model.proto.CommonUser.BankType
     */
    @MSFiled(openFilter = true, key = "type", openSort = true)
    private int type;
    @MSFiled(openFilter = true, key = "uid", openSort = true)
    private long uid;
    @MSFiled(openFilter = true, key = "targetId", openSort = true)
    private long targetId;
    private long g;
    private long g1;
    private long g2;
    @MSFiled(openFilter = true, key = "t", openSort = true)
    private long t;
    @MSFiled(openFilter = true, key = "state", openSort = true)
    private int state;
    @MSFiled(openFilter = true, key = "d", openSort = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date d;

}

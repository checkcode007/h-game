package com.z.model.meilisearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MSIndex(uid = "user_log", primaryKey = "id")
public class MSUserLog implements Serializable {
    @MSFiled(openFilter = true, key = "id", openSort = true)
    private long id;
    @MSFiled(openFilter = true, key = "uid", openSort = true)
    private long uid;
    @MSFiled(openFilter = true, key = "type", openSort = true)
    private int type;
    private int robot;
    private String name;
    @MSFiled(openFilter = true, key = "phone", openSort = true)
    private String phone;
    @MSFiled(openFilter = true, key = "email", openSort = true)
    private String email;
    @MSFiled(openFilter = true, key = "deviceId", openSort = true)
    private String deviceId;
    @MSFiled(openFilter = true, key = "ip", openSort = true)

    private String ip;
    /**
     * @see com.z.model.type.UserAction
     */
    @MSFiled(openFilter = true, key = "action", openSort = true)

    private int action;
    @MSFiled(openFilter = true, key = "day", openSort = true)
    private int day;
    @MSFiled(openFilter = true, key = "t", openSort = true)
    private long t;
    @MSFiled(openFilter = true, key = "d", openSort = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date d;
}

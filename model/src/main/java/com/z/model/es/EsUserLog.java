package com.z.model.es;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户行为
 */
@Data
public class EsUserLog implements Serializable {
    private long id;
    private int type;
    private int robot;
    private String name;
    private String phone;
    private String email;
    private String deviceId;
    private String ip;
    /**
     * @see com.z.model.type.UserAction
     */
    private int action;
    private int day;
    /**
     * 操作时间
     */
    private long t;
    private String d;
}

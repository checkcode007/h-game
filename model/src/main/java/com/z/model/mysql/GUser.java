package com.z.model.mysql;

import lombok.Data;

import java.util.Date;


@Data
public class GUser {
    private long id;
    /**
     *  类型 0 普通账号
     * @see com.z.model.proto.CommonUser.UserType
     */
    private int type;
    /**
     * 是否是机器人
     */
    private int  robot;

    private String name;
    private String password;
    /**
     * 设备id
     */
    private String deviceId;
    private String ip;
    /**
     * 等级
     */
    private int lv;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 头像
     */
    private String icon;

    private int state;
    private Date createTime;

    private Date updateTime;

}

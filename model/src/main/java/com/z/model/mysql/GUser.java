package com.z.model.mysql;

import lombok.Data;


@Data
public class GUser extends Model {
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
    private int icon;
    /**
     * 点卡生成次数
     */
    private int codeCout;
    /**
     * @see com.z.model.type.user.UserState
     */
    private int state;
    /**
     * 锁定状态
     */
    private boolean lockState;

}

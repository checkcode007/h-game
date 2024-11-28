package com.z.model.type;
public enum CfgEnum {

    BASE("base",Integer.class,"基数"),
    TAXES("taxes",Integer.class,"税率"),
    REGNUN("regNum",Integer.class,"手机注册上限"),
    NIUNIU_TIME("niuniu_time",Integer.class,"百变牛牛-时间-准备"),

    BAIBIAN_XIAOMALI_BET_MIN("baibian_xiaomali_bet_min",Integer.class,"百变小玛丽-下注-最小金额"),
    BAIBIAN_XIAOMALI_BET_MAX("baibian_xiaomali_bet_max",Integer.class,"百变小玛丽-下注-最大金额"),
    BAIBIAN_XIAOMALI_BET_BASE("baibian_xiaomali_bet_base",Integer.class,"百变小玛丽-下注-除以的基数"),
    CODE_GOLD("code_gold",Integer.class,"兑换码对应的金币"),
    CODE_TIME("code_time",Integer.class,"点卡过期时间（秒）"),
    ;
    public String name;
    public Class clazz;
    public String des;

    CfgEnum(String name, Class clazz, String des) {
        this.name = name;
        this.clazz = clazz;
        this.des = des;
    }
}

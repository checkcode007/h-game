package com.z.model.type;
public enum CfgEnum {

    BASE("base",Integer.class,"基数"),
    TAXES("taxes",Integer.class,"税率"),
    REGNUN("regNum",Integer.class,"手机注册上限"),
    NIUNIU_TIME("niuniu_time",Integer.class,"百变牛牛-时间-准备"),


    CODE_GOLD("code_gold",Integer.class,"兑换码对应的金币"),
    CODE_TIME("code_time",Integer.class,"点卡过期时间（秒）"),


    U_V1("u_v1",Integer.class,"控制用户状态的值1"),
    U_V2("u_v2",Integer.class,"控制用户状态的值2"),
    U_V3("u_v3",Integer.class,"控制用户状态的值3"),
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

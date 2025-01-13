package com.z.core;

/**
 * 新排行榜
 * 1 房间榜,2 个人榜,3 组合榜
 * @author zcj
 */
public enum RankingBigType {
    ROOM(1, "房间榜"),
    ROLE(2, "个人榜"),
    ;

    int code;
    String name;

    RankingBigType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static RankingBigType getByCode(int code) {
        for (RankingBigType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}

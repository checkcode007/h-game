package com.z.core;

/**
 * 新排行榜类型
 * 分数类型
 * @author zcj
 */
public enum RankingScoreType {
    SCORE(0, "分数", "score"),
    DIAMOND(2, "钻石", "diamond"),
    PT(3, "pocket币", "pt"),
    PAY(4, "充值", "pay"),
    ;

    int code;
    String name;
    String groupName;

    RankingScoreType(int code, String name, String groupName) {
        this.code = code;
        this.name = name;
        this.groupName = groupName;
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

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public static RankingScoreType getByCode(int code) {
        for (RankingScoreType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}

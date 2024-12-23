package com.z.model.type;

/**
 * 位置类型
 */
public enum PosType {
    ALL(0,"全部"),Y(1,"指定类型"),N(2,"排除类型")
    ;
    private int k;
    private String name;

    PosType(int k, String name) {
        this.k = k;
        this.name = name;
    }

    public int getK() {
        return k;
    }

    public String getName() {
        return name;
    }
    public static PosType getType(int k) {
        for (PosType type : PosType.values()) {
            if (type.getK() == k) {
                return type;
            }
        }
        return null;
    }
}

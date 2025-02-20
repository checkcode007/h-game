package com.z.model.type;

public enum LineType {
    LOW(0,"低"),MID(1,"中"),HIGH(2,"高")
    ;
    private int k;
    private String name;

    LineType(int k, String name) {
        this.k = k;
        this.name = name;
    }

    public int getK() {
        return k;
    }

    public String getName() {
        return name;
    }
    public static LineType getType(int k) {
        for (LineType type : values()) {
            if (type.getK() == k) {
                return type;
            }
        }
        return null;
    }
}

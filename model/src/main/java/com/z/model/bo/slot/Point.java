package com.z.model.bo.slot;

import lombok.Data;

@Data
public class Point {
    int x;
    int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "P{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

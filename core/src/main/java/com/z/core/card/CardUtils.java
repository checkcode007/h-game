package com.z.core.card;

import java.util.Collections;
import java.util.List;

public class CardUtils {
    /**
     * 是否有相同的点数
     * 将点数排序，然后比较相邻的元素是否相同。如果相同，就表示存在相同点数的牌
     * @param points
     * @return
     */
    public static boolean hasSamePoint(List<Integer> points) {
        Collections.sort(points);
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).equals(points.get(i - 1))) {
                return true; // 表示有相同点数
            }
        }
        return false; // 没有相同点数
    }

}

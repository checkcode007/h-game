package com.z.common.util;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public final class MysqlTables {

    /**
     * 取模运行10个表
     * @return
     */
    public static String modular_10(Long id, String tName) {
        if (id == null || id == 0) {
            return tName + "_0";
        }
        long n = id % 10;
        return tName + "_" + n;
    }

    public static String modular_1000(Long id, String tName) {
        if (id == null || id == 0) {
            return tName + "_0";
        }
        long n = id % 1000;
        return tName + "_" + n;
    }

    public static List<String> getModular_10(String tName) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(tName + "_" + i);
        }
        return list;
    }

    /**
     * 哈希运算
     * @param id
     * @param tName
     * @return
     */
    public static String hash_10(String id,String tName) {
        if (StringUtils.isBlank(id)) {
            return tName;
        }

        int n = Math.abs(id.hashCode()) % 10;
        return tName + "_" + n;
    }

}

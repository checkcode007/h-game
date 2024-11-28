package com.z.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法
 * @author zcj
 */
public enum SnowflakeId {
    ins;

    private long workerId = 0;
    private long datacenterId = 1;
    private Snowflake snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    
    SnowflakeId() {
        workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        workerId = workerId % 32;
        snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    }

    public long snowflakeId() {
        return snowflake.nextId();
    }

    public long snowflakeId(long workerId, long datacenterId) {
        snowflake = IdUtil.createSnowflake(workerId, datacenterId);
        return snowflake.nextId();
    }

    public static void main(String[] args) {
        // 1303931069132832768
        for (int i = 0; i < 10; i++) {
            System.err.println(SnowflakeId.ins.snowflakeId());
        }
    }

    public long gameId() {
        snowflake = IdUtil.createSnowflake(1, 1);
        return snowflake.nextId();
    }
    public long roundId() {
        snowflake = IdUtil.createSnowflake(1, 2);
        return snowflake.nextId();
    }
}

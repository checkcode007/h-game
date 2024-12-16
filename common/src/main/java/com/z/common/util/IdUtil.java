package com.z.common.util;

import com.z.common.type.RedisKey;

public class IdUtil {

    public static long nextEmailId() {
        return RedisUtil.incr(RedisKey.EMAIL_ID,1);
    }

    public static long nextRoomId() {
        return RedisUtil.incr(RedisKey.ROOM_ID,1);
    }
}

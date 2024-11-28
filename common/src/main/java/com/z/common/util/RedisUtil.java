package com.z.common.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@Log4j2
public class RedisUtil {
    public static RedisTemplate<String, Object> redisTemplate;
    // 推荐：用于字符串操作
    public static StringRedisTemplate stringRedisTemplate;
    static {
        redisTemplate = SpringContext.getBean("redisTemplate");
        // 推荐：用于字符串操作
        stringRedisTemplate =SpringContext.getBean("stringRedisTemplate");
    }

    // 设置 Redis 字符串值
    public void setStringValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    // 获取 Redis 字符串值
    public String getStringValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // 设置 Redis 字符串值并设置过期时间
    public void setStringValueWithExpiration(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return boolean
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

    }

    /**
     * 根据前缀获取keys
     * @param pattern 表达式
     * @param count 数量
     * @return Set<String>
     */
    public static Set<String> keys(String pattern, int count) {
        return RedisUtil.scan(pattern+"*", count);
    }

    /**
     * 根据前缀获取keys
     * @param pattern 表达式
     * @param count 数量
     * @return Set<String>
     */
    public static Set<String> keysCustom(String pattern,int count) {
        return redisTemplate.keys(pattern);
    }

    /**
     * scan 实现
     * @param pattern   表达式
     * @param consumer  对迭代到的key进行操作
     */
    public static void scan(String pattern, int count, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisConnection connection) -> {
            try (
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(count).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                cursor.close();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * scan 实现
     * @param pattern 表达式
     * @param count 数量
     * @return Set<String>
     */
    public static Set<String> scan(String pattern, int count) {
        Set<String> keys = new HashSet<>();
        RedisClusterConnection clusterConnection = redisTemplate.getConnectionFactory().getClusterConnection();
        Iterable<RedisClusterNode> redisClusterNodes = clusterConnection.clusterGetNodes();
        for (RedisClusterNode next : redisClusterNodes) {
            if(next.isMaster()) {
                try {
                    Cursor<byte[]> scan = clusterConnection.scan(next, ScanOptions.scanOptions().match(pattern).count(count).build());
                    while (scan.hasNext()) {
                        keys.add(new String(scan.next()));
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return keys;
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public static long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true - 存在 / false - 不存在
     */
    public static boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public static boolean del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
               return redisTemplate.delete(key[0]);
            } else {
               return redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key))>0;
            }
        }
        return false;
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取 String 类型 key-value
     * @param key 键
     * @return String
     */
    public static String getStr(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true - 成功 / false - 失败
     */
    public static boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置 String 类型 key-value
     * @param key 键
     * @param value 值
     */
    public static void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static void set(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true - 成功 / false - 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定键不存在则设置缓存，如果键已经存在，则不会执行任何操作
     * 常用于占位
     * @param key 键
     * @param value 值
     * @param ttl 存活时间
     * @return true:键不存在,并设置缓存 false 表示键已存在
     */
    public static Boolean setIfAbsent(String key,Object value,long ttl) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, ttl, TimeUnit.SECONDS);
    }

    /**
     * 判断set member 是否存在
     * @param key 键
     * @param value 值
     * @return true - 存在 / false - 不存在
     */
    public static boolean sisMember(String key, Object value){
        return redisTemplate.opsForSet().isMember(key,value);
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return long
     */
    public static long incr(String key, long delta) {
        return incr(key, delta,0);
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @param expire 过期时间
     * @return long
     */
    public static long incr(String key, long delta,long expire) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }

        Long i = redisTemplate.opsForValue().increment(key, delta);
        if (expire > 0) {
            expire(key, expire);
        }
        return i;
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return long
     */
    public static long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @param expire 过期时间
     * @return long
     */
    public static long decr(String key, long delta, long expire) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        Long i = redisTemplate.opsForValue().increment(key, -delta);
        if (expire > 0) {
            expire(key, expire);
        }
        return i;
    }

    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return Object
     */
    public static Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 扫描set
     * @param key 键
     * @param pattern 表达式
     * @param count 数量
     * @return List<Map.Entry<Object, Object>>
     */
    public List<Map.Entry<Object, Object>> hscan(String key, String pattern, long count) {
        List<Map.Entry<Object, Object>> resultList = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().count(count).match(pattern).build();

        try (Cursor<Map.Entry<Object, Object>> hashCursor = redisTemplate.opsForHash().scan(key, scanOptions)) {
            while (hashCursor.hasNext()) {
                resultList.add(hashCursor.next());
            }
        } catch (Exception e) {
            log.error("Error occurred during Redis scan operation", e);
        }

        return resultList;
    }


    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取hashKey下对应的键值
     * @param key 键
     * @param values 要获取的键值的所有key
     * @return List<Object>
     */
    public static List<Object> hmget(String key, List<Object> values ) {
        return redisTemplate.opsForHash().multiGet(key, values);
    }

    /**
     * 获取所有哈希表中的字段
     * @param key 键
     * @return Set<Object>
     */
    public static Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 集合数量
     * @param key 键
     * @return Long
     */
    public static Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true - 成功 / false - 失败
     */
    public static boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true - 成功 / false - 失败
     */
    public static boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true - 成功 / false - 失败
     */
    public static boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true - 成功 / false - 失败
     */
    public static boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true - 存在 / false - 不存在
     */
    public static boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return double
     */
    public static double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return double
     */
    public static double hincr(String key, String item, double by,long time) {
        double aDouble= redisTemplate.opsForHash().increment(key, item, by);
        if (time > 0) {
            expire(key, time);
        }
        return aDouble;
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return double
     */
    public static double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return double
     */
    public static double hdecr(String key, String item, double by,long time) {
        double aDouble= redisTemplate.opsForHash().increment(key, item, -by);
        if (time > 0) {
            expire(key, time);
        }
        return aDouble;
    }

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return Set<Object>
     */
    public static Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForZSet().range(key,0,-1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return Set<Object>
     */
    public static Set<Object> sMembers1(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @param count 数量
     * @return Set<Object>
     */
    public static Set<Object> sMembers(String key, Integer count) {
        try {
            return redisTemplate.opsForSet().distinctRandomMembers(key, count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true - 存在 / false - 不存在
     */
    public static boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将数据移出set缓存
     * @param key 键
     * @param count 移出的数量
     * @return List<Object>
     */
    public static List<Object> sPop(String key, long count) {
        try {
            return redisTemplate.opsForSet().pop(key, count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sadd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSetAnd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return long
     */
    public static long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据key获取set集合
     * @param key 键
     * @return Set<Object>
     */
    public static Set<Object> sget(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束 0 到 -1 代表所有值
     * @return List<Object>
     */
    public static List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return long
     */
    public static long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return Object
     */
    public static Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return boolean
     */
    public static boolean lRightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return boolean
     */
    public static boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return boolean
     */
    public static boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return boolean
     */
    public static boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return boolean
     */
    public static boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从左侧删除
     * @param key 键
     * @return Object
     */
    public static Object lRightRemove(String key) {
        Object object = redisTemplate.opsForList().rightPop(key);
        return object;
    }

    /**
     * 添加 ZSet 元素
     * @param key 键
     * @param value 值
     * @param score 积分
     * @return boolean
     */
    public static boolean add(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 添加 ZSet 元素
     * @param key 键
     * @param value 值
     * @param score 积分
     * @return boolean
     */
    public static boolean zadd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 批量查询
     * @param keys 键列表
     * @return List<Object>
     */
    public static List<Object> getList(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 添加 ZSet 元素
     * @param key 键
     * @param value 值
     * @param score 积分
     * @param expire 过期时间，秒
     */
    public static void zadd(String key, Object value, double score, long expire) {
        redisTemplate.opsForZSet().add(key, value, score);
        if (expire > 0) {
            expire(key, expire);
        }
    }

    /**
     * 批量添加 Zset <br>
     * Set<TypedTuple<Object>> tuples = new HashSet<>();<br>
     * TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<Object>("zset-5",9.6);<br>
     * tuples.add(objectTypedTuple1);
     *
     * @param key 键
     * @param tuples 值 积分对
     * @return Long
     */
    public static Long batchAddZset(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    /**
     * Zset 删除一个或多个元素
     *
     * @param key 键
     * @param values 值
     * @return Long
     */
    public static Long removeZset(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * Zset 根据分数删除
     * @param key 键
     * @param minScore 最低分
     * @param maxScore 最高分
     * @return Long
     */
    public static Long removeRangeByScore(String key, double minScore, double maxScore) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, minScore,maxScore);
    }

    /**
     * 对指定的 zset 的 value 值 , socre 属性做增减操作
     * @param key 键
     * @param value 值
     * @param score 积分
     * @return Double
     */
    public static Double incrementScore(String key, Object value, double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    /**
     * 获取 key 中指定 value 的排名(从0开始,从小到大排序)
     * @param key 键
     * @param value 值
     * @return Long
     */
    public static Long rank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取 key 中指定 value 的排名(从0开始,从大到小排序)
     * @param key 键
     * @param value 值
     * @return Long
     */
    public static Long reverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从小到大,带上分数)
     * @param key 键
     * @param start 开始
     * @param end 结束
     * @return Set<ZSetOperations.TypedTuple<Object>>
     */
    public static Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从小到大,只有列名)
     *
     * @param key 键
     * @param start 开始
     * @param end 结束
     * @return Set<Object>
     */
    public static Set<Object> range(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从小到大,只有列名)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @return Set<Object>
     */
    public static Set<Object> rangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从小到大,集合带分数)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @return
     */
    public static Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从小到大,不带分数的集合)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @param offset 从指定下标开始
     * @param count  输出指定元素数量
     * @return Set<Object>
     */
    public static Set<Object> rangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从大到小,不带分数的集合)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @param offset 从指定下标开始
     * @param count  输出指定元素数量
     * @return Set<Object>
     */
    public static Set<Object> reverseRangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从小到大,集合带分数)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @return Set<Object>
     */
    public static Set<Object> reverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从大到小,带分数的集合)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @param offset 从指定下标开始
     * @param count  输出指定元素数量
     * @return Set<ZSetOperations.TypedTuple<Object>>
     */
    public static Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从小到大,集合带分数)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @return Set<ZSetOperations.TypedTuple<Object>>
     */
    public static Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * 返回 分数范围内 指定 count 数量的元素集合, 并且从 offset 下标开始(从小到大,带分数的集合)
     * @param key 键
     * @param min 开始积分
     * @param max 结束积分
     * @param offset 从指定下标开始
     * @param count  输出指定元素数量
     * @return Set<ZSetOperations.TypedTuple<Object>>
     */
    public static Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 获取索引区间内的排序结果集合(从0开始,从大到小,只有列名)
     * @param key 键
     * @param start 开始
     * @param end 结束
     * @return Set<Object>
     */
    public static Set<Object> reverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合中的值的下标，没有值下标为null
     * @param key 键
     * @param value 值
     * @return Long
     */
    public static Long zrank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取有序集合中所有的数量
     * @param key 键
     * @return Long
     */
    public static Long zcard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取有序集合中对应分数的数量
     *
     * @param key 键
     * @return Long
     */
    public static Long zcount(String key, Long start, Long end) {
        return redisTemplate.opsForZSet().count(key, start, end);
    }

    /**
     * 加积分
     * @param key 键
     * @param member 成员
     * @param score 积分
     * @return Double
     */
    public static Double zincrby(String key, Object member, double score) {
        return redisTemplate.opsForZSet().incrementScore(key,member,score);
    }

    /**
     * 获取分数
     * @param key 键
     * @param member 成员
     * @return Double
     */
    public static Double zscore(String key, Object member) {
        Double score = redisTemplate.opsForZSet().score(key, member);
        return Objects.nonNull(score) ? score : 0.0;
    }

    /**
     * 获取积分
     * @param key 键
     * @param members 成员
     * @param luaScript lua脚本
     * @return List
     */
    public static List getScoresAndValuesByMembers(String key, List<Object> members, String luaScript) {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(List.class);
        return redisTemplate.execute(redisScript, Collections.singletonList(key), members.toArray());
    }

    /**
     * 扫描 Redis 有序集合（ZSet）中的元素
     *
     * @param key Redis 键
     * @param count 扫描数量
     * @param pattern 匹配模式
     * @return 扫描到的元素列表
     */
    public List<ZSetOperations.TypedTuple<Object>> zscan(String key, int count, String pattern) {
        List<ZSetOperations.TypedTuple<Object>> resultList = new ArrayList<>();

        // 创建 ScanOptions 配置
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .count(count)
                .match(pattern)
                .build();

        try (Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan(key, scanOptions)) {
            // 扫描有序集合的元素
            while (cursor.hasNext()) {
                resultList.add(cursor.next());
            }
        } catch (Exception e) {
            // 记录日志而非打印堆栈跟踪
            log.error("Error occurred during Redis ZSet scan operation", e);
        }
        return resultList;
    }

    /**
     * 获取分数
     * @param key 键
     * @param member 成员
     * @return Double
     */
    public static Double zscoreNull(String key, Object member) {
        return redisTemplate.opsForZSet().score(key,member);
    }

    public static Boolean lock(String key, Long timeOut) throws InterruptedException {
        long time = Instant.now().toEpochMilli();
        do {
            boolean result = redisTemplate.opsForValue().setIfAbsent(key,key, Duration.ofSeconds(timeOut));
            if (result) {
                return true;
            } else {
                Thread.sleep(200);
            }
        }while (Instant.now().toEpochMilli() - time < timeOut * 1000);
        return false;
    }

    public static boolean unLock(String key) {
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.delete(key);
        } else {
            return false;
        }
    }

    public static void copyZSet(String sourceZSetKey, String destinationZSetKey) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        // 获取源ZSET的所有成员及分数
        Set<ZSetOperations.TypedTuple<Object>> sourceMembers = zSetOps.rangeWithScores(sourceZSetKey, 0, -1);

        // 将成员及分数复制到目标ZSET
        for (ZSetOperations.TypedTuple<Object> member : sourceMembers) {
            zSetOps.add(destinationZSetKey, member.getValue(), member.getScore());
        }
    }

    public static Object script(String script, List<String> keys, Object... args) {
        // 创建 RedisScript 对象
        RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
        // 执行 Lua 脚本
        return redisTemplate.execute(redisScript, keys, args);
    }

    public static Object scriptDouble(String script, List<String> keys, Object... args) {
        // 创建 RedisScript 对象
        RedisScript<Double> redisScript = RedisScript.of(script, Double.class);
        // 执行 Lua 脚本
        return redisTemplate.execute(redisScript, keys, args);
    }

    public static void publish(String topic, String message) {
        redisTemplate.convertAndSend(topic, message);
    }

    public static void main(String[] args) {
        redisTemplate.opsForList().leftPush("1", "2");
    }
}
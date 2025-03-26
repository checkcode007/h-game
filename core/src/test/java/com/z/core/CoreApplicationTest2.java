package com.z.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

@SpringBootTest
class CoreApplicationTest2 {
    static Table<Integer, Long, RankingCacheBo> table = HashBasedTable.<Integer, Long, RankingCacheBo>create();

    static Set<Long> set = new HashSet<>();
    static Set<Long> set1 = new HashSet<>();
    static long sum = 0l, vote = 0,sumCout =0;

    static Map<Long, Integer> itemMap = new HashMap<>();
    static Map<Long, Integer> scoreMap = new HashMap<>();

    static Map<Long, Long> countMap = new HashMap<>();

    static {
        itemMap.put(49009L, 77);
        itemMap.put(46003L, 99);
        itemMap.put(46002L, 199);
        itemMap.put(46001L, 333);
    }

//    static Map<Long, Long> priceMap = new HashMap<>();
//
//    static {
//        priceMap.put(49009L, 77);
//        itemMap.put(46003L, 99);
//        itemMap.put(46002L, 199);
//        itemMap.put(46001L, 333);
//    }

    static Map<Long, Long> voteMap = new HashMap<>();

    public static void main(String[] args) {
        String filePath = "/Users/mac/Downloads/PIC/dd19.log";  // 请替换为实际的文件路径
        readLogFile(filePath);
        filePath = "/Users/mac/Downloads/PIC/dd19_2.log";  // 请替换为实际的文件路径
        readLogFile(filePath);
        test1();
//        System.err.println("sum====>" + sum);
//        System.err.println("votes====>" + vote);
//        System.err.println("count====>" + sumCout);
//        countMap.forEach((k,v)->{
//            System.err.println("k:"+k+" v:"+v);
//        });
    }

    public static void readLogFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 处理每一行日志内容
//                System.err.println(line);
//                if(++i>10) break;
                RankingBo bo = parseRankingBo(line);
                if (bo == null) {
                    continue;
                }
                long uid = bo.getUid();
                long rid = bo.getRoomId();
                int type = bo.getType();
                long itemId = bo.getItemId();
                if (!itemMap.containsKey(itemId)) {
                    continue;
                }

                double score = bo.getScore();
                int votes = itemMap.get(itemId);
                int c = (int)score/votes;
                if(type !=2) continue;
//                votes= votes*c;
//                if (uid != 3552680) continue;
//                if (itemId != 49009) continue;

                voteMap.put(uid, voteMap.getOrDefault(uid, 0L) + votes);
                scoreMap.put(uid, scoreMap.getOrDefault(uid, 0) + (int) score);
                countMap.put(itemId, countMap.getOrDefault(itemId, 0L) + (long)score);
//                System.err.println("uid:" + uid + " itemId:" + bo.getItemId() + "type:" +bo.getType()+ " score:" + bo.getScore() + " votes:" + votes + " time:" + new Date(bo.getLastTime()));
                sum += bo.getScore();
                vote += votes;
//                set1.add(uid);
                addScore(bo);
                sumCout++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test1() {
        int index = 0;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (RankingCacheBo bo : table.row(RankingBigType.ROLE.getCode()).values()) {
            atomicInteger.getAndIncrement();
            long uid = bo.getUid();
            RankScore rankScore = bo.getRankScore();
            long score = (long)rankScore.getScore();
            Map<String, Object> fieldValues = new HashMap<>();
            fieldValues.put("diamond", scoreMap.getOrDefault(uid,0));
            fieldValues.put("votes", scoreMap.getOrDefault(uid, 0));
//                if(++index>20) continue;
            sql(uid, fieldValues);

        }
//        System.err.println(atomicInteger.get());
    }

    public static void sql(long uid, Map<String, Object> fieldValues) {
        // 构建查询条件
        String query = String.format("{ \"day\": 20250319, \"uid\": %d }", uid);

        // 构建更新语句
        StringBuilder updateFields = new StringBuilder();
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            if (updateFields.length() > 0) {
                updateFields.append(", ");
            }
            updateFields.append("\"").append(entry.getKey()).append("\": ").append(formatFieldValue(entry.getValue()));
        }

        // 构建完整的更新语句
        String update = "{ $set: { " + updateFields + " } }";


        // 拼接完整的 MongoDB 更新语句
        String mongoUpdateStatement = String.format(
                "db.getCollection(\"ramadan_rank\").update(%s, %s);",
                query, update
        );
        System.err.println(mongoUpdateStatement);
    }

    // 辅助方法，用于格式化不同类型的字段值
    private static String formatFieldValue(Object value) {
        if (value instanceof Integer) {
            return value.toString();
        } else if (value instanceof Long) {
            return value + "L"; // 在Java中表示Long类型，MongoDB通常不需要
        } else if (value instanceof Double) {
            return value.toString();
        } else if (value instanceof String) {
            return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
        } else {
            return "\"" + value.toString().replace("\"", "\\\"") + "\""; // 简单处理其他类型
        }
    }

    public static void addScore(RankingBo bo) {
        addRoleScore(bo);
    }


    public static void addRoleScore(RankingBo bo) {
        int type = bo.getType();
        long uid = bo.getUid();
        double score = bo.getScore();

        RankingCacheBo cacheBo = table.get(RankingBigType.ROLE.getCode(), uid);
        if (cacheBo == null) {
            cacheBo = new RankingCacheBo();
        }
        cacheBo.setType(RankingBigType.ROLE.getCode());
        cacheBo.setId(uid);
        cacheBo.setUid(uid);
        cacheBo.addScore(type, score);
        table.put(RankingBigType.ROLE.getCode(), uid, cacheBo);
    }

    public static RankingBo parseRankingBo(String log) {
        // 正则表达式用于提取日志中的字段
        String regex = "RankingBo\\(uid=(\\d+), roomId=(\\d+), name=(.*?), type=(\\d+), score=(\\d+\\.\\d+), itemId=(\\d+), lastTime=(\\d+), pkId=(\\d+), cfgId=(\\d+), hallStage=(\\d+), hallGroup=(\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            // 从日志中提取字段并转化为相应的对象属性
            long uid = Long.parseLong(matcher.group(1));
            long roomId = Long.parseLong(matcher.group(2));
            String name = matcher.group(3).equals("null") ? null : matcher.group(3); // 处理 null 值
            int type = Integer.parseInt(matcher.group(4));
            double score = Double.parseDouble(matcher.group(5));
            long itemId = Long.parseLong(matcher.group(6));
            long lastTime = Long.parseLong(matcher.group(7));
            long cfgId = Long.parseLong(matcher.group(9));

            // 创建 RankingBo 对象
            RankingBo rankingBo = new RankingBo();
            rankingBo.uid = uid;
            rankingBo.roomId = roomId;
            rankingBo.name = name;
            rankingBo.type = type;
            rankingBo.score = score;
            rankingBo.itemId = itemId;
            rankingBo.lastTime = lastTime;
            rankingBo.cfgId = cfgId;

            return rankingBo;
        } else {
            System.err.println("Log does not match the expected format: " + log);
//            throw new IllegalArgumentException("日志格式不匹配");
            return null;
        }
    }

}

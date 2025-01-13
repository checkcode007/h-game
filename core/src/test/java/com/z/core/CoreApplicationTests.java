package com.z.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

@SpringBootTest
class CoreApplicationTests {
   static Table<Integer, Long, RankingCacheBo> table = HashBasedTable.<Integer, Long, RankingCacheBo>create();

   static Set<Long> set = new HashSet<>();
    static Set<Long> set1 = new HashSet<>();
    public static void main(String[] args) {
        String filePath = "/Users/mac/Downloads/PIC/b_13.log";  // 请替换为实际的文件路径
        readLogFile(filePath);
        filePath = "/Users/mac/Downloads/PIC/b_13_2.log";  // 请替换为实际的文件路径
        readLogFile(filePath);
//        test1();

        String s = "5604283,5901300,4760464,3785580,5176390,5620388,4770115,5924678,5665152,5769587,4426879,5521012,3915300,5558914,5634520,5501850,5536456,4332818,5782510,3462443,5810987,5243912,5557932,5845538,666463,5908657,3566053,5619147,5923155,5071820,5830114,5162478,4885955,5196849,5638755,5854309,5313668,5689729,3975079,3756261,4726724,4455974,5557919,2892794,4159470,5673422,4246322,5521226,5618922,5558145,5545123,5558675,5618953,5613892,5270702,5618972,3331633,5808988,5068132,3551208,4999399,5575653,4048369,4579504,5485985,5346102,5883031,3580639,3559687,4017774,4103708,3098463,5834263,5105026,5650090,5715490,5473305,5017046,3256952,3510146,5099197,181136,5443752,5906073,5216173,4196920,5619310,5379095,5099993,5896773,827853,5920995,4801867,3420899,2853810,1753336,4637265,5002447,1889684,5410838,5915731,5858414,3414498,5127180,5130179,5857741,5575642,5575614,5860232,4330949,5060775,5863921,5251273,1400265,4030780,2329671,5890493,3569900,4785108,2423740,5419763,2648076,5344657,5608973,1623077,5924883,5618988,5559030,5030588,3935860,1204728,4109034,2398259,5445630,4185600,5915446,5774919,5592224,3806332,1595389,4978545,3612933,5636591,5299338,4811294,5013504,5372443,5575825,5897058,5923637,5520362,5162536,5903692,5628163,5559219,5878472,5918202,1439584,5563757,4366903,4352759,4395267,5693802,3438909,1622118,4115687,5619040,4000121,5159282,5247952,5258212,3450218,4111271,5075151,4739636,3786219,5896865,5556402,3839548,4483003,5882656,5223386,1659122,5731401,5521264,5558893,5412501,2601240,3296062,2592232,5700209,4035617,1475783,3279427,3657735,5483258,5619371,3390472,4420738,5670576,5619437,5404960,3415958,5832927,4214249,516715,5305579,5894798,5108305,5619249,4159682,2388663,510950,3763342,1039274,5884175,5708307,5664060,5037813,4918565,3870202,2550529,5679635,1125628,5817637,5845839,3159872,5756717,3566719,4006552,4765106,5701480,3650713,5559020,4850922,5309148,4376337,5669883,5670974,5672777,5254337,5179590,4361502,5894592,4326179,5925429,2246462,5542503,1492347,1500399,4951336,5513967,173337,5715406,4258441,5558023,5557891,4188478,5919892,5558262,4716301,5371667,5741901,5238154,5913536,5766073,4977772,2600614,1752849,4533171,5617950,5316216,5528595,5864638,5446374,4191739,3351882,4265436,5674679,5276166,5913200,5546217,5031582,5014996,3957091,5887250,5838132,5674696,5727695,5918395,5618079,3325466,5830826,5888126,4956012,5769414,4316273,5709174,2767456,5031816,5434418,5017606,4988444,4739690,5925435,1246168,5520038,5834362,2639564,5663924,5521110,5521043,5619182,5619118,5558699,5017564,5670084,4427310,1260413,5331735,4717117,5834344,5557868,5499555,5665107,5834421,5758150,5607085,5210987,4262959,5673129,5862653,5152431,4014000,5893774,5675983,4621869,4258662,3126623,4642292,5897917,5160056,3397802,5808876,5731680,5422988,5114688,5834172,2510538,3807780,2218166,2990780,5902204,4193686,4172746,4144901,4173657,4910994,5220961,5083405,4442154,4123104,5894248,5863901,2327592,5428992,3365804,3637165,4194216,499582,5557942,5558127,3705760,5795600,3703173,5368861,3888409,5670188,5127158,5374858,5528618,2409913,5521114,5878823,4142706,5665625,4173669,5521152,5246393,2355318,5921273,5521246,5918702,5521086,5912491,3643131,5610224,4911565,5389723,5557884,5842225,3902332,4807895,5910249,5132738,4203453,5558250,3504971,4416354,5558175,5510726,5756216,5894280,5665094,4715796,5833911,5770244,5584463,4685075,2733128,3472416,4117674,3570000,3570649,2536421,3574571,5860950,5521224,5629120,4374512,4791404,4072181,5585370,4238839,5871142,4248684,4238824,5619502,5911854,5076155,4037884,4012081,5864124,5761150,5619020,5041876,5619089,5538450,5833875,3720072,4307901,5751802,4117018,5736633,4739933,5103473,4848961,4958414,5679658,5817931,4100642,5808035,2956484,5114068,5381786";

        for (String ss : s.split(",")) {
            set.add(Long.parseLong(ss));
        }
        System.err.println(set.size());
        set.removeAll(set1);
        System.err.println("------------"+set.size());

        for (Long l : set) {
            System.err.println(l);
        }
    }

    public static void readLogFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 处理每一行日志内容
//                System.err.println(line);
//                if(++i>10) break;
                RankingBo bo = parseRankingBo(line);
                if(bo == null){ continue; }
                long uid = bo.getUid();
                long rid = bo.getRoomId();
                int type = bo.getType();
                double score = bo.getScore();
//                if(uid == 5589887L && type == 2){
//                    System.err.println("itemId:"+bo.getItemId() +" score:"+bo.getScore());
//                }
                set1.add(uid);
                addScore(bo);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static void  test1(){
        int index =0;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (RankingCacheBo bo : table.row(RankingBigType.ROLE.getCode()).values()) {
            atomicInteger.getAndIncrement();
            long uid = bo.getUid();
            RankScore rankScore =  bo.getRankScore();
            double diamond = rankScore.getDiamond();
            double pt = rankScore.getPt();
            double pay = rankScore.getPay();

            if(diamond>0){
//                if(++index>20) continue;
                sql(uid,"diamond",diamond);

            }
        }
        System.err.println(atomicInteger.get());
    }
    public static void sql(long uid,String name,double score){
        // 构建查询条件
        String query = String.format("{ \"day\": 20250113, \"uid\": %d }", uid);

        // 构建更新语句
        String update = String.format("{ $set: { \""+name+"\": %d } }", (long)score);

        // 拼接完整的 MongoDB 更新语句
        String mongoUpdateStatement = String.format(
                "db.getCollection(\"rankings_role\").update(%s, %s);",
                query, update
        );
        System.err.println(mongoUpdateStatement);
    }
    public static void addScore(RankingBo bo){
        addRoleScore(bo);
        addRoomScore(bo);
    }
    public static void ptable(){
        for (RankingCacheBo bo : table.row(RankingBigType.ROLE.getCode()).values()) {

        }
    }
    public static void addRoleScore(RankingBo bo){
        int type = bo.getType();
        long uid = bo.getUid();
        double score = bo.getScore();

        RankingCacheBo  cacheBo = table.get(RankingBigType.ROLE.getCode(),uid);
        if(cacheBo == null ){
            cacheBo = new RankingCacheBo();
        }
        cacheBo.setType(RankingBigType.ROLE.getCode());
        cacheBo.setId(uid);
        cacheBo.setUid(uid);
        cacheBo.addScore(type,score);
        table.put(RankingBigType.ROLE.getCode(),uid,cacheBo);
    }
    public static  void addRoomScore(RankingBo bo){
        int type = bo.getType();
        long uid = bo.getUid();
        double score = bo.getScore();
        long roomId = bo.getRoomId();
        if(roomId<1) return;
        RankingCacheBo  cacheBo = table.get(RankingBigType.ROOM.getCode(),roomId);
        if(cacheBo == null ){
            cacheBo = new RankingCacheBo();
        }
        cacheBo.setType(RankingBigType.ROOM.getCode());
        cacheBo.setId(roomId);
        cacheBo.setUid(uid);
        cacheBo.setRoomId(roomId);
        cacheBo.addScore(type,score);
        table.put(RankingBigType.ROOM.getCode(),roomId,cacheBo);
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
            long pkId = Long.parseLong(matcher.group(8));
            long cfgId = Long.parseLong(matcher.group(9));
            int hallStage = Integer.parseInt(matcher.group(10));
            int hallGroup = Integer.parseInt(matcher.group(11));

            // 创建 RankingBo 对象
            RankingBo rankingBo = new RankingBo();
            rankingBo.uid = uid;
            rankingBo.roomId = roomId;
            rankingBo.name = name;
            rankingBo.type = type;
            rankingBo.score = score;
            rankingBo.itemId = itemId;
            rankingBo.lastTime = lastTime;
            rankingBo.pkId = pkId;
            rankingBo.cfgId = cfgId;
            rankingBo.hallStage = hallStage;
            rankingBo.hallGroup = hallGroup;

            return rankingBo;
        } else {
            System.err.println("Log does not match the expected format: " + log);
//            throw new IllegalArgumentException("日志格式不匹配");
            return null;
        }
    }

    public static RankingCacheBo parseCache(String log) {
        String regex = "RankingCacheBo\\(type=(\\d+), id=(\\d+), uid=(\\d+), roomId=(\\d+), cfgId=(\\d+), rankScore=RankScore\\(score=(\\d+\\.\\d+), diamond=(\\d+\\.\\d+), pt=(\\d+\\.\\d+), pay=(\\d+\\.\\d+)\\)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            int type = Integer.parseInt(matcher.group(1));
            long id = Long.parseLong(matcher.group(2));
            long uid = Long.parseLong(matcher.group(3));
            long roomId = Long.parseLong(matcher.group(4));
            long cfgId = Long.parseLong(matcher.group(5));
            double score = Double.parseDouble(matcher.group(6));
            double diamond = Double.parseDouble(matcher.group(7));
            double pt = Double.parseDouble(matcher.group(8));
            double pay = Double.parseDouble(matcher.group(9));

            RankScore rankScore = new RankScore(score, diamond, pt, pay);

            RankingCacheBo cache = new RankingCacheBo();
            cache.setType(type);
            cache.setId(id);
            cache.setUid(uid);
            cache.setRoomId(roomId);
            cache.setCfgId(cfgId);
            cache.setRankScore(rankScore);

            return cache;
        } else {
            System.err.println(log);
            throw new IllegalArgumentException("日志格式不匹配");
        }
    }
}

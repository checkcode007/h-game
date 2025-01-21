//package com.z.core.test1;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.HashBasedTable;
//import com.google.common.collect.Table;
//import com.z.core.RankScore;
//import com.z.core.RankingBigType;
//import com.z.core.RankingBo;
//import com.z.core.RankingCacheBo;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@SpringBootTest
//class CoreApplicationTest2 {
//   static Table<Integer, Long, RankingCacheBo> table = HashBasedTable.<Integer, Long, RankingCacheBo>create();
//
//   static Set<Long> set = new HashSet<>();
//    static Set<Long> set1 = new HashSet<>();
//   static long sum = 0l;
//    public static void main(String[] args) {
//        String filePath = "/Users/mac/Downloads/PIC/test1.log";  // 请替换为实际的文件路径
//        readLogFile(filePath);
////        filePath = "/Users/mac/Downloads/PIC/a_19_2.log";  // 请替换为实际的文件路径
////        readLogFile(filePath);
////        filePath = "/Users/mac/Downloads/PIC/11_b.log";  // 请替换为实际的文件路径
////        readLogFile(filePath);
////        filePath = "/Users/mac/Downloads/PIC/11_b_2.log";  // 请替换为实际的文件路径
////        readLogFile(filePath);
////        test1();
//
////        String s = "5604283,5901300,4760464,3785580,5176390,5620388,4770115,5924678,5665152,5769587,4426879,5521012,3915300,5558914,5634520,5501850,5536456,4332818,5782510,3462443,5810987,5243912,5557932,5845538,666463,5908657,3566053,5619147,5923155,5071820,5830114,5162478,4885955,5196849,5638755,5854309,5313668,5689729,3975079,3756261,4726724,4455974,5557919,2892794,4159470,5673422,4246322,5521226,5618922,5558145,5545123,5558675,5618953,5613892,5270702,5618972,3331633,5808988,5068132,3551208,4999399,5575653,4048369,4579504,5485985,5346102,5883031,3580639,3559687,4017774,4103708,3098463,5834263,5105026,5650090,5715490,5473305,5017046,3256952,3510146,5099197,181136,5443752,5906073,5216173,4196920,5619310,5379095,5099993,5896773,827853,5920995,4801867,3420899,2853810,1753336,4637265,5002447,1889684,5410838,5915731,5858414,3414498,5127180,5130179,5857741,5575642,5575614,5860232,4330949,5060775,5863921,5251273,1400265,4030780,2329671,5890493,3569900,4785108,2423740,5419763,2648076,5344657,5608973,1623077,5924883,5618988,5559030,5030588,3935860,1204728,4109034,2398259,5445630,4185600,5915446,5774919,5592224,3806332,1595389,4978545,3612933,5636591,5299338,4811294,5013504,5372443,5575825,5897058,5923637,5520362,5162536,5903692,5628163,5559219,5878472,5918202,1439584,5563757,4366903,4352759,4395267,5693802,3438909,1622118,4115687,5619040,4000121,5159282,5247952,5258212,3450218,4111271,5075151,4739636,3786219,5896865,5556402,3839548,4483003,5882656,5223386,1659122,5731401,5521264,5558893,5412501,2601240,3296062,2592232,5700209,4035617,1475783,3279427,3657735,5483258,5619371,3390472,4420738,5670576,5619437,5404960,3415958,5832927,4214249,516715,5305579,5894798,5108305,5619249,4159682,2388663,510950,3763342,1039274,5884175,5708307,5664060,5037813,4918565,3870202,2550529,5679635,1125628,5817637,5845839,3159872,5756717,3566719,4006552,4765106,5701480,3650713,5559020,4850922,5309148,4376337,5669883,5670974,5672777,5254337,5179590,4361502,5894592,4326179,5925429,2246462,5542503,1492347,1500399,4951336,5513967,173337,5715406,4258441,5558023,5557891,4188478,5919892,5558262,4716301,5371667,5741901,5238154,5913536,5766073,4977772,2600614,1752849,4533171,5617950,5316216,5528595,5864638,5446374,4191739,3351882,4265436,5674679,5276166,5913200,5546217,5031582,5014996,3957091,5887250,5838132,5674696,5727695,5918395,5618079,3325466,5830826,5888126,4956012,5769414,4316273,5709174,2767456,5031816,5434418,5017606,4988444,4739690,5925435,1246168,5520038,5834362,2639564,5663924,5521110,5521043,5619182,5619118,5558699,5017564,5670084,4427310,1260413,5331735,4717117,5834344,5557868,5499555,5665107,5834421,5758150,5607085,5210987,4262959,5673129,5862653,5152431,4014000,5893774,5675983,4621869,4258662,3126623,4642292,5897917,5160056,3397802,5808876,5731680,5422988,5114688,5834172,2510538,3807780,2218166,2990780,5902204,4193686,4172746,4144901,4173657,4910994,5220961,5083405,4442154,4123104,5894248,5863901,2327592,5428992,3365804,3637165,4194216,499582,5557942,5558127,3705760,5795600,3703173,5368861,3888409,5670188,5127158,5374858,5528618,2409913,5521114,5878823,4142706,5665625,4173669,5521152,5246393,2355318,5921273,5521246,5918702,5521086,5912491,3643131,5610224,4911565,5389723,5557884,5842225,3902332,4807895,5910249,5132738,4203453,5558250,3504971,4416354,5558175,5510726,5756216,5894280,5665094,4715796,5833911,5770244,5584463,4685075,2733128,3472416,4117674,3570000,3570649,2536421,3574571,5860950,5521224,5629120,4374512,4791404,4072181,5585370,4238839,5871142,4248684,4238824,5619502,5911854,5076155,4037884,4012081,5864124,5761150,5619020,5041876,5619089,5538450,5833875,3720072,4307901,5751802,4117018,5736633,4739933,5103473,4848961,4958414,5679658,5817931,4100642,5808035,2956484,5114068,5381786";
////
////        for (String ss : s.split(",")) {
////            set.add(Long.parseLong(ss));
////        }
////        System.err.println(set.size());
////        set.removeAll(set1);
////        System.err.println("------------"+set.size());
////
////        for (Long l : set) {
////            System.err.println(l);
////        }
//    }
//
//    public static void readLogFile(String filePath) {
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // 处理每一行日志内容
////                System.err.println(line);
////                if(++i>10) break;
//                GiveGiftsBo bo = parseGiveGiftsBo(line);
//                if(bo == null){ continue; }
////                long uid = bo.getUid();
////                long rid = bo.getRoomId();
////                int type = bo.getType();
////                double score = bo.getScore();
////                if(uid == 5608973L && type == 2){
////                    System.err.println("itemId:"+bo.getItemId()+"type:" +" score:"+bo.getScore()+" time:"+new Date(bo.getLastTime()));
////                    sum += bo.getScore();
////                }
////                set1.add(uid);
////                addScore(bo);
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public  static void  test1(){
//        int index =0;
//        AtomicInteger atomicInteger = new AtomicInteger(0);
//        for (RankingCacheBo bo : table.row(RankingBigType.ROLE.getCode()).values()) {
//            atomicInteger.getAndIncrement();
//            long uid = bo.getUid();
//            RankScore rankScore =  bo.getRankScore();
//            double diamond = rankScore.getDiamond();
//            double pt = rankScore.getPt();
//            double pay = rankScore.getPay();
//
//            if(pay>0){
////                if(++index>20) continue;
//                sql(uid,"pay",pay);
//
//            }
//        }
//        System.err.println(atomicInteger.get());
//    }
//    public static void sql(long uid,String name,double score){
//        // 构建查询条件
//        String query = String.format("{ \"day\": 20250111, \"uid\": %d }", uid);
//
//        // 构建更新语句
//        String update = String.format("{ $set: { \""+name+"\": %d } }", (long)score);
//
//        // 拼接完整的 MongoDB 更新语句
//        String mongoUpdateStatement = String.format(
//                "db.getCollection(\"rankings_role\").update(%s, %s);",
//                query, update
//        );
//        System.err.println(mongoUpdateStatement);
//    }
//    public static void addScore(RankingBo bo){
//        addRoleScore(bo);
//        addRoomScore(bo);
//    }
//    public static void ptable(){
//        for (RankingCacheBo bo : table.row(RankingBigType.ROLE.getCode()).values()) {
//
//        }
//    }
//    public static void addRoleScore(RankingBo bo){
//        int type = bo.getType();
//        long uid = bo.getUid();
//        double score = bo.getScore();
//
//        RankingCacheBo  cacheBo = table.get(RankingBigType.ROLE.getCode(),uid);
//        if(cacheBo == null ){
//            cacheBo = new RankingCacheBo();
//        }
//        cacheBo.setType(RankingBigType.ROLE.getCode());
//        cacheBo.setId(uid);
//        cacheBo.setUid(uid);
//        cacheBo.addScore(type,score);
//        table.put(RankingBigType.ROLE.getCode(),uid,cacheBo);
//    }
//    public static  void addRoomScore(RankingBo bo){
//        int type = bo.getType();
//        long uid = bo.getUid();
//        double score = bo.getScore();
//        long roomId = bo.getRoomId();
//        if(roomId<1) return;
//        RankingCacheBo  cacheBo = table.get(RankingBigType.ROOM.getCode(),roomId);
//        if(cacheBo == null ){
//            cacheBo = new RankingCacheBo();
//        }
//        cacheBo.setType(RankingBigType.ROOM.getCode());
//        cacheBo.setId(roomId);
//        cacheBo.setUid(uid);
//        cacheBo.setRoomId(roomId);
//        cacheBo.addScore(type,score);
//        table.put(RankingBigType.ROOM.getCode(),roomId,cacheBo);
//    }
//
////    public static GiveGiftsBo parseRankingBo(String log) {
////        // 正则表达式用于提取日志中的字段
////        ObjectMapper objectMapper = new ObjectMapper();
////
////        try {
////            // 使用 ObjectMapper 反序列化 JSON 字符串为 GiveGiftsBo 对象
////            GiveGiftsBo giveGiftsBo = objectMapper.readValue(log, GiveGiftsBo.class);
////
////            // 输出转换后的对象，验证结果
////            System.out.println(giveGiftsBo.getNickName());
////            System.out.println(giveGiftsBo.getGiftId());
////            return giveGiftsBo;
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return null;
////    }
//
//    public static GiveGiftsBo parseGiveGiftsBo(String log) {
//        // 正则表达式用于提取日志中的字段
//        String regex = "GiveGiftsBo\\(userId=(\\d+), nickName=(.*?), toUserIds=\\[(.*?)\\], realIds=\\{(.*?)\\}, gName=(.*?), giftId=(\\d+), gDiamondPrice=(\\d+), gCoinPrice=(\\d+), gCount=(\\d+), gExp=(\\d+), gwExp=(\\d+), baoxiangrealPocket=(\\d+), realPocket=(\\d+), noRealPocket=(\\d+), glamour=(\\d+), gAttack=(\\d+), giftUrl=(.*?), gHierarchy=(\\d+), giftRecordIds=\\{(.*?)\\}, type=(\\d+), bagGift=(true|false), clientVer=(.*?), roomId=(\\d+), roomName=(.*?), realAttack=(\\d+), rankingListVal=(\\d+), serverBroadcast=(\\d+), gQuality=(\\d+), source=(\\d+), baoxiangId=(\\d+), bxId=(\\d+), bxDiamondP=(\\d+), bxCoinP=(\\d+), isKnapsack=(true|false), callId=(.*?), lastTime=(\\d+), ipCC=(.*?)}";
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(log);
//
//        if (matcher.find()) {
//            // 提取各个字段
//            long userId = Long.parseLong(matcher.group(1));
//            String nickName = matcher.group(2);
//            List<Long> toUserIds = parseLongList(matcher.group(3));  // 转换 toUserIds
//            Map<Long, Boolean> realIds = parseRealIds(matcher.group(4));  // 转换 realIds
//            String gName = matcher.group(5);
//            long giftId = Long.parseLong(matcher.group(6));
//            int gDiamondPrice = Integer.parseInt(matcher.group(7));
//            int gCoinPrice = Integer.parseInt(matcher.group(8));
//            int gCount = Integer.parseInt(matcher.group(9));
//            int gExp = Integer.parseInt(matcher.group(10));
//            int gwExp = Integer.parseInt(matcher.group(11));
//            long baoxiangrealPocket = Long.parseLong(matcher.group(12));
//            long realPocket = Long.parseLong(matcher.group(13));
//            long noRealPocket = Long.parseLong(matcher.group(14));
//            long glamour = Long.parseLong(matcher.group(15));
//            int gAttack = Integer.parseInt(matcher.group(16));
//            String giftUrl = matcher.group(17);
//            int gHierarchy = Integer.parseInt(matcher.group(18));
//            Map<Long, String> giftRecordIds = parseGiftRecordIds(matcher.group(19));  // 转换 giftRecordIds
//            int type = Integer.parseInt(matcher.group(20));
//            boolean bagGift = Boolean.parseBoolean(matcher.group(21));
//            String clientVer = matcher.group(22);
//            long roomId = Long.parseLong(matcher.group(23));
//            String roomName = matcher.group(24).equals("null") ? null : matcher.group(24);
//            int realAttack = Integer.parseInt(matcher.group(25));
//            int rankingListVal = Integer.parseInt(matcher.group(26));
//            int serverBroadcast = Integer.parseInt(matcher.group(27));
//            int gQuality = Integer.parseInt(matcher.group(28));
//            int source = Integer.parseInt(matcher.group(29));
//            long baoxiangId = Long.parseLong(matcher.group(30));
//            long bxId = Long.parseLong(matcher.group(31));
//            int bxDiamondP = Integer.parseInt(matcher.group(32));
//            int bxCoinP = Integer.parseInt(matcher.group(33));
//            boolean isKnapsack = Boolean.parseBoolean(matcher.group(34));
//            String callId = matcher.group(35);
//            long lastTime = Long.parseLong(matcher.group(36));
//            String ipCC = matcher.group(37);
//
//            // 创建 GiveGiftsBo 对象并填充数据
//            GiveGiftsBo giveGiftsBo = new GiveGiftsBo();
//            giveGiftsBo.setUserId(userId);
//            giveGiftsBo.setNickName(nickName);
//            giveGiftsBo.setToUserIds(toUserIds);
//            giveGiftsBo.setRealIds(realIds);
//            giveGiftsBo.setgName(gName);
//            giveGiftsBo.setGiftId(giftId);
//            giveGiftsBo.setgDiamondPrice(gDiamondPrice);
//            giveGiftsBo.setgCoinPrice(gCoinPrice);
//            giveGiftsBo.setgCount(gCount);
//            giveGiftsBo.setgExp(gExp);
//            giveGiftsBo.setGwExp(gwExp);
//            giveGiftsBo.setBaoxiangrealPocket(baoxiangrealPocket);
//            giveGiftsBo.setRealPocket(realPocket);
//            giveGiftsBo.setNoRealPocket(noRealPocket);
//            giveGiftsBo.setGlamour(glamour);
//            giveGiftsBo.setgAttack(gAttack);
//            giveGiftsBo.setGiftUrl(giftUrl);
//            giveGiftsBo.setgHierarchy(gHierarchy);
//            giveGiftsBo.setGiftRecordIds(giftRecordIds);
//            giveGiftsBo.setType(type);
//            giveGiftsBo.setBagGift(bagGift);
//            giveGiftsBo.setClientVer(clientVer);
//            giveGiftsBo.setRoomId(roomId);
//            giveGiftsBo.setRoomName(roomName);
//            giveGiftsBo.setRealAttack(realAttack);
//            giveGiftsBo.setRankingListVal(rankingListVal);
//            giveGiftsBo.setServerBroadcast(serverBroadcast);
//            giveGiftsBo.setgQuality(gQuality);
//            giveGiftsBo.setSource(source);
//            giveGiftsBo.setBaoxiangId(baoxiangId);
//            giveGiftsBo.setBxId(bxId);
//            giveGiftsBo.setBxDiamondP(bxDiamondP);
//            giveGiftsBo.setBxCoinP(bxCoinP);
//            giveGiftsBo.setKnapsack(isKnapsack);
//            giveGiftsBo.setCallId(callId);
//            giveGiftsBo.setLastTime(lastTime);
//            giveGiftsBo.setIpCC(ipCC);
//            System.err.println(giveGiftsBo.toString());
//            return giveGiftsBo;
//        } else {
//            System.err.println("Log does not match the expected format: " + log);
//            return null;
//        }
//    }
//
//    // 辅助方法：转换 toUserIds 字符串为 List<Long>
//    private static List<Long> parseLongList(String str) {
//        List<Long> list = new ArrayList<>();
//        if (str != null && !str.isEmpty()) {
//            String[] ids = str.split(",");
//            for (String id : ids) {
//                list.add(Long.parseLong(id.trim()));
//            }
//        }
//        return list;
//    }
//
//    // 辅助方法：转换 realIds 字符串为 Map<Long, Boolean>
//    private static Map<Long, Boolean> parseRealIds(String str) {
//        Map<Long, Boolean> map = new HashMap<>();
//        if (str != null && !str.isEmpty()) {
//            String[] entries = str.split(",");
//            for (String entry : entries) {
//                String[] keyValue = entry.split("=");
//                if (keyValue.length == 2) {
//                    map.put(Long.parseLong(keyValue[0].trim()), Boolean.parseBoolean(keyValue[1].trim()));
//                }
//            }
//        }
//        return map;
//    }
//
//    // 辅助方法：转换 giftRecordIds 字符串为 Map<Long, String>
//    private static Map<Long, String> parseGiftRecordIds(String str) {
//        Map<Long, String> map = new HashMap<>();
//        if (str != null && !str.isEmpty()) {
//            String[] entries = str.split(",");
//            for (String entry : entries) {
//                String[] keyValue = entry.split("=");
//                if (keyValue.length == 2) {
//                    map.put(Long.parseLong(keyValue[0].trim()), keyValue[1].trim());
//                }
//            }
//        }
//        return map;
//    }
//}

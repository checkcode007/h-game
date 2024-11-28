package com.z.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NameUtils {

    // 中文姓氏库
    private static final List<String> CHINESE_SURNAMES = Arrays.asList(
            "张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴",
            "孙", "郑", "何", "马", "郭", "冯", "许", "蔡", "邓", "杜",
            "龚", "傅", "彭", "韩", "吕", "姚", "林", "高", "魏", "廖"
    );

    // 中文名字库（男性）
    private static final List<String> CHINESE_MALE_NAMES = Arrays.asList(
            "伟", "强", "超", "磊", "俊", "明", "昊", "博", "星", "君",
            "海", "军", "斌", "巍", "宇", "颖", "凯", "昌", "华", "诚",
            "义", "德", "彦", "潇", "阳", "智", "韬", "博文", "亦凡", "思远"
    );

    // 中文名字库（女性）
    private static final List<String> CHINESE_FEMALE_NAMES = Arrays.asList(
            "娜", "婷", "丽", "英", "芳", "洁", "珊", "玲", "梦", "欣",
            "丽萍", "娜娜", "婧", "慧", "雯", "芸", "佳", "琴", "薇", "静",
            "明霞", "若颖", "淑慧", "嘉怡", "美琳", "瑾萱", "雨婷"
    );

    // 英文名字库（男性）
    private static final List<String> ENGLISH_MALE_NAMES = Arrays.asList(
            "John", "Michael", "James", "David", "Robert", "William", "Daniel",
            "Joseph", "Thomas", "Charles", "Benjamin", "Henry", "Jack", "Samuel",
            "Peter", "Andrew", "Matthew", "George", "Richard", "Christopher"
    );

    // 英文名字库（女性）
    private static final List<String> ENGLISH_FEMALE_NAMES = Arrays.asList(
            "Alice", "Emma", "Sarah", "Olivia", "Sophia", "Lily", "Grace", "Anna",
            "Chloe", "Isabelle", "Charlotte", "Amelia", "Ruby", "Emily", "Jessica",
            "Victoria", "Hannah", "Lucy", "Elizabeth", "Ava"
    );
    // 字母和数字的字符集
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 随机数生成器
    private static final Random RANDOM = new Random();

    // 随机生成中文名
    public static String generateChineseName(boolean isMale) {
        String surname = CHINESE_SURNAMES.get(RANDOM.nextInt(CHINESE_SURNAMES.size()));
        String name = isMale ? CHINESE_MALE_NAMES.get(RANDOM.nextInt(CHINESE_MALE_NAMES.size())) :
                CHINESE_FEMALE_NAMES.get(RANDOM.nextInt(CHINESE_FEMALE_NAMES.size()));
        return surname + name;
    }

    // 随机生成英文名
    public static String generateEnglishName(boolean isMale) {
        return isMale ? ENGLISH_MALE_NAMES.get(RANDOM.nextInt(ENGLISH_MALE_NAMES.size())) :
                ENGLISH_FEMALE_NAMES.get(RANDOM.nextInt(ENGLISH_FEMALE_NAMES.size()));
    }

    // 随机生成中英文组合名字
    public static String generateRandomName() {
        boolean isChinese = RANDOM.nextBoolean();
        boolean isMale = RANDOM.nextBoolean(); // 随机选择性别
        boolean isChar = RANDOM.nextBoolean(); // 随机选择性别
        if(isChar){
            int num = RANDOM.nextInt(6,10);
            return generateRandomString(num);
        }
        if (isChinese) {
            return generateChineseName(isMale);
        } else {
            return generateEnglishName(isMale);
        }

    }


    // 生成指定长度的随机字符串
    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 从字符集ALPHABET中随机选择一个字符
            int index = RANDOM.nextInt(ALPHABET.length());
            result.append(ALPHABET.charAt(index));
        }
        return result.toString();
    }

    // 测试生成
    public static void main(String[] args) {
        System.out.println("随机生成的中英文名字：");
        for (int i = 0; i < 100; i++) {
            int num = RANDOM.nextInt(6,10);
            System.out.println(generateRandomString(num));
        }
    }
}

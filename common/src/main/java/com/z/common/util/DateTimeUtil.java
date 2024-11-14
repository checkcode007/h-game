package com.z.common.util;

import cn.hutool.core.date.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 * @author zcj
 */
public class DateTimeUtil {
    public  final static long DAY_SECOND = 86400;
    public  final static long DAY_MILLS = 86400000;
    public  final static long HOUR_MILLS = 3600000;
    public  final static long MINUTE_MILLS = 60000;
    private static DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter SHORT_DTF = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter SHORT1_DTF = DateTimeFormat.forPattern("yyyyMMdd");
    private static DateTimeFormatter YM_DTF = DateTimeFormat.forPattern("yyyyMM");
    private static DateTimeFormatter Y_DTF = DateTimeFormat.forPattern("yyyy");
    private static final String FORMATE_DATE = "yyyy-MM-dd";
    private static final String FORMATE_SECONDS = "HH:mm:ss";
    private static final String FORMATE_SHORT1 = "yyyyMMdd";
    public static final String FORMATE_FULL = FORMATE_DATE.concat(" ").concat(FORMATE_SECONDS);
    public static final DateTime MIN = new DateTime( 0000, 1, 1, 0, 0, 0, DateTimeZone.UTC );
    public static final DateTime MAX= new DateTime( 9999, 1, 1, 0, 0, 0, DateTimeZone.UTC );

    public static DateTime toDateTime(String dateStr) {
        return DTF.parseDateTime(dateStr);
    }

    public static DateTime short1ToTime(String dateStr) {
        return SHORT1_DTF.parseDateTime(dateStr);
    }

    public static DateTime short1ToTime(int day) {
        return SHORT1_DTF.parseDateTime(String.valueOf(day));
    }

    public static long toLong(String dateStr) {
        return DTF.parseDateTime(dateStr).getMillis();
    }

    public static DateTime toShortDateTime(String dateStr) {
        return SHORT_DTF.parseDateTime(dateStr);
    }

    public static String timeToStr(DateTime date) {
        return date.toString(DTF);
    }

    public static String timeToShortStr(DateTime date) {
        return date.toString(SHORT_DTF);
    }

    public static DateTime getDayStart(DateTime date) {
        return date.withTimeAtStartOfDay();
    }

    public static DateTime getDayEnd(DateTime date) {
        return date.millisOfDay().withMaximumValue();
    }

    public static DateTime getDayEndAfter10Second(DateTime date) {
        return getDayEnd(date).plusSeconds(10);
    }

    public static DateTime getTodayStart() {
        return DateTime.now().withTimeAtStartOfDay();
    }

    public static String getTodayStartStr() {
        return DateTime.now().withTimeAtStartOfDay().toString(FORMATE_FULL);
    }

    public static DateTime getTodayEnd() {
        return DateTime.now().millisOfDay().withMaximumValue();
    }

    public static String getTodayEndStr() {
        return DateTime.now().millisOfDay().withMaximumValue().toString(FORMATE_FULL);
    }

    public static Date getTodayDayZeroDate() {
        return  DateTime.parse(DateTime.now().toString("yyyy-MM-dd")).toDate();
    }

    public static DateTime getTodayDayZeroDateTime() {
        return  DateTime.parse(DateTime.now().toString("yyyy-MM-dd"));
    }

    public static DateTime getZeroDateTime(DateTime date) {
        return  DateTime.parse(date.toString("yyyy-MM-dd"));
    }

    public static Date getNextDayZeroDate() {
        return  DateTime.parse(DateTime.now().plusDays(1).toString("yyyy-MM-dd")).toDate();
    }

    public static Date plusDay(int num,String formate) {
        return  DateTime.parse(DateTime.now().plusDays(num).toString(formate)).toDate();
    }

    public static String getTodayDayZeroStr() {
        return  DateTime.now().toString("yyyy-MM-dd");
    }

    public static String getNextDayZeroStr() {
        return DateTime.now().plusDays(1).toString("yyyy-MM-dd");
    }

    public static String getYesTodayZeroStr() {
        return  DateTime.now().plusDays(-1).toString("yyyy-MM-dd");
    }

    public static String getYesTodayEndStr() {
        return DateTime.now().toString("yyyy-MM-dd");
    }

    public static long getNextDayZero() {
        return  DateTime.parse(DateTime.now().plusDays(1).toString("yyyy-MM-dd")).getMillis();
    }

    public static long getYesterdayStartTime() {
        return  DateTime.parse(DateTime.now().plusDays(-1).toString("yyyy-MM-dd")).getMillis();
    }

    /**
     * 日期加减
     * @param num 数量
     * @return 获得计算后0点得时间戳
     */
    public static long plusDaysGetZeroMillis(int num) {
        return  DateTime.parse(DateTime.now().plusDays(num).toString("yyyy-MM-dd")).getMillis();
    }

    public static long getYesterdayEndTime() {
        return  DateTime.parse(DateTime.now().toString("yyyy-MM-dd")).getMillis();
    }

    /**
     * 日期加减
     * @param num 数量
     * @param format 日期格式
     */
    public static String plusDaysFormat(int num, String format) {
        return DateTime.now().plusDays(num).toString(format);
    }

    public static Date dateTime(String format, String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *获取时间末
     * @param date
     * @param amount 0为今天
     * @return
     */
    public static Date getTimeEnd(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 重置时分秒
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    /**
     *获取时间始
     * @param date
     * @param amount 0为今天
     * @return
     */
    public static Date getTimeStart(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 重置时分秒
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    public static int diffDays(DateTime dateTime1, DateTime dateTime2) {
        return Days.daysBetween(dateTime1, dateTime2).getDays();
    }

    public static int diffDays(int day1, int day2) {
        return Days.daysBetween(short1ToTime(day1),short1ToTime(day2)).getDays();
    }

    public static boolean isSameDay(DateTime dateTime1, DateTime dateTime2) {
        return dateTime1.getYear() == dateTime2.getYear() && dateTime1.getDayOfYear() == dateTime2.getDayOfYear();
    }

    public static int getDay() {
        return DateTime.now().getDayOfYear();
    }

    public static int minusDay(int day) {
        return DateTime.now().minusDays(day).getDayOfYear();
    }

    public static int getWeek() {
        return DateTime.now().getWeekOfWeekyear();
    }

    public static int minusWeeks(int week) {
        return DateTime.now().minusWeeks(week).getWeekOfWeekyear();
    }

    public static DateTime getGMCMinius4D() {
         return new DateTime().withZone(DateTimeZone.forID("Etc/GMT-4"));
    }

    public static DateTime getGMCMinius3D() {
        return new DateTime().withZone(getGMC3Zone());
    }

    public static DateTime getGMCMinius3D(DateTime dateTime) {
        return dateTime.withZone(getGMC3Zone());
    }

    public static DateTime getGMC3() {
        return new DateTime().plusHours(-5);
    }

    public static DateTimeZone getGMC3Zone() {
        return DateTimeZone.forID("Etc/GMT-3");
    }

    public static DateTimeZone getGMC8Zone() {
        return DateTimeZone.forID("Etc/GMT-8");
    }

    public static int getDay(DateTime dateTime) {
        return dateTime.getDayOfYear();
    }

    public static int minusDay(DateTime dateTime, int day) {
        return dateTime.minusDays(day).getDayOfYear();
    }

    public static int getWeek(DateTime dateTime) {
        return dateTime.getWeekOfWeekyear();
    }

    public static int minusWeeks(DateTime dateTime, int week) {
        return dateTime.minusWeeks(week).getWeekOfWeekyear();
    }

    public static int getMonth(DateTime dateTime) {
        return dateTime.getMonthOfYear();
    }

    public static int minusMonths(DateTime dateTime, int month) {
        return dateTime.minusMonths(month).getMonthOfYear();
    }

    public static DateTime getMondayStart(DateTime dateTime) {
        return dateTime.withDayOfWeek(DateTimeConstants.MONDAY).millisOfDay().withMinimumValue();
    }

    public static DateTime getSunDayEnd(DateTime dateTime) {
        return dateTime.withDayOfWeek(DateTimeConstants.SUNDAY).withHourOfDay(23).millisOfDay().withMaximumValue();
    }

    public static DateTime todayBeginTime() {
        DateTime dateTime = DateTime.now();
        return dateTime.withMillisOfDay(0);
    }

    public static DateTime monthStart() {
        DateTime dateTime = DateTime.now();
        return dateTime.dayOfMonth().withMinimumValue().millisOfDay().withMinimumValue();
    }

    public static DateTime monthEnd() {
        DateTime dateTime = DateTime.now();
        return dateTime.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue();
    }

    public static DateTime monthStart(DateTime dateTime) {
        return dateTime.dayOfMonth().withMinimumValue().millisOfDay().withMinimumValue();
    }

    public static DateTime monthEnd(DateTime dateTime) {
        return dateTime.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue();
    }

    public static int monthStartShort() {
        DateTime dateTime = DateTime.now();
        return Integer.parseInt(dateTime.dayOfMonth().withMinimumValue().toString(SHORT1_DTF));
    }

    public static int monthEndShort() {
        DateTime dateTime = DateTime.now();
        return Integer.parseInt(dateTime.dayOfMonth().withMaximumValue().toString(SHORT1_DTF));
    }

    public static String format(DateTime dateTime) {
        return dateTime.toString(DTF);
    }

    public static String getDateShortStr() {
        DateTime dateTime = DateTime.now();
        return dateTime.toString(SHORT1_DTF);
    }

    public static String getYearMonthStr() {
        DateTime dateTime = DateTime.now();
        return dateTime.toString(YM_DTF);
    }

    public static String getYearStr() {
        DateTime dateTime = DateTime.now();
        return dateTime.toString(Y_DTF);
    }

    public static String getYearStrMinus() {
        DateTime dateTime = DateTime.now().minusDays(1);
        return dateTime.toString(Y_DTF);
    }

    /**
     * 获取上周1和上周日
     * @return LocalDate[]
     */
    public static LocalDate[] getPreviousLastWeek() {
        LocalDate date = LocalDate.now();
        final int dayOfWeek = date.getDayOfWeek().getValue();
        final LocalDate from = date.minusDays(dayOfWeek + 6); // (dayOfWeek - 1) + 7
        final LocalDate to = date.minusDays(dayOfWeek);
        return new LocalDate[]{from, to};
    }

    /**
     * 获取本周1和本周日
     * @return
     */
    public static LocalDate[] getPreviousWeek() {
        LocalDate date = LocalDate.now();
        final int dayOfWeek = date.getDayOfWeek().getValue();
        final LocalDate from = date.minusDays(dayOfWeek -1); // (dayOfWeek - 1) + 7
        final LocalDate to = date.minusDays(dayOfWeek-7);
        return new LocalDate[]{from, to};
    }

    public static String getPreviousWeekStr(LocalDate date,String formateStr) {
        return date.format(java.time.format.DateTimeFormatter.ofPattern(formateStr));
    }
    
    public static Date weekLocalToDate(LocalDate localDate) {
        String previousWeekStartTime = DateTimeUtil.getPreviousWeekStr(localDate,"yyyy-MM-dd");
        String[] split = previousWeekStartTime.split("-");
        LocalDate of = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        ZoneId zone = ZoneId.of("UTC+8");
        Instant instant = of.atStartOfDay().atZone(zone).toInstant();
        return new Date(instant.toEpochMilli());
    }

    public static int getYearMonthInt(DateTime dateTime) {
        return Integer.valueOf(dateTime.toString(YM_DTF));
    }
    
    public static String getYearMonthStr(DateTime dateTime) {
        return dateTime.toString(YM_DTF);
    }
    
    public static DateTime getYearMonthDate(String string) {
        return YM_DTF.parseDateTime(string);
    }
    
    public static String getDateShortStr(DateTime dateTime ) {
        return dateTime.toString(SHORT1_DTF);
    }
    
    public static int getDateShortInt() {
        return Integer.parseInt(getDateShortStr());
    }
    
    public static int getDateShortInt(DateTime dateTime) {
        return Integer.parseInt(getDateShortStr(dateTime));
    }

    public static boolean isSameMonth(DateTime dateTime1,DateTime dateTime2) {
        if(dateTime1.getYear() == dateTime2.getYear() && dateTime1.getMonthOfYear() == dateTime2.getMonthOfYear()) {
            return true;
        }
        return false;
    }

    /**
     * 月份加减
     * @param num 增加的月份
     * @return yyyyMM
     */
    public static String plusYearMonthStr(int num) {
        java.time.YearMonth currentYearMonth = java.time.YearMonth.now();
        java.time.format.DateTimeFormatter formatter  = java.time.format.DateTimeFormatter.ofPattern("yyyyMM");
        java.time.YearMonth result = currentYearMonth.plusMonths(num);
        return result.format(formatter);
    }

    public static DateTime format(String string) {
        return DateTime.parse(string, SHORT1_DTF);
    }
    
    public static long getTimePlusDays(int dayNum) {
        Instant now = Instant.now();
        Instant thirtyDaysAgo = now.plus(dayNum, ChronoUnit.DAYS);
        return thirtyDaysAgo.toEpochMilli();
    }
    
    public static Date toZone3Date(Date originalDate) {
        Date utcDate = DateUtil.convertTimeZone(originalDate, TimeZone.getTimeZone("UTC"));
        return DateUtil.convertTimeZone(originalDate, TimeZone.getTimeZone("Etc/GMT-3"));
    }

    public static void main(String[] args) {
//        System.err.println(new DateTime(0));
//        System.err.println(new Date(0));
    }
}

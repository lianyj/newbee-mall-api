package ltd.newbee.mall.util;


import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    /**
     * 日期格式 精确到秒 ：yyyyMMddHHmmss
     */
    public static final String DATE_FORMDATE_TO_SECOND = "yyyyMMddHHmmss";

    /**
     * 默认时间格式，精确到秒：
     */
    public static final String DEFAULT_TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认时间格式，精确到日：
     */
    public static final String DEFAULT_DATE_FORMATE = "yyyy-MM-dd";

    /**
     * LocalDateTime转毫秒时间戳
     * @param localDateTime LocalDateTime
     * @return 时间戳
     */
    public static Long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zoneId).toInstant();
            return instant.toEpochMilli();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间戳转LocalDateTime
     * @param timestamp 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        try {
            Instant instant = Instant.ofEpochMilli(timestamp);
            ZoneId zone = ZoneId.systemDefault();
            return LocalDateTime.ofInstant(instant, zone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Date转LocalDateTime
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        try {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * LocalDateTime转Date
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            return Date.from(zdt.toInstant());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取一天中凌晨时间
     * @param date
     * @return
     */
    public final static Date getDayBeginTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date start = calendar.getTime();
        return start;
    }


    /**
     * 获取一天中最晚的时间
     * @param date
     * @return
     */
    public final static Date getDayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date start = calendar.getTime();
        return start;
    }

    /**
     * 将当前日期按格式转字符串
     *
     * @return
     */
    public static String getCurrentDate() {
        return format(new Date(), DEFAULT_DATE_FORMATE);
    }

    /**
     * 将当前日期按格式转字符串
     *
     * @return
     */
    public static String getCurrentDateTime() {
        return format(new Date(), DEFAULT_TIME_FORMATE);
    }


    /**
     * 日期型按格式转字符串
     *
     * @param date
     * @return
     */
    public final static String formatDateTime(Date date) {
        return format(date, DEFAULT_TIME_FORMATE);
    }

    public final static String formatDate(Date date) {
        return format(date, DEFAULT_DATE_FORMATE);
    }

    /**
     * 日期型按格式转字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public final static String format(Date date, String pattern) {
        if (null == date || null == pattern)
            return "";
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 增加天
     * @param pDate
     * @param day
     * @return
     */
    public final static Date addDay(Date pDate, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + day);
        return c.getTime();
    }

    /**
     * 增加小时
     * @param pDate
     * @param hour
     * @return
     */
    public final static Date addHour(Date pDate, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + hour);
        return c.getTime();
    }

    /**
     * 增加分
     * @param pDate
     * @param minute
     * @return
     */
    public final static Date addMinute(Date pDate, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(pDate);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minute);
        return c.getTime();
    }

    public final static Date parseDateTime(String dateStr) {
        return parse(dateStr, DEFAULT_TIME_FORMATE);
    }

    /**
     * 日期型按格式转字符串
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public final static Date parse(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(pattern))
            return null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(dateStr);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public final static int getDifferDays(String startDate, String endDate) throws Exception {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            long startTime = df.parse(startDate).getTime();// 当前的时间
            long endTime = df.parse(endDate).getTime();// 结束时间
            long diff = endTime - startTime;
            long dd = (long) 1000 * 24 * 60 * 60;// 一天的毫秒数
            if (diff < 0) {
                return 0;
            } else {
                int d = (int) (diff / dd) + 1;
                return d;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}

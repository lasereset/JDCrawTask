package com.dianming.jd.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IntelliDate {

    // 时间精确到分
    private static final SimpleDateFormat TimeMinute = new SimpleDateFormat("H时m分", Locale.CHINA);

    private static final SimpleDateFormat YHD_HMS = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.CHINA);

    // 全部
    private static final SimpleDateFormat TimeFormat = new SimpleDateFormat("yy年M月d日H时m分s秒", Locale.CHINA);

    // 无秒,无年
    private static final SimpleDateFormat TimeFormatNoSecondYear = new SimpleDateFormat("M月d日H时m分", Locale.CHINA);

    private static int daysBetween(Calendar cal1, Calendar cal2) {

        long time1 = cal1.getTimeInMillis();

        long time2 = cal2.getTimeInMillis();
        return (int) Math.abs((time2 - time1) / (1000 * 3600 * 24));
    }

    public static int daysTo(Date date) {
        return (int) Math.abs((date.getTime() - System.currentTimeMillis()) / (1000 * 3600 * 24));
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isSameYear(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }


    public static Date formatYMDhms(String dateStr) {
        try {
            return YHD_HMS.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 持续时间
    public static String getPassedTime(Date begin, Date end) {

        try {
            long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long min = between % (24 * 3600) % 3600 / 60;
            StringBuilder sb = new StringBuilder();
            if (0l != day) {
                sb.append(day + "天");
            }
            if (0l != hour) {
                sb.append(hour + "小时");
            }
            if (0l != min) {
                sb.append(min + "分钟");
            }
            if (sb.length() == 0) {
                return "约1分钟";
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 智能显示
    public static String getDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Calendar now = Calendar.getInstance();
        if (daysBetween(c, now) > 1) {
            // 相隔一天以上
            if (isSameYear(c, now)) {
                // 今年
                return TimeFormatNoSecondYear.format(date);
            } else {
                return TimeFormat.format(date);
            }
        } else {
            // 要么昨天,要么今天
            if (isSameDay(c, now)) {
                // 今天
                return TimeMinute.format(date);// formatDate(date, TimeMinute, 25L * ONE_HOUR);
            } else {
                // return "昨天" + TimeMinute.format(date);
                return TimeFormatNoSecondYear.format(date);
            }
        }
    }

    public static String format(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static Date getTodayStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getTodayEnd() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static int getDate() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }

    // 获得当天0点时间
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();

    }

    // 获得昨天0点时间
    public static Date getYesterdaymorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning().getTime() - 3600 * 24 * 1000);
        return cal.getTime();
    }

    // 获得当天近7天时间
    public static Date getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning().getTime() - 3600 * 24 * 1000 * 7);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekmorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    public static Date getLastMonthStartMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesMonthmorning());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     * 
     * @return Date
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }

    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    public static Date getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }
}

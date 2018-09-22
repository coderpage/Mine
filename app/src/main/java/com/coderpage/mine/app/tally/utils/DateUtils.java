package com.coderpage.mine.app.tally.utils;

import java.util.Calendar;

/**
 * @author lc. 2018-07-08 21:27
 * @since 0.6.0
 */

public class DateUtils {

    /** 返回当月开始时间 */
    public static long currentMonthStartUnixTime() {
        Calendar monthStartCalendar = Calendar.getInstance();
        monthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
        monthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        monthStartCalendar.set(Calendar.MINUTE, 0);
        monthStartCalendar.set(Calendar.SECOND, 0);
        return monthStartCalendar.getTimeInMillis();
    }

    /** 返回今天开始时间 */
    public static long todayStartUnixTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        return todayStart.getTimeInMillis();
    }

    /** 返回今天结束时间 */
    public static long todayEndUnixTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 23);
        todayStart.set(Calendar.MINUTE, 59);
        todayStart.set(Calendar.SECOND, 59);
        todayStart.set(Calendar.MILLISECOND, 999);
        return todayStart.getTimeInMillis();
    }
}

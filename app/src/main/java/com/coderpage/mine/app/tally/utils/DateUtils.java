package com.coderpage.mine.app.tally.utils;

import android.util.Pair;

import java.util.Calendar;

/**
 * @author lc. 2018-07-08 21:27
 * @since 0.6.0
 */

public class DateUtils {

    /**
     * 返回指定月份开始和结束时间 unix ms
     *
     * @param year  年份
     * @param month 月份 1~12
     * @return 返回时间区间
     */
    public static Pair<Long, Long> monthDateRange(int year, int month) {
        Calendar monthStartCalendar = Calendar.getInstance();
        monthStartCalendar.set(Calendar.YEAR, year);
        // month -1 的原因是, Calendar MONTH 从 0 开始计数，即 0 是一月
        monthStartCalendar.set(Calendar.MONTH, month - 1);
        monthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
        monthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        monthStartCalendar.set(Calendar.MINUTE, 0);
        monthStartCalendar.set(Calendar.SECOND, 0);
        long monthStartDate = monthStartCalendar.getTimeInMillis();

        monthStartCalendar.set(Calendar.DAY_OF_MONTH, TimeUtils.getDaysTotalOfMonth(year, month));
        monthStartCalendar.set(Calendar.HOUR_OF_DAY, 23);
        monthStartCalendar.set(Calendar.MINUTE, 59);
        monthStartCalendar.set(Calendar.SECOND, 59);
        monthStartCalendar.set(Calendar.MILLISECOND, 999);
        long monthEndDate = monthStartCalendar.getTimeInMillis();

        return new Pair<>(monthStartDate, monthEndDate);
    }

    /**
     * 返回指定年开始和结束时间 unix ms
     *
     * @param year  年份
     * @return 返回时间区间
     */
    public static Pair<Long, Long> yearDateRange(int year) {
        Calendar yearStartCalendar = Calendar.getInstance();
        yearStartCalendar.set(Calendar.YEAR, year);
        yearStartCalendar.set(Calendar.MONTH, 0);
        yearStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
        yearStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        yearStartCalendar.set(Calendar.MINUTE, 0);
        yearStartCalendar.set(Calendar.SECOND, 0);
        long yearStartDate = yearStartCalendar.getTimeInMillis();

        yearStartCalendar.set(Calendar.MONTH, 11);
        yearStartCalendar.set(Calendar.DAY_OF_MONTH, 31);
        yearStartCalendar.set(Calendar.HOUR_OF_DAY, 23);
        yearStartCalendar.set(Calendar.MINUTE, 59);
        yearStartCalendar.set(Calendar.SECOND, 59);
        yearStartCalendar.set(Calendar.MILLISECOND, 999);
        long yearEndDate = yearStartCalendar.getTimeInMillis();

        return new Pair<>(yearStartDate, yearEndDate);
    }

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

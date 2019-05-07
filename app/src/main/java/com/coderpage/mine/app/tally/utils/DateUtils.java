package com.coderpage.mine.app.tally.utils;

import android.content.Context;
import android.util.Pair;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author lc. 2018-07-08 21:27
 * @since 0.6.0
 */

public class DateUtils {

    /** 一日毫秒数 */
    public static final int DAY_MILLISECONDS = 24 * 60 * 60 * 1000;

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
     * @param year 年份
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

    /**
     * 格式化时间区间
     *
     * @param context        context
     * @param startTimeMills 开始时间
     * @param endTimeMillis  结束时间
     * @return 时间区间信息
     */
    public static String formatDisplayDateRange(Context context, long startTimeMills, long endTimeMillis) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(startTimeMills);
        endCalendar.setTimeInMillis(endTimeMillis);

        int yearCurrent = currentCalendar.get(Calendar.YEAR);
        int monthCurrent = currentCalendar.get(Calendar.MONTH);
        int dayCurrent = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int yearStart = startCalendar.get(Calendar.YEAR);
        int monthStart = startCalendar.get(Calendar.MONTH);
        int dayStart = startCalendar.get(Calendar.DAY_OF_MONTH);
        int yearEnd = endCalendar.get(Calendar.YEAR);
        int monthEnd = endCalendar.get(Calendar.MONTH);
        int dayEnd = endCalendar.get(Calendar.DAY_OF_MONTH);

        // 是否是同一天
        boolean isInSameDay = yearStart == yearEnd && monthStart == monthEnd && dayStart == dayEnd;
        // 是否是今天
        boolean isToday = isInSameDay && yearStart == yearCurrent && monthStart == monthCurrent && dayStart == dayCurrent;
        if (isToday) {
            return ResUtils.getString(context, R.string.date_format_today);
        }
        if (isInSameDay) {
            return ResUtils.getString(context, R.string.date_format_y_m_d);
        }

        // 是否是同一个月
        boolean isInSameMonth = yearStart == yearEnd && monthStart == monthEnd;
        // 是否是一整月的时间
        boolean isRangeOfFullMonth = false;
        if (isInSameMonth) {
            Pair<Long, Long> monthRange = monthDateRange(yearStart, monthStart + 1);
            isRangeOfFullMonth = ((monthRange.second - monthRange.first) - (endTimeMillis - startTimeMills)) < DAY_MILLISECONDS;
        }
        if (isRangeOfFullMonth) {
            SimpleDateFormat monthFormat = new SimpleDateFormat(
                    ResUtils.getString(context, R.string.date_format_y_m), Locale.getDefault());
            return monthFormat.format(new Date(startTimeMills));
        }
        if (isInSameMonth) {
            int dateFormatResId = (yearStart == yearCurrent) ? R.string.date_format_m_d : R.string.date_format_y_m_d;
            SimpleDateFormat dayFormat = new SimpleDateFormat(
                    ResUtils.getString(context, dateFormatResId), Locale.getDefault());
            return dayFormat.format(new Date(startTimeMills)) + "~" + dayFormat.format(new Date(endTimeMillis));
        }

        // 是否是同一年的
        boolean isInSameYear = yearStart == yearEnd;
        boolean isRangeOfFullYear = false;
        if (isInSameYear) {
            Pair<Long, Long> yearRange = yearDateRange(yearStart);
            isRangeOfFullYear = ((yearRange.second - yearRange.first) - (endTimeMillis - startTimeMills)) < DAY_MILLISECONDS;
        }
        if (isRangeOfFullYear) {
            SimpleDateFormat yearFormat = new SimpleDateFormat(
                    ResUtils.getString(context, R.string.date_format_y), Locale.getDefault());
            return yearFormat.format(new Date(startTimeMills));
        }

        int dateFormatResId = isInSameYear ? R.string.date_format_m_d : R.string.date_format_y_m_d;
        SimpleDateFormat dayFormat = new SimpleDateFormat(
                ResUtils.getString(context, dateFormatResId), Locale.getDefault());
        return dayFormat.format(new Date(startTimeMills)) + "~" + dayFormat.format(new Date(endTimeMillis));
    }
}

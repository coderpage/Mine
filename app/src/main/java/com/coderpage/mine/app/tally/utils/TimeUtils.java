package com.coderpage.mine.app.tally.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author abner-l. 2017-03-19
 */
public class TimeUtils {
    private static SimpleDateFormat mHourMinFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat mYearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static SimpleDateFormat mMonthDayFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

    public static String getRecordDisplayDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(timeMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentYear > year) {
            return mYearMonthDayFormat.format(calendar.getTime());
        }

        if (currentMonth > month || currentDay > day) {
            return mMonthDayFormat.format(calendar.getTime());
        }

        return mHourMinFormat.format(calendar.getTime());
    }

    /**
     * 计算该月份的天数
     *
     * @param year  年
     * @param month 月
     *
     * @return 返回该月的天数
     */
    public static int getDaysTotalOfMonth(int year, int month) {
        if (month == 2) {
            boolean leapyear = year % 4 == 0; // 闰年
            if (leapyear) {
                return 29;
            } else {
                return 28;
            }
        }
        if (month == 1 || month == 3 || month == 5
                || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        }
        return 30;
    }
}

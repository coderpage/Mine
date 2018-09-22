package com.coderpage.mine.app.tally.common.utils;

import com.coderpage.mine.app.tally.utils.TimeUtils;

import java.text.DecimalFormat;

/**
 * @author lc. 2018-08-05 10:58
 * @since 0.6.0
 */

public class TallyUtils {

    private static final DecimalFormat DISPLAY_MONEY_FORMAT = new DecimalFormat("0.00");

    /** 格式化显示的金额 */
    public static String formatDisplayMoney(double money) {
        return DISPLAY_MONEY_FORMAT.format(money);
    }

    /** 格式化显示的时间 */
    public static String formatDisplayTime(long timeMills) {
        return TimeUtils.getRecordDisplayDate(timeMills);
    }
}

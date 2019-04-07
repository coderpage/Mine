package com.coderpage.mine.app.tally.module.home.model;

import com.coderpage.mine.app.tally.common.utils.TallyUtils;

/**
 * @author lc. 2018-07-11 20:32
 * @since 0.6.0
 *
 * 首页今日消费总览模块数据
 */

public class HomeRecent3DayRecordsModel {

    /** 近3日新增账单数量 */
    private int recent3DayRecordsCount;
    /** 今日消费金额 */
    private double toadyExpenseAmount;

    public int getRecent3DayRecordsCount() {
        return recent3DayRecordsCount;
    }

    public void setRecent3DayRecordsCount(int recent3DayRecordsCount) {
        this.recent3DayRecordsCount = recent3DayRecordsCount;
    }

    public double getToadyExpenseAmount() {
        return toadyExpenseAmount;
    }

    public void setToadyExpenseAmount(double toadyExpenseAmount) {
        this.toadyExpenseAmount = toadyExpenseAmount;
    }

    public String getDisplayExpenseAmount() {
        return TallyUtils.formatDisplayMoney(toadyExpenseAmount);
    }
}

package com.coderpage.mine.app.tally.module.home.model;

import com.coderpage.mine.app.tally.common.utils.TallyUtils;

/**
 * @author lc. 2018-07-11 20:32
 * @since 0.6.0
 *
 * 首页今日消费总览模块数据
 */

public class HomeTodayExpenseModel {

    private double toadyExpenseAmount;

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

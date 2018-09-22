package com.coderpage.mine.app.tally.module.home.model;

import android.util.Pair;

import java.util.List;

/**
 * @author lc. 2018-07-11 20:26
 * @since 0.6.0
 *
 * 首页月消费总览模块数据
 */

public class HomeMonthModel {

    /** 月消费总额 */
    private double monthExpenseAmount;

    /** 月收入总额 */
    private double monthInComeAmount;

    /** 月分类消费数据 */
    private List<Pair<String, Double>> monthCategoryExpenseData;

    public double getMonthExpenseAmount() {
        return monthExpenseAmount;
    }

    public void setMonthExpenseAmount(double monthExpenseAmount) {
        this.monthExpenseAmount = monthExpenseAmount;
    }

    public double getMonthInComeAmount() {
        return monthInComeAmount;
    }

    public void setMonthInComeAmount(double monthInComeAmount) {
        this.monthInComeAmount = monthInComeAmount;
    }

    public List<Pair<String, Double>> getMonthCategoryExpenseData() {
        return monthCategoryExpenseData;
    }

    public void setMonthCategoryExpenseData(List<Pair<String, Double>> monthCategoryExpenseData) {
        this.monthCategoryExpenseData = monthCategoryExpenseData;
    }
}

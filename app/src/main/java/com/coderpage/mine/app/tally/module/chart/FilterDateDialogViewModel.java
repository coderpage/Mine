package com.coderpage.mine.app.tally.module.chart;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.coderpage.framework.BaseViewModel;
import com.coderpage.mine.MineApp;

/**
 * @author lc. 2019-06-15 13:40
 * @since 0.6.3
 */

public class FilterDateDialogViewModel extends BaseViewModel {

    /** 年账单-选中 */
    private ObservableBoolean mYearlySelect = new ObservableBoolean(false);
    /** 月账单-选中 */
    private ObservableBoolean mMonthlySelect = new ObservableBoolean(false);
    /** 自定义-选中 */
    private ObservableBoolean mCustomSelect = new ObservableBoolean(false);
  
    /** 选择的年 */
    private ObservableInt mSelectYear = new ObservableInt();
    /** 选择的月 */
    private ObservableInt mSelectMonth = new ObservableInt();

    /** 自定义选择的开始时间 */
    private long mCustomStartDate;
    /** 自定义选择的结束时间 */
    private long mCustomEndDate;

    public FilterDateDialogViewModel() {
        super(MineApp.getAppContext());
    }

}

package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.databinding.ObservableField;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.chart.TallyChartActivity;
import com.coderpage.mine.app.tally.module.home.model.HomeMonthModel;
import com.coderpage.mine.app.tally.persistence.preference.SettingPreference;
import com.coderpage.mine.app.tally.ui.dialog.SetBudgetMonthDialog;
import com.coderpage.mine.app.tally.utils.SecurityUtils;

import java.text.DecimalFormat;

/**
 * @author lc. 2019-04-06 23:40
 * @since 0.6.0
 */


public class HomeMonthInfoViewModel extends BaseViewModel {

    private DecimalFormat mMoneyFormat = new DecimalFormat("0.00");

    private HomeMonthModel mData;

    /** 本月支出金额 */
    private ObservableField<String> mExpenseMoney = new ObservableField<>();
    /** 本月收入金额 */
    private ObservableField<String> mIncomeMoney = new ObservableField<>();
    /** 本月预算剩余金额 */
    private ObservableField<String> mBudgetLeftMoney = new ObservableField<>();

    /** 是否隐藏金额 */
    private ObservableField<Boolean> mHideMoney = new ObservableField<>(false);

    public HomeMonthInfoViewModel(Application application) {
        super(application);
        mHideMoney.set(SettingPreference.getHideMoney(application));
    }

    public void setData(HomeMonthModel data) {
        mData = data;
        refresh(data);
    }

    public ObservableField<String> getExpenseMoney() {
        return mExpenseMoney;
    }

    public ObservableField<String> getIncomeMoney() {
        return mIncomeMoney;
    }

    public ObservableField<String> getBudgetLeftMoney() {
        return mBudgetLeftMoney;
    }

    public ObservableField<Boolean> getHideMoney() {
        return mHideMoney;
    }

    private void refresh(HomeMonthModel data) {
        mData = data;
        mExpenseMoney.set(formatExpenseMoney(mData));
        mIncomeMoney.set(formatIncomeMoney(mData));
        mBudgetLeftMoney.set(formatBudgetLeftMoney(mData));
    }

    /** 格式化本月支出总额 */
    private String formatExpenseMoney(HomeMonthModel data) {
        Boolean hideMoney = mHideMoney.get();
        if (hideMoney != null && hideMoney) {
            return "****";
        }
        if (data == null) {
            return ResUtils.getString(getApplication(), R.string.tally_module_home_non_expense_place_holder);
        }
        double monthExpenseAmount = data.getMonthExpenseAmount();
        if (monthExpenseAmount == 0) {
            return ResUtils.getString(getApplication(), R.string.tally_module_home_non_expense_place_holder);
        }
        return "¥" + mMoneyFormat.format(monthExpenseAmount);
    }

    /** 格式化本月收入总额 */
    private String formatIncomeMoney(HomeMonthModel data) {
        Boolean hideMoney = mHideMoney.get();
        if (hideMoney != null && hideMoney) {
            return "****";
        }
        if (data == null) {
            return ResUtils.getString(getApplication(), R.string.tally_module_home_non_income_place_holder);
        }
        double monthIncomeAmount = data.getMonthInComeAmount();
        if (monthIncomeAmount == 0) {
            return ResUtils.getString(getApplication(), R.string.tally_module_home_non_income_place_holder);
        }
        return "¥" + mMoneyFormat.format(monthIncomeAmount);
    }

    /** 格式化预算剩余金额 */
    private String formatBudgetLeftMoney(HomeMonthModel data) {
        Boolean hideMoney = mHideMoney.get();
        if (hideMoney != null && hideMoney) {
            return "****";
        }
        float budgetMonth = SettingPreference.getBudgetMonth(getApplication());
        if (budgetMonth <= 0) {
            return ResUtils.getString(getApplication(), R.string.tally_module_home_non_budget_place_holder);
        }
        double monthExpenseAmount = data.getMonthExpenseAmount();
        double budgetLeftMoney = budgetMonth - monthExpenseAmount;
        return "¥" + mMoneyFormat.format(budgetLeftMoney);
    }

    /** 本月消费、收入数据模块点击 */
    public void onMonthInfoClick(Activity activity) {
        SecurityUtils.executeAfterFingerprintAuth(activity, () -> {
            activity.startActivity(new Intent(activity, TallyChartActivity.class));
        });
    }

    /** 显示 or 隐藏金额点击 */
    public void onShowOrHideMoneyClick(Activity activity) {
        SecurityUtils.executeAfterFingerprintAuth(activity, () -> {
            Boolean hideMoney = mHideMoney.get();
            hideMoney = hideMoney == null ? false : hideMoney;
            mHideMoney.set(!hideMoney);
            SettingPreference.setHideMoney(getApplication(), !hideMoney);
            refresh(mData);
        });
    }

    /** 预算金额点击 */
    public void onBudgetMoneyClick(Activity activity) {
        new SetBudgetMonthDialog(activity).setListener((dialog, budget) -> {
            mBudgetLeftMoney.set(formatBudgetLeftMoney(mData));
            dialog.dismiss();
        }).show();
    }
}

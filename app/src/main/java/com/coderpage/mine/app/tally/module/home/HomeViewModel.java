package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.eventbus.EventExpenseAdd;
import com.coderpage.mine.app.tally.eventbus.EventExpenseDelete;
import com.coderpage.mine.app.tally.eventbus.EventExpenseUpdate;
import com.coderpage.mine.app.tally.eventbus.EventIncomeAdd;
import com.coderpage.mine.app.tally.eventbus.EventIncomeDelete;
import com.coderpage.mine.app.tally.eventbus.EventIncomeUpdate;
import com.coderpage.mine.app.tally.module.about.AboutActivity;
import com.coderpage.mine.app.tally.module.chart.TallyChartActivity;
import com.coderpage.mine.app.tally.module.detail.RecordDetailActivity;
import com.coderpage.mine.app.tally.module.edit.RecordEditActivity;
import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.module.home.model.HomeMonthModel;
import com.coderpage.mine.app.tally.module.home.model.HomeTodayExpenseModel;
import com.coderpage.mine.app.tally.module.records.RecordsActivity;
import com.coderpage.mine.app.tally.module.setting.SettingActivity;
import com.coderpage.mine.app.tally.persistence.model.Expense;
import com.coderpage.mine.app.tally.persistence.model.Income;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-07-08 15:24
 * @since 0.6.0
 */

public class HomeViewModel extends AndroidViewModel implements LifecycleObserver {

    private DecimalFormat mMoneyTextFormat = new DecimalFormat("0.00");

    /** 刷新状态 */
    private MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>();

    /** 首页数据列表 */
    private MutableLiveData<List<HomeDisplayData>> mDataList = new MutableLiveData<>();

    private HomRepository mRepository;

    public HomeViewModel(Application application) {
        super(application);
        mRepository = new HomRepository();
        refresh();
    }

    LiveData<Boolean> observableRefreshing() {
        return mRefreshing;
    }

    LiveData<List<HomeDisplayData>> observableDataList() {
        return mDataList;
    }

    /** 添加新纪录点击 */
    public void onAddNewRecordClick(Activity activity) {
        RecordEditActivity.openAsAddNewExpense(activity);
    }

    /** 底部菜单按钮点击 */
    public void onBottomMenuClick(Activity activity) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.Widget_Dialog_BottomSheet);
        View.OnClickListener menuClickListener = (v) -> {
            int id = v.getId();
            switch (id) {
                case R.id.lyBtnAbout:
                    activity.startActivity(new Intent(activity, AboutActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.lyBtnSetting:
                    activity.startActivity(new Intent(activity, SettingActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.lyBtnExpenseRecords:
                    activity.startActivity(new Intent(activity, RecordsActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.lyBtnChart:
                    activity.startActivity(new Intent(activity, TallyChartActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                default:
                    break;
            }
        };

        View view = activity.getLayoutInflater().inflate(R.layout.widget_tally_bottom_menu_sheet, null);
        view.findViewById(R.id.lyBtnAbout).setOnClickListener(menuClickListener);
        view.findViewById(R.id.lyBtnSetting).setOnClickListener(menuClickListener);
        view.findViewById(R.id.lyBtnExpenseRecords).setOnClickListener(menuClickListener);
        view.findViewById(R.id.lyBtnChart).setOnClickListener(menuClickListener);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        Window window = bottomSheetDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setWindowAnimations(R.style.BottomSheetAnimation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /** 本月消费、收入数据模块点击 */
    public void onMonthInfoClick(Activity activity) {
        activity.startActivity(new Intent(activity, TallyChartActivity.class));
    }

    /** 消费记录 ITEM 点击 */
    public void onExpenseItemClick(Activity activity, Expense expense) {
        RecordDetailActivity.openExpenseDetail(activity, expense.getId());
    }

    /** 收入记录 ITEM 点击 */
    public void onIncomeItemClick(Activity activity, Income income, View view) {
        RecordDetailActivity.openIncomeDetail(activity, income.getId());
    }

    /** 消费记录 ITEM 长按 */
    public boolean onExpenseItemLongClick(Activity activity, Expense expense) {
        new AlertDialog.Builder(activity).setItems(R.array.expenseItemLongClickOption, (dialog, which) -> {
            switch (which) {
                case 0:
                    deleteExpense(expense);
                    break;
                case 1:
                    RecordEditActivity.openAsUpdateExpense(activity, expense.getId());
                    break;
                default:
                    break;
            }
        }).show();
        return true;
    }

    /** 消费记录 ITEM 长按 */
    public boolean onIncomeItemLongClick(Activity activity, Income income) {
        new AlertDialog.Builder(activity).setItems(R.array.expenseItemLongClickOption, (dialog, which) -> {
            switch (which) {
                case 0:
                    deleteIncome(income);
                    break;
                case 1:
                    RecordEditActivity.openAsUpdateIncome(activity, income.getId());
                    break;
                default:
                    break;
            }
        }).show();
        return true;
    }

    /** 刷新首页数据 */
    void refresh() {
        if (mRefreshing.getValue() != null && mRefreshing.getValue()) {
            return;
        }
        mRefreshing.setValue(true);
        mRepository.loadCurrentMonthExpenseData(result -> {
            mRefreshing.setValue(false);
            if (result.isOk()) {
                double monthExpenseTotalAmount = mRepository.getCurrentMonthExpenseTotalAmount();
                double monthInComeTotalAmount = mRepository.getCurrentMonthInComeTotalAmount();
                double todayExpenseTotalAmount = mRepository.getTodayExpenseTotalAmount();

                List<Pair<String, Double>> categoryExpenseTotal = mRepository.getCategoryExpenseTotal();
                List<Expense> todayExpenseList = mRepository.getTodayExpenseList();
                List<Income> todayInComeList = mRepository.getTodayInComeList();

                HomeMonthModel monthModel = new HomeMonthModel();
                monthModel.setMonthExpenseAmount(monthExpenseTotalAmount);
                monthModel.setMonthInComeAmount(monthInComeTotalAmount);
                monthModel.setMonthCategoryExpenseData(categoryExpenseTotal);

                HomeTodayExpenseModel todayExpenseModel = new HomeTodayExpenseModel();
                todayExpenseModel.setToadyExpenseAmount(todayExpenseTotalAmount);

                List<HomeDisplayData> dataList = new ArrayList<>();
                dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_MONTH_INFO, monthModel));
                dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_TODAY_EXPENSE, todayExpenseModel));

                if (todayInComeList != null) {
                    for (Income income : todayInComeList) {
                        dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_IN_COME_ITEM, income));
                    }
                }

                if (todayExpenseList != null) {
                    for (Expense expense : todayExpenseList) {
                        dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_EXPENSE_ITEM, expense));
                    }
                }

                mDataList.setValue(dataList);
            }
        });
    }

    /** 删除消费记录 */
    private void deleteExpense(Expense expense) {
        if (expense == null) {
            return;
        }
        mRepository.deleteExpense(expense.getId(), new Callback<Void, IError>() {
            @Override
            public void success(Void aVoid) {
                refresh();
            }

            @Override
            public void failure(IError iError) {
                UIUtils.showToastShort(getApplication(), iError.msg());
            }
        });
    }

    /** 删除消费记录 */
    private void deleteIncome(Income income) {
        if (income == null) {
            return;
        }
        mRepository.deleteInCome(income.getId(), new Callback<Void, IError>() {
            @Override
            public void success(Void aVoid) {
                refresh();
            }

            @Override
            public void failure(IError iError) {
                UIUtils.showToastShort(getApplication(), iError.msg());
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        EventBus.getDefault().register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventExpenseAdd(EventExpenseAdd event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventExpenseUpdate(EventExpenseUpdate event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventExpenseDelete(EventExpenseDelete event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventIncomeAdd(EventIncomeAdd event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventIncomeUpdate(EventIncomeUpdate event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventIncomeDelete(EventIncomeDelete event) {
        refresh();
    }
}

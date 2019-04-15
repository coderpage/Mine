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
import android.databinding.ObservableField;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;

import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordDelete;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.module.edit.RecordEditActivity;
import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.module.home.model.HomeMonthModel;
import com.coderpage.mine.app.tally.module.home.model.HomeRecent3DayRecordsModel;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.preference.SettingPreference;
import com.coderpage.mine.app.tally.ui.dialog.MenuDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-07-08 15:24
 * @since 0.6.0
 */

public class HomeViewModel extends AndroidViewModel implements LifecycleObserver {

    /** 是否隐藏金额 */
    private ObservableField<Boolean> mHideMoney = new ObservableField<>(false);
    /** 刷新状态 */
    private MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>();

    /** 首页数据列表 */
    private MutableLiveData<List<HomeDisplayData>> mDataList = new MutableLiveData<>();

    private HomRepository mRepository;

    public HomeViewModel(Application application) {
        super(application);
        mRepository = new HomRepository();
        mHideMoney.set(SettingPreference.getHideMoney(application));
        refresh();
    }

    public ObservableField<Boolean> getHideMoney() {
        return mHideMoney;
    }

    LiveData<Boolean> observableRefreshing() {
        return mRefreshing;
    }

    LiveData<List<HomeDisplayData>> observableDataList() {
        return mDataList;
    }

    /** 显示 or 隐藏金额点击 */
    public void onShowOrHideMoneyClick() {
        Boolean hideMoney = mHideMoney.get();
        hideMoney = hideMoney == null ? false : hideMoney;
        mHideMoney.set(!hideMoney);
        SettingPreference.setHideMoney(getApplication(), !hideMoney);
    }

    /** 添加新纪录点击 */
    public void onAddNewRecordClick(Activity activity) {
        RecordEditActivity.openAsAddNewExpense(activity);
    }

    /** 底部菜单按钮点击 */
    public void onBottomMenuClick(FragmentActivity activity) {
        MenuDialog.show(activity);
    }

    /** 刷新首页数据 */
    void refresh() {
        if (mRefreshing.getValue() != null && mRefreshing.getValue()) {
            return;
        }
        mRepository.loadCurrentMonthExpenseData(result -> {
            mRefreshing.setValue(false);
            if (result.isOk()) {
                int recent3DayRecordCount = mRepository.getRecent3DayRecordCount();
                double monthExpenseTotalAmount = mRepository.getCurrentMonthExpenseTotalAmount();
                double monthInComeTotalAmount = mRepository.getCurrentMonthInComeTotalAmount();
                double todayExpenseTotalAmount = mRepository.getTodayExpenseTotalAmount();

                List<Pair<String, Double>> categoryExpenseTotal = mRepository.getCategoryExpenseTotal();
                List<Record> recent3DayRecordList = mRepository.getRecent3DayRecordList();

                HomeMonthModel monthModel = new HomeMonthModel();
                monthModel.setMonthExpenseAmount(monthExpenseTotalAmount);
                monthModel.setMonthInComeAmount(monthInComeTotalAmount);
                monthModel.setMonthCategoryExpenseData(categoryExpenseTotal);

                HomeRecent3DayRecordsModel recent3DayRecordsModel = new HomeRecent3DayRecordsModel();
                recent3DayRecordsModel.setToadyExpenseAmount(todayExpenseTotalAmount);
                recent3DayRecordsModel.setRecent3DayRecordsCount(recent3DayRecordCount);

                List<HomeDisplayData> dataList = new ArrayList<>();
                dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_MONTH_INFO, monthModel));
                dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_RECENT_DAY_INFO, recent3DayRecordsModel));

                for (Record record : recent3DayRecordList) {
                    dataList.add(new HomeDisplayData(HomeDisplayData.TYPE_RECORD_ITEM, record));
                }
                mDataList.setValue(dataList);
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
    public void onEventRecordAdd(EventRecordAdd event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordDelete(EventRecordDelete event) {
        refresh();
    }
}

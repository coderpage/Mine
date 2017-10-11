package com.coderpage.mine.app.tally.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.coderpage.base.common.IError;
import com.coderpage.base.utils.LogUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.about.AboutActivity;
import com.coderpage.mine.app.tally.chart.ChartActivity;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.edit.ExpenseEditActivity;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordDelete;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.records.RecordsActivity;
import com.coderpage.mine.app.tally.search.SearchActivity;
import com.coderpage.mine.app.tally.setting.SettingActivity;
import com.coderpage.mine.app.tally.ui.widget.LoadMoreRecyclerView;
import com.coderpage.mine.app.tally.update.UpdateUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coderpage.base.utils.LogUtils.LOGI;


/**
 * @author abner-l. 2017-01-23
 * @since 0.1.0
 */

public class MainActivity extends BaseActivity
        implements UpdatableView<MainModel, MainModel.MainQueryEnum,
        MainModel.MainUserActionEnum, IError> {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    DecimalFormat mAmountDecimalFormat = new DecimalFormat("0.00");
    MainPresenter mPresenter;
    UserActionListener mUserActionListener;

    LoadMoreRecyclerView mHistoryRecordsRecycler;
    TextView mSumOfMonthAmountTv;
    TextView mTodayExpenseTipTv;
    MainHistoryExpenseAdapter mAllExpenseAdapter;

    PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);
        initView();
        initPresenter();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        UpdateUtils.checkPersistedNewVersionAndShowUpdateConfirmDialog(this);
    }

    private void initView() {
        mHistoryRecordsRecycler = ((LoadMoreRecyclerView) findViewById(R.id.recyclerTodayRecord));
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mHistoryRecordsRecycler.setLayoutManager(linearLayoutManager);
        mHistoryRecordsRecycler.setHasFixedSize(true);
        mHistoryRecordsRecycler.setNestedScrollingEnabled(false);
        mAllExpenseAdapter = new MainHistoryExpenseAdapter(this);
        mHistoryRecordsRecycler.setAdapter(mAllExpenseAdapter);

        mSumOfMonthAmountTv = ((TextView) findViewById(R.id.tvMonthAmount));
        mTodayExpenseTipTv = ((TextView) findViewById(R.id.tvTodayExpenseRecordTip));

        mPieChart = (PieChart) findViewById(R.id.chartCurrentMonth);
        mPieChart.setTouchEnabled(false);

        findViewById(R.id.btnAddRecord).setOnClickListener(mOnClickListener);
        findViewById(R.id.lyMonthInfo).setOnClickListener(mOnClickListener);
        findViewById(R.id.ivBottomMenu).setOnClickListener(mOnClickListener);
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(
                new MainModel(this),
                this,
                MainModel.MainUserActionEnum.values(),
                MainModel.MainQueryEnum.values());
        mPresenter.loadInitialQueries();
        mAllExpenseAdapter.setUserActionListener(mUserActionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tally_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private View.OnClickListener mOnClickListener = (View v) -> {
        int id = v.getId();
        switch (id) {
            case R.id.btnAddRecord:
                Intent intent = new Intent(MainActivity.this, ExpenseEditActivity.class);
                startActivity(intent);
                break;
            case R.id.lyMonthInfo:
                Intent chartIntent = new Intent(MainActivity.this, ChartActivity.class);
                startActivity(chartIntent);
                break;
            case R.id.ivBottomMenu:
                showBottomSheet();
                break;
        }
    };

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Widget_Dialog_BottomSheet);
        View.OnClickListener menuClickListener = (v) -> {
            int id = v.getId();
            switch (id) {
                case R.id.lyBtnAbout:
                    startActivity(new Intent(this, AboutActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.lyBtnSetting:
                    startActivity(new Intent(this, SettingActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.lyBtnExpenseRecords:
                    startActivity(new Intent(this, RecordsActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.lyBtnChart:
                    startActivity(new Intent(this, ChartActivity.class));
                    bottomSheetDialog.dismiss();
                    break;
            }
        };

        View view = getLayoutInflater().inflate(R.layout.widget_tally_bottom_menu_sheet, null);
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
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordAdd(EventRecordAdd event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.REFRESH_TODAY_EXPENSE, null);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.REFRESH_TODAY_EXPENSE, null);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordDelete(EventRecordDelete event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.REFRESH_TODAY_EXPENSE, null);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Override
    public void displayData(MainModel model, MainModel.MainQueryEnum query) {
        switch (query) {
            case MONTH_TOTAL:
                String format = mAmountDecimalFormat.format(model.getMonthTotal());
                mSumOfMonthAmountTv.setText(format);
                reDrawPieChart(model.getCurrentMonthExpenseItemList());
                break;
            case EXPENSE_INIT:
                mAllExpenseAdapter.refreshData(model.getTodayExpenseList());
                refreshToadyExpenseTip(model);
                break;
        }
    }

    @Override
    public void displayErrorMessage(MainModel.MainQueryEnum query, IError error) {

    }

    @Override
    public void displayUserActionResult(MainModel model,
                                        Bundle args, MainModel.MainUserActionEnum userAction,
                                        boolean success,
                                        IError error) {
        LOGI(TAG, "displayUserActionResult-> action=" + userAction.getId());
        switch (userAction) {
            case RELOAD_MONTH_TOTAL:
                if (success) {
                    String format = mAmountDecimalFormat.format(model.getMonthTotal());
                    mSumOfMonthAmountTv.setText(format);
                    reDrawPieChart(model.getCurrentMonthExpenseItemList());
                }
                break;
            case EXPENSE_DELETE:
                if (success) {
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.REFRESH_TODAY_EXPENSE, null);
                }
                break;
            case REFRESH_TODAY_EXPENSE:
                if (success) {
                    mAllExpenseAdapter.refreshData(model.getTodayExpenseList());
                    refreshToadyExpenseTip(model);
                }
                break;
        }
    }

    private void refreshToadyExpenseTip(MainModel model) {
        List<Expense> todayExpenseList = model.getTodayExpenseList();
        if (todayExpenseList.isEmpty()) {
            mTodayExpenseTipTv.setText(R.string.tally_today_no_expense_record_tip);
        } else {
            float todayTotal = 0f;
            for (Expense expense : todayExpenseList) {
                todayTotal += expense.getAmount();
            }
            String totalFormat = mAmountDecimalFormat.format(todayTotal);
            mTodayExpenseTipTv.setText(
                    ResUtils.getString(this, R.string.tally_toady_expense_total, totalFormat));
        }
    }

    private void reDrawPieChart(List<Expense> items) {
        Map<String, Float> getMountByCategoryName = new HashMap<>();
        for (Expense item : items) {
            Float amount = getMountByCategoryName.get(item.getCategoryName());
            if (amount == null) {
                amount = item.getAmount();
            } else {
                amount += item.getAmount();
            }
            getMountByCategoryName.put(item.getCategoryName(), amount);
        }
        List<PieEntry> pieEntryList = new ArrayList<>();
        for (Map.Entry<String, Float> entry : getMountByCategoryName.entrySet()) {
            pieEntryList.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        Resources resources = getResources();
        pieDataSet.setColors(
                resources.getColor(R.color.categoryColor1),
                resources.getColor(R.color.categoryColor2),
                resources.getColor(R.color.categoryColor3),
                resources.getColor(R.color.categoryColor4),
                resources.getColor(R.color.categoryColor5),
                resources.getColor(R.color.categoryColor6),
                resources.getColor(R.color.categoryColor7),
                resources.getColor(R.color.categoryColor8),
                resources.getColor(R.color.categoryColor9),
                resources.getColor(R.color.categoryColor10),
                resources.getColor(R.color.categoryColor11),
                resources.getColor(R.color.categoryColor12),
                resources.getColor(R.color.categoryColor13),
                resources.getColor(R.color.categoryColor14),
                resources.getColor(R.color.categoryColor15),
                resources.getColor(R.color.categoryColor16),
                resources.getColor(R.color.categoryColor17),
                resources.getColor(R.color.categoryColor18));
        PieData pieData = new PieData(pieDataSet);
        mPieChart.setData(pieData);
        mPieChart.setDescription(null);
        mPieChart.setDrawEntryLabels(false);
        Legend legend = mPieChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(ResUtils.getColor(MainActivity.this, R.color.appTextColorSecondary));

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    @Override
    public Uri getDataUri(MainModel.MainQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }
}
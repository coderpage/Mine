package com.coderpage.mine.app.tally.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.app.tally.about.AboutActivity;
import com.coderpage.mine.app.tally.chart.ChartActivity;
import com.coderpage.mine.app.tally.edit.ExpenseEditActivity;
import com.coderpage.mine.app.tally.records.RecordsActivity;
import com.coderpage.utils.LogUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.ui.widget.LoadMoreRecyclerView;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;
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

import static com.coderpage.utils.LogUtils.LOGI;

/**
 * @author abner-l. 2017-01-23
 * @since 0.1.0
 */

public class MainActivity extends BaseActivity
        implements UpdatableView<MainModel, MainModel.MainQueryEnum,
        MainModel.MainUserActionEnum> {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    DecimalFormat mAmountDecimalFormat = new DecimalFormat(".00");
    String mAmountFormat;
    MainPresenter mPresenter;
    UserActionListener mUserActionListener;

    LoadMoreRecyclerView mHistoryRecordsRecycler;
    TextView mSumOfMonthAmountTv;
    MainHistoryExpenseAdapter mAllExpenseAdapter;

    PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);
        mAmountFormat = getString(R.string.tally_amount_cny);
        initView();
        initPresenter();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mHistoryRecordsRecycler = ((LoadMoreRecyclerView) findViewById(R.id.recyclerHistoryRecord));
        mHistoryRecordsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAllExpenseAdapter = new MainHistoryExpenseAdapter(this);
        mHistoryRecordsRecycler.setAdapter(mAllExpenseAdapter);
        mHistoryRecordsRecycler.setPullActionListener(new LoadMoreRecyclerView.PullActionListener() {

            @Override
            public void onPullUpLoadMore() {
                ArrayList<ExpenseItem> dataList = mAllExpenseAdapter.getDataList();
                long loadMoreStartDate = System.currentTimeMillis();
                if (!dataList.isEmpty()) {
                    loadMoreStartDate = dataList.get(dataList.size() - 1).getTime();
                }
                Bundle args = new Bundle(1);
                args.putLong(MainModel.EXTRA_LOAD_MORE_START_DATE, loadMoreStartDate);
                mUserActionListener.onUserAction(MainModel.MainUserActionEnum.LOAD_MORE, args);
            }
        });
        mSumOfMonthAmountTv = ((TextView) findViewById(R.id.tvMonthAmount));

        findViewById(R.id.btnAddRecord).setOnClickListener(mOnClickListener);
        findViewById(R.id.lyMonthInfo).setOnClickListener(mOnClickListener);

        mPieChart = (PieChart) findViewById(R.id.chartCurrentMonth);
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
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        DrawShadowFrameLayout drawShadowFrameLayout =
                (DrawShadowFrameLayout) findViewById(R.id.main_content);
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);
    }

    private void setContentTopClearance(int clearance) {
        View view = findViewById(R.id.lyContainer);
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), clearance,
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mine_tally, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.menu_expense_records:
                startActivity(new Intent(this, RecordsActivity.class));
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
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordAdd(EventRecordAdd event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.NEW_EXPENSE_CREATED, args);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.EXPENSE_EDITED, args);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Override
    public void displayData(MainModel model, MainModel.MainQueryEnum query) {
        switch (query) {
            case MONTH_TOTAL:
                String format = String.format(
                        mAmountFormat, mAmountDecimalFormat.format(model.getMonthTotal()));
                mSumOfMonthAmountTv.setText(format);
                reDrawPieChart(model.getCurrentMonthExpenseItemList());
                break;
            case EXPENSE_INIT:
                mAllExpenseAdapter.refreshData(model.getInitExpenseItemList());
                break;
        }
    }

    @Override
    public void displayErrorMessage(MainModel.MainQueryEnum query) {

    }

    @Override
    public void displayUserActionResult(MainModel model,
                                        Bundle args, MainModel.MainUserActionEnum userAction,
                                        boolean success) {
        LOGI(TAG, "displayUserActionResult-> action=" + userAction.getId());
        switch (userAction) {
            case NEW_EXPENSE_CREATED:
                if (success) {
                    ExpenseItem newAddExpenseItem = model.getNewAddExpenseItem();
                    mAllExpenseAdapter.addNewItem(newAddExpenseItem);
                }
                break;
            case RELOAD_MONTH_TOTAL:
                if (success) {
                    String format = String.format(
                            mAmountFormat, mAmountDecimalFormat.format(model.getMonthTotal()));
                    mSumOfMonthAmountTv.setText(format);
                    reDrawPieChart(model.getCurrentMonthExpenseItemList());
                }
                break;
            case EXPENSE_EDITED:
                if (success) {
                    ExpenseItem editedExpenseItem = model.getEditedExpenseItem();
                    mAllExpenseAdapter.refreshItem(editedExpenseItem);
                }
                break;
            case EXPENSE_DELETE:
                if (success) {
                    long deletedId = args.getLong(MainModel.EXTRA_EXPENSE_ID);
                    mAllExpenseAdapter.removeItem(deletedId);
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
                }
                break;
            case LOAD_MORE:
                if (success) {
                    mAllExpenseAdapter.addHistoryItems(model.getLoadMoreExpenseItemList());
                }
                mPresenter.setOnLoadMore(false);
                break;
        }
    }

    private void reDrawPieChart(List<ExpenseItem> items) {
        Map<String, Float> getMountByCategoryName = new HashMap<>();
        for (ExpenseItem item : items) {
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
//        mPieChart.setEntryLabelTextSize(9f);
        mPieChart.setCenterTextSize(20f);
        mPieChart.setDrawEntryLabels(false);
        mPieChart.getLegend().setEnabled(true);
        mPieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        mPieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        mPieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);

        mPieChart.invalidate();
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
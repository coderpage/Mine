package com.coderpage.mine.app.tally.module.chart;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.router.TallyRouter;
import com.coderpage.mine.app.tally.module.chart.data.DailyData;
import com.coderpage.mine.app.tally.module.chart.data.Month;
import com.coderpage.mine.app.tally.module.chart.data.MonthlyData;
import com.coderpage.mine.app.tally.module.chart.data.MonthlyDataList;
import com.coderpage.mine.app.tally.module.chart.widget.MineBarChart;
import com.coderpage.mine.app.tally.module.chart.widget.MineLineChart;
import com.coderpage.mine.tally.module.chart.TallyChartActivityBinding;
import com.coderpage.mine.ui.BaseActivity;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-09-24 14:37
 * @since 0.6.0
 */
@Route(path = TallyRouter.CHART)
public class TallyChartActivity extends BaseActivity {

    static final String EXTRA_YEAR = "extra_year";
    static final String EXTRA_MONTH = "extra_month";

    private MineBarChart mBarChart;
    private MineLineChart mLineChart;
    private RecyclerView mCategoryDataRecycler;
    private TallyChartCategoryDataAdapter mCategoryDataAdapter;

    private TallyChartActivityBinding mBinding;
    private TallyChartViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.tally_module_chart_tally_chart_activity);
        mViewModel = ViewModelProviders.of(this).get(TallyChartViewModel.class);
        getLifecycle().addObserver(mViewModel);

        initView();
        subScribeUi();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsClose(v -> finish());
    }

    /**
     * 打开图片页
     *
     * @param activity activity
     * @param year     年
     * @param month    月
     */
    public static void open(Activity activity, int year, int month) {
        Intent intent = new Intent(activity, TallyChartActivity.class);
        intent.putExtra(EXTRA_YEAR, year);
        intent.putExtra(EXTRA_MONTH, month);
        activity.startActivity(intent);
    }

    private void initView() {
        mBarChart = mBinding.barChart;
        mLineChart = mBinding.lineChart;
        mCategoryDataRecycler = mBinding.recyclerCategory;
        mCategoryDataRecycler.setLayoutManager(new LinearLayoutManager(self(), LinearLayoutManager.VERTICAL, false));
        mCategoryDataAdapter = new TallyChartCategoryDataAdapter(mViewModel);
        mCategoryDataRecycler.setAdapter(mCategoryDataAdapter);
    }

    private void subScribeUi() {
        mBinding.setActivity(this);
        mBinding.setVm(mViewModel);

        // 日消费柱状图
        mViewModel.getDailyExpenseList().observe(this, dailyDataList -> {
            if (dailyDataList != null) {
                showDailyBarChart(true, dailyDataList);
            }
        });
        // 日收入柱状图
        mViewModel.getDailyIncomeList().observe(this, dailyDataList -> {
            if (dailyDataList != null) {
                showDailyBarChart(false, dailyDataList);
            }
        });
        // 月支出、月收入折线图
        mViewModel.getMonthlyDataList().observe(this, monthlyDataList -> {
            if (monthlyDataList != null) {
                showMonthlyLineChart(monthlyDataList);
            }
        });
        // 支出分类饼图
        mViewModel.getCategoryExpenseDataList().observe(this, categoryDataList -> {
            if (categoryDataList != null) {
                mCategoryDataAdapter.setDataList(categoryDataList);
            }
        });
        // 收入分类饼图
        mViewModel.getCategoryIncomeDataList().observe(this, categoryDataList -> {
            if (categoryDataList != null) {
                mCategoryDataAdapter.setDataList(categoryDataList);
            }
        });
        mViewModel.getViewReliedTask().observe(this, task -> {
            if (task != null) {
                task.execute(this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tally_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_date_select:
                mViewModel.onSelectDateClick(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示柱状图
     *
     * @param isShowExpense 是否显示为支出图
     * @param dailyDataList 每日支出或每日收入数据
     */
    private void showDailyBarChart(boolean isShowExpense, List<DailyData> dailyDataList) {
        int barColor = isShowExpense ? UIUtils.getColor(this, R.color.expenseColor)
                : UIUtils.getColor(this, R.color.incomeColor);

        List<BarEntry> yValues = new ArrayList<>();
        if (dailyDataList != null) {
            for (int i = 0; i < dailyDataList.size(); i++) {
                DailyData dailyData = dailyDataList.get(i);
                BarEntry barEntry = new BarEntry(i, dailyData.getAmount());
                barEntry.setData(dailyData);
                yValues.add(barEntry);
            }
        }

        BarDataSet barDataSet = new BarDataSet(yValues, "");
        barDataSet.setColor(Color.GRAY);
        barDataSet.setDrawValues(false);
        barDataSet.setFormLineWidth(0);
        barDataSet.setBarShadowColor(ResUtils.getColor(this, R.color.chartGridLine));
        barDataSet.setColor(barColor);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(ResUtils.getColor(this, R.color.chartGridLine));
        xAxis.setDrawGridLines(false);
        xAxis.setGridColor(ResUtils.getColor(this, R.color.chartGridLine));
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(yValues.size());
        xAxis.setValueFormatter((value, axis) -> {
            String format = "";
            if (value != 0
                    && value != axis.mEntryCount - 1
                    && value != axis.mEntryCount / 2) {
                return format;
            }
            DailyData dailyData = dailyDataList != null && dailyDataList.size() > value ?
                    dailyDataList.get((int) value) : null;
            if (dailyData != null) {
                return dailyData.getMonth() + "-" + dailyData.getDayOfMonth();
            }
            return format;
        });

        YAxis axisLeft = mBarChart.getAxisLeft();
        axisLeft.setAxisMinimum(0);
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);

        YAxis axisRight = mBarChart.getAxisRight();
        axisRight.setAxisMinimum(0);
        axisRight.setDrawLabels(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        mBarChart.setNoDataText(ResUtils.getString(self(), R.string.tally_chart_empty_tip));
        mBarChart.setNoDataTextColor(ResUtils.getColor(self(), R.color.appTextColorPrimary));
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawBorders(false);
        mBarChart.setDrawBarShadow(true);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setData(barData);
        mBarChart.setDrawValueAboveBar(false);
        mBarChart.setDescription(null);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.animateY(500);
    }

    /**
     * 显示月支出、月收入折线图
     *
     * @param monthlyDataList 月支出、月收入数据
     */
    private void showMonthlyLineChart(MonthlyDataList monthlyDataList) {

        List<Entry> yValuesExpense = new ArrayList<>();
        if (monthlyDataList != null && monthlyDataList.getExpenseList() != null) {
            List<MonthlyData> expenseList = monthlyDataList.getExpenseList();
            for (int i = 0; i < expenseList.size(); i++) {
                MonthlyData monthlyData = expenseList.get(i);
                Entry entry = new Entry(i, monthlyData.getAmount());
                entry.setData(monthlyData);
                yValuesExpense.add(entry);
            }
        }
        LineDataSet expenseDataSet = new LineDataSet(yValuesExpense, "");
        expenseDataSet.setColor(ResUtils.getColor(this, R.color.expenseColor));
        expenseDataSet.setDrawValues(false);
        expenseDataSet.setFormLineWidth(0);
        expenseDataSet.setCircleColor(ResUtils.getColor(this, R.color.expenseColor));
        expenseDataSet.setCircleRadius(Color.WHITE);

        List<Entry> yValuesIncome = new ArrayList<>();
        if (monthlyDataList != null && monthlyDataList.getIncomeList() != null) {
            List<MonthlyData> incomeList = monthlyDataList.getIncomeList();
            for (int i = 0; i < incomeList.size(); i++) {
                MonthlyData monthlyData = incomeList.get(i);
                Entry entry = new Entry(i, monthlyData.getAmount());
                entry.setData(monthlyData);
                yValuesIncome.add(entry);
            }
        }
        LineDataSet incomeDataSet = new LineDataSet(yValuesIncome, "");
        incomeDataSet.setColor(ResUtils.getColor(this, R.color.incomeColor));
        incomeDataSet.setDrawValues(false);
        incomeDataSet.setFormLineWidth(0);
        incomeDataSet.setCircleColor(ResUtils.getColor(this, R.color.incomeColor));


        List<String> xAxisLabels = new ArrayList<>();
        List<Entry> entryList = yValuesExpense.size() >= yValuesIncome.size() ? yValuesExpense : yValuesIncome;
        for (Entry entry : entryList) {
            MonthlyData expense = (MonthlyData) entry.getData();
            Month month = expense.getMonth();
            String label = UIUtils.getString(this, R.string.tally_month_format, month.getMonth());
            xAxisLabels.add(label);
        }
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(ResUtils.getColor(getApplicationContext(), R.color.appTextColorLabel));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(ResUtils.getColor(this, R.color.chartGridLine));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter((value, axis) -> {
            if (xAxisLabels.size() > value) {
                return xAxisLabels.get((int) value);
            }
            return "";
        });

        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setAxisMinimum(0);
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);

        YAxis axisRight = mLineChart.getAxisRight();
        axisRight.setAxisMinimum(0);
        axisRight.setDrawLabels(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        if (yValuesIncome.isEmpty() && yValuesExpense.isEmpty()) {
            return;
        }

        LineData lineData = new LineData();
        if (!yValuesIncome.isEmpty()) {
            lineData.addDataSet(incomeDataSet);
        }
        if (!yValuesExpense.isEmpty()) {
            lineData.addDataSet(expenseDataSet);
        }

        mBarChart.setNoDataText(ResUtils.getString(self(), R.string.tally_chart_empty_tip));
        mBarChart.setNoDataTextColor(ResUtils.getColor(self(), R.color.appTextColorPrimary));
        mLineChart.setScaleEnabled(false);
        mLineChart.getXAxis().setAxisMaximum(lineData.getXMax());
        mLineChart.getXAxis().setAxisMinimum(lineData.getXMin());
        mLineChart.setVisibleXRange(0, 11);
        mLineChart.setDrawBorders(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDescription(null);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setData(lineData);
        mLineChart.animateY(500);
    }
}

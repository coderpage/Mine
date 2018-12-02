package com.coderpage.mine.app.tally.module.chart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coderpage.base.common.IError;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.chart.data.DailyData;
import com.coderpage.mine.app.tally.module.chart.data.Month;
import com.coderpage.mine.app.tally.module.chart.data.MonthlyData;
import com.coderpage.mine.app.tally.module.chart.widget.ExpenseLineChart;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.ui.widget.MonthSelectDialog;
import com.coderpage.mine.app.tally.utils.PopupUtils;
import com.coderpage.mine.app.tally.utils.TimeUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coderpage.base.utils.LogUtils.makeLogTag;
import static com.coderpage.base.utils.UIUtils.dp2px;
import static com.coderpage.mine.app.tally.module.chart.ChartModel.ChartQueryEnum;
import static com.coderpage.mine.app.tally.module.chart.ChartModel.ChartUserActionEnum;

/**
 * @author abner-l. 2017-04-23
 */

public class ChartActivity extends BaseActivity implements
        UpdatableView<ChartModel, ChartModel.ChartQueryEnum,
                ChartModel.ChartUserActionEnum, IError> {

    private static final String TAG = makeLogTag(ChartActivity.class);

    private static final String EXTRA_YEAR = "extra_year";
    private static final String EXTRA_MONTH = "extra_month";

    private ExpenseLineChart mLineChart;
    private TextView mMonthTv;
    private TextView mLineChartExpenseTipTv;
    private TextView mMonthTotalTv;
    private PieChart mPieChart;
    private RecyclerView mCategoryMonthRecycler;
    private MonthCategoryExpenseAdapter mMonthCategoryAdapter;

    private int mInitYear;
    private int mInitMonth;
    private Presenter mPresenter;
    private ChartModel mModel;
    private UserActionListener<ChartModel.ChartUserActionEnum> mUserActionListener;
    private DecimalFormat mDecimalFormat = new DecimalFormat(".00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_chart);
        initData();
        initView();
        initPresenter();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mInitYear = intent.getIntExtra(EXTRA_YEAR, -1);
        mInitMonth = intent.getIntExtra(EXTRA_MONTH, -1);
    }

    private void initView() {
        mLineChart = (ExpenseLineChart) findViewById(R.id.lineChart);
        setupBaseLineChart();
        mMonthTv = (TextView) findViewById(R.id.tvMonth);
        mLineChartExpenseTipTv = (TextView) findViewById(R.id.tvMonthExpenseTip);
        mMonthTotalTv = (TextView) findViewById(R.id.tvMonthExpenseTotal);
        mPieChart = (PieChart) findViewById(R.id.pieChart);
        mCategoryMonthRecycler = (RecyclerView) findViewById(R.id.recyclerCategoryExpense);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(ChartActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mCategoryMonthRecycler.setLayoutManager(linearLayoutManager);
        mCategoryMonthRecycler.setHasFixedSize(true);
        mCategoryMonthRecycler.setNestedScrollingEnabled(false);
        mMonthCategoryAdapter = new MonthCategoryExpenseAdapter(ChartActivity.this);
        mCategoryMonthRecycler.setAdapter(mMonthCategoryAdapter);
        setupPieChart();

        findViewById(R.id.ivSwitchChartMode).setOnClickListener(mOnclickListener);
    }

    private void initPresenter() {
        mModel = new ChartModel(this);
        if (mInitYear != -1 && mInitMonth != -1) {
            mPresenter = new PresenterImpl<>(mModel, this, ChartUserActionEnum.values(), null);
            mPresenter.loadInitialQueries();
            Bundle args = new Bundle(2);
            args.putInt(EXTRA_YEAR, mInitYear);
            args.putInt(EXTRA_MONTH, mInitMonth);
            mUserActionListener.onUserAction(ChartUserActionEnum.SWITCH_MONTH, args);
        } else {
            mPresenter = new PresenterImpl<>(mModel,
                    this, ChartUserActionEnum.values(), ChartQueryEnum.values());
            mPresenter.loadInitialQueries();
        }
    }

    public static void open(Activity activity, int year, int month) {
        Intent intent = new Intent(activity, ChartActivity.class);
        intent.putExtra(EXTRA_YEAR, year);
        intent.putExtra(EXTRA_MONTH, month);
        activity.startActivity(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((v) -> finish());
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
                mUserActionListener.onUserAction(ChartUserActionEnum.SHOW_HISTORY_MONTH_LIST, null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupBaseLineChart() {
        mLineChart.setDescription(null);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setEnabled(false);

        YAxis axisRight = mLineChart.getAxisRight();
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);
        axisRight.setDrawLabels(false);
        axisRight.setEnabled(true);

        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawZeroLine(true);
        axisLeft.setZeroLineWidth(0.2F);
        axisLeft.setZeroLineColor(ResUtils.getColor(getContext(), R.color.colorHint));
    }

    private void setupDailyLineChart() {
        mLineChart.setViewPortOffsets(
                dp2px(getContext(), 16F),
                dp2px(getContext(), 32F),
                dp2px(getContext(), 16F),
                0);

        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setDrawZeroLine(true);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setEnabled(false);
    }

    private void setupMonthlyLineChart(List<Entry> entryList) {
        mLineChart.setViewPortOffsets(
                dp2px(getContext(), 16F),
                dp2px(getContext(), 32F),
                dp2px(getContext(), 16F),
                dp2px(getContext(), 16F));

        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setDrawZeroLine(false);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(ResUtils.getColor(getContext(), R.color.appTextColorLabel));

        List<String> xAxisLabels = new ArrayList<>(entryList.size());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (Entry entry : entryList) {
            MonthlyData expense = (MonthlyData) entry.getData();
            Month month = expense.getMonth();
            String label = month.getYear() == currentYear ?
                    getString(R.string.string_format_date_m, month.getMonth()) :
                    month.getYear() + "/" + month.getMonth();
            xAxisLabels.add(label);
        }
        xAxis.setValueFormatter((value, axis) -> {
            if (xAxisLabels.size() > value) {
                return xAxisLabels.get((int) value);
            }
            return String.valueOf(false);
        });
    }

    private void setupPieChart() {
        mPieChart.setDescription(null);
        mPieChart.setCenterTextSize(20f);
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.getLegend().setEnabled(true);
        mPieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        mPieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mPieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        mPieChart.getLegend().setWordWrapEnabled(true);
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

        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.4f);
        pieDataSet.setValueLinePart2Length(0.8f);
        pieDataSet.setValueLineColor(getResources().getColor(R.color.colorHint));
        pieDataSet.setValueTextColor(getResources().getColor(R.color.appTextColorPrimary));
        pieDataSet.setValueTextSize(9);

        PieData pieData = new PieData(pieDataSet);
        mPieChart.setData(pieData);
        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    private void showDailyExpenseLineChart(List<Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            // TODO showEmptyData
            return;
        }

        setupDailyLineChart();

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setLabel(null);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(ResUtils.getColor(this, R.color.chartLine));
        lineDataSet.setLineWidth(1.0F);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataSet.setHighLightColor(ResUtils.getColor(getContext(), R.color.colorHint));

        int dayCountOfMonth = TimeUtils.getDaysTotalOfMonth(
                mModel.getDisplayMonth().getYear(), mModel.getDisplayMonth().getMonth());
        LineData lineData = new LineData(lineDataSet);

        mLineChart.setData(lineData);
        mLineChart.setSourceType(ExpenseLineChart.TYPE_DAILY_OF_MONTH);
        mLineChart.getXAxis().setAxisMinimum(0);
        mLineChart.getXAxis().setAxisMaximum(dayCountOfMonth);
        mLineChart.resetVisibleXRange();
        mLineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    private void showMonthlyExpenseLineChart(List<MonthlyData> data) {
        List<Entry> entries = generateMonthlyExpenseLineChartEntries(data);
        if (data == null || data.isEmpty()) {
            // TODO show empty data
            return;
        }

        setupMonthlyLineChart(entries);

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setLabel(null);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextColor(ResUtils.getColor(this, R.color.appTextColorLabel));
        lineDataSet.setValueTextSize(12);
        lineDataSet.setColor(ResUtils.getColor(this, R.color.chartLine));
        lineDataSet.setLineWidth(2.0F);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(ResUtils.getColor(this, R.color.chartLine));
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(false);

        LineData lineData = new LineData(lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.setSourceType(ExpenseLineChart.TYPE_MONTHLY);
        mLineChart.getXAxis().setAxisMaximum(lineData.getXMax());
        mLineChart.getXAxis().setAxisMinimum(lineData.getXMin());
        mLineChart.setVisibleXRange(0, 5);

        mLineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    private List<Entry> generateMonthlyExpenseLineChartEntries(List<MonthlyData> expenseList) {
        List<Entry> result = new ArrayList<>(expenseList != null ? expenseList.size() : 0);
        if (expenseList == null || expenseList.isEmpty()) {
            return result;
        }

        MonthlyData preMonthlyExpense = null;
        // 处理后的月份消费数据集合
        List<MonthlyData> formattedList = new ArrayList<>();
        // 本循环是为了处理月份中断问题，从数据库中只会取出有消费记录的月份
        // 因此会出现月份中断的问题，但是在绘图时月份是连续的，此处是为了将没有消费记录的月份穿插起来
        for (MonthlyData expense : expenseList) {
            if (preMonthlyExpense == null) {
                preMonthlyExpense = expense;
                formattedList.add(expense);
                continue;
            }

            while (!preMonthlyExpense.getMonth().next().equals(expense.getMonth())) {
                Month next = preMonthlyExpense.getMonth().next();
                MonthlyData nextMonthlyExpense = new MonthlyData();
                nextMonthlyExpense.setMonth(next);
                nextMonthlyExpense.setAmount(0f);

                preMonthlyExpense = nextMonthlyExpense;
                formattedList.add(nextMonthlyExpense);
            }

            preMonthlyExpense = expense;
            formattedList.add(expense);
        }

        for (int i = 0; i < formattedList.size(); i++) {
            MonthlyData expense = formattedList.get(i);
            Entry entry = new Entry(i, expense.getAmount());
            entry.setData(expense);
            result.add(entry);
        }

        return result;
    }

    @Override
    public void displayData(ChartModel model, ChartModel.ChartQueryEnum query) {
        switch (query) {
            case LOAD_CURRENT_MONTH_DATA:
                showMonthTipViews(
                        model.getDisplayMonth().getYear(), model.getDisplayMonth().getMonth());
                List<DailyData> monthDailyExpenseList = model.getMonthDailyExpenseList();
                showDailyExpenseLineChart(generateLineData(monthDailyExpenseList));
                reDrawPieChart(model.getMonthExpenseList());
                float monthTotal = calculateMonthTotal(model.getMonthExpenseList());
                mMonthTotalTv.setText(
                        getString(R.string.tally_amount_cny, mDecimalFormat.format(monthTotal)));
                mMonthCategoryAdapter.refreshData(model.getMonthCategoryExpenseList());
                mLineChartExpenseTipTv.setText(getString(R.string.tally_daily_expense_tip));
                break;
            default:
                break;
        }
    }

    @Override
    public void displayUserActionResult(ChartModel model,
                                        Bundle args,
                                        ChartModel.ChartUserActionEnum userAction,
                                        boolean success,
                                        IError error) {
        switch (userAction) {
            case SHOW_HISTORY_MONTH_LIST:
                if (success) {
                    List<Month> historyMonthList = model.getHistoryMonthList();
                    showMonthSwitchPopupWindow(historyMonthList);
                }
                break;
            case SWITCH_MONTH:
                if (success) {
                    showMonthTipViews(
                            model.getDisplayMonth().getYear(), model.getDisplayMonth().getMonth());
                    List<DailyData> monthDailyExpenseList = model.getMonthDailyExpenseList();
                    showDailyExpenseLineChart(generateLineData(monthDailyExpenseList));
                    reDrawPieChart(model.getMonthExpenseList());

                    float monthTotal = calculateMonthTotal(model.getMonthExpenseList());
                    mMonthTotalTv.setText(
                            getString(R.string.tally_amount_cny, mDecimalFormat.format(monthTotal)));
                    mMonthCategoryAdapter.refreshData(model.getMonthCategoryExpenseList());
                    mLineChartExpenseTipTv.setText(getString(R.string.tally_daily_expense_tip));
                }
                break;

            case SWITCH_TO_DAILY_DATA:
                if (success) {
                    List<DailyData> monthDailyExpenseList = model.getMonthDailyExpenseList();
                    showDailyExpenseLineChart(generateLineData(monthDailyExpenseList));
                    mLineChartExpenseTipTv.setText(getString(R.string.tally_daily_expense_tip));
                }
                break;

            case SWITCH_TO_MONTHLY_DATA:
                if (success) {
                    showMonthlyExpenseLineChart(model.getMonthlyExpenseList());
                    mLineChartExpenseTipTv.setText(getString(R.string.tally_monthly_expense_tip));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void displayErrorMessage(ChartModel.ChartQueryEnum query, IError error) {

    }

    private List<Entry> generateLineData(List<DailyData> monthDailyExpenseList) {
        List<Entry> entries = new ArrayList<>();
        for (DailyData expense : monthDailyExpenseList) {
            Entry entry = new Entry(expense.getDayOfMonth(), expense.getAmount());
            entry.setData(expense);
            entries.add(entry);
        }
        return entries;
    }

    private float calculateMonthTotal(List<Expense> itemList) {
        float total = 0.0f;
        for (Expense item : itemList) {
            total += item.getAmount();
        }
        return total;
    }

    private void showMonthTipViews(int year, int month) {
        mMonthTv.setText(getString(R.string.tally_month_info_format,
                year, String.format(Locale.getDefault(), "%1$02d", month)));
    }

    /**
     * 显示历史月份列表，在{@link PopupWindow}中弹出
     *
     * @param historyMonthList
     */
    private void showMonthSwitchPopupWindow(List<Month> historyMonthList) {
        new MonthSelectDialog(this, historyMonthList, new MonthSelectDialog.DateSelectListener() {
            @Override
            public void onMonthSelect(MonthSelectDialog dialog, Month month) {
                Bundle args = new Bundle(2);
                args.putInt(EXTRA_YEAR, month.getYear());
                args.putInt(EXTRA_MONTH, month.getMonth());
                mUserActionListener.onUserAction(ChartUserActionEnum.SWITCH_MONTH, args);
                dialog.dismiss();
            }
        }, mModel.getDisplayMonth()).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Uri getDataUri(ChartModel.ChartQueryEnum query) {
        return null;
    }

    @Override
    public void addListener(UserActionListener<ChartUserActionEnum> listener) {
        mUserActionListener = listener;
    }

    private View.OnClickListener mOnclickListener = (view) -> {
        int id = view.getId();
        switch (id) {
            case R.id.ivSwitchChartMode:
                showChartModeSwitchPopup(view);
                break;
            default:
                break;
        }
    };

    private void showChartModeSwitchPopup(View anchor) {

        final String menuTextSwitch2Daily = getString(R.string.tally_switch_2_daily_expense);
        final String menuTextSwitch2Monthly = getString(R.string.tally_switch_2_monthly_expense);
        List<String> menus = Arrays.asList(menuTextSwitch2Daily, menuTextSwitch2Monthly);

        ListPopupWindow popupWindow = PopupUtils.createPopupMenuWindow(ChartActivity.this, anchor, menus);

        popupWindow.setHorizontalOffset(-getResources().getDimensionPixelSize(R.dimen.spacing_normal));
        popupWindow.setVerticalOffset(getResources().getDimensionPixelSize(R.dimen.spacing_normal));
        popupWindow.setDropDownGravity(Gravity.END);
        popupWindow.setOnItemClickListener((parent, view, position, id) -> {
            popupWindow.dismiss();
            String menuText = menus.get(position);
            if (menuTextSwitch2Daily.equals(menuText)) {
                mUserActionListener.onUserAction(ChartUserActionEnum.SWITCH_TO_DAILY_DATA, null);
            } else if (menuTextSwitch2Monthly.equals(menuText)) {
                mUserActionListener.onUserAction(ChartUserActionEnum.SWITCH_TO_MONTHLY_DATA, null);
            }
        });
        popupWindow.show();


    }
}

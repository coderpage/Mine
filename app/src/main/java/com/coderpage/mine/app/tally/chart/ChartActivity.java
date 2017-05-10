package com.coderpage.mine.app.tally.chart;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.chart.data.DailyExpense;
import com.coderpage.mine.app.tally.chart.data.Month;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;
import com.coderpage.utils.AndroidUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coderpage.mine.R.id.lineChart;
import static com.coderpage.mine.app.tally.chart.ChartModel.ChartQueryEnum;
import static com.coderpage.mine.app.tally.chart.ChartModel.ChartUserActionEnum;
import static com.coderpage.mine.app.tally.chart.ChartModel.EXTRA_MONTH;
import static com.coderpage.mine.app.tally.chart.ChartModel.EXTRA_YEAR;

/**
 * @author abner-l. 2017-04-23
 */

public class ChartActivity extends BaseActivity implements
        UpdatableView<ChartModel, ChartModel.ChartQueryEnum, ChartModel.ChartUserActionEnum> {

    private LineChart mLineChart;
    private TextView mMonthTv;
    private TextView mLineChartMonthExpenseTipTv;
    private TextView mLineChartMonthDailySwitcherTv;
    private PieChart mPieChart;
    private PopupWindow mMonthSwitchPopupWindow;

    private Presenter mPresenter;
    private UserActionListener mUserActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tall_chart);
        initView();
        initPresenter();
    }

    private void initView() {
        mLineChart = (LineChart) findViewById(lineChart);
        setupLineChart();
        mMonthTv = (TextView) findViewById(R.id.tvMonth);
        mLineChartMonthExpenseTipTv = (TextView) findViewById(R.id.tvMonthExpenseTip);
        mLineChartMonthDailySwitcherTv = (TextView) findViewById(R.id.tvMonthDailyChartSwitcher);
        mPieChart = (PieChart) findViewById(R.id.pieChart);
        setupPieChart();

        mLineChartMonthDailySwitcherTv.setOnClickListener(mOnClickListener);
        findViewById(R.id.ivMonthSwitch).setOnClickListener(mOnClickListener);
    }

    private void initPresenter() {
        mPresenter = new PresenterImpl(new ChartModel(this),
                this, ChartUserActionEnum.values(), ChartQueryEnum.values());
        mPresenter.loadInitialQueries();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((v) -> finish());
    }

    private void setupLineChart() {
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setGranularity(1f);
        IAxisValueFormatter formatter = (value, axis) -> (int) value + "";
        mLineChart.getXAxis().setValueFormatter(formatter);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setDescription(null);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setDrawAxisLine(false);
        mLineChart.getAxisRight().setDrawGridLines(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getAxisLeft().setDrawAxisLine(false);
        mLineChart.getAxisRight().setDrawAxisLine(false);
        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
    }

    private void setupPieChart() {
        mPieChart.setDescription(null);
//        mPieChart.setEntryLabelTextSize(9f);
        mPieChart.setCenterTextSize(20f);
        mPieChart.setDrawEntryLabels(false);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.getLegend().setEnabled(true);
        mPieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        mPieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mPieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
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
            return;
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setLabel(null);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setColor(getResources().getColor(R.color.chartLine));
        lineDataSet.setCircleColor(getResources().getColor(R.color.chartLine));
        lineDataSet.setLineWidth(1.0f);
        lineDataSet.setCircleRadius(1.3f);
        LineData lineData = new LineData(lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        DrawShadowFrameLayout drawShadowFrameLayout =
                ((DrawShadowFrameLayout) findViewById(R.id.main_content));
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);
    }

    private void setContentTopClearance(int clearance) {
        View rootView = findViewById(R.id.lyContainer);
        if (rootView != null) {
            rootView.setPadding(rootView.getPaddingLeft(), clearance,
                    rootView.getPaddingRight(), rootView.getPaddingBottom());
        }
    }

    @Override
    public void displayData(ChartModel model, ChartModel.ChartQueryEnum query) {
        switch (query) {
            case LOAD_CURRENT_MONTH_DATA:
                showMonthTipViews(
                        model.getDisplayMonth().getYear(), model.getDisplayMonth().getMonth());
                List<DailyExpense> monthDailyExpenseList = model.getMonthDailyExpenseList();
                List<Entry> entries = new ArrayList<>();
                for (DailyExpense expense : monthDailyExpenseList) {
                    entries.add(new Entry(expense.getDayOfMonth(), expense.getExpense()));
                }
                showDailyExpenseLineChart(entries);
                reDrawPieChart(model.getMonthExpenseList());
                break;
        }
    }

    @Override
    public void displayUserActionResult(ChartModel model,
                                        Bundle args,
                                        ChartModel.ChartUserActionEnum userAction,
                                        boolean success) {
        switch (userAction) {
            case SHOW_HISTORY_MONTH_LIST:
                if (success) {
                    List<Month> historyMonthList = model.getHistoryMonthList();
                    showMonthSwitchPopupWindow(historyMonthList);
                }
                break;
            case SWITCH_MONTH:
                if (mMonthSwitchPopupWindow.isShowing()) {
                    mMonthSwitchPopupWindow.dismiss();
                }
                if (success) {
                    showMonthTipViews(
                            model.getDisplayMonth().getYear(), model.getDisplayMonth().getMonth());
                    List<DailyExpense> monthDailyExpenseList = model.getMonthDailyExpenseList();
                    List<Entry> entries = new ArrayList<>();
                    for (DailyExpense expense : monthDailyExpenseList) {
                        entries.add(new Entry(expense.getDayOfMonth(), expense.getExpense()));
                    }
                    showDailyExpenseLineChart(entries);
                    reDrawPieChart(model.getMonthExpenseList());
                }
                break;
        }
    }

    @Override
    public void displayErrorMessage(ChartModel.ChartQueryEnum query) {

    }

    private void showMonthTipViews(int year, int month) {
        mMonthTv.setText(getString(R.string.tally_month_info_format,
                String.format(Locale.getDefault(), "%1$02d", month), year));
        mLineChartMonthExpenseTipTv.setText(getString(R.string.tally_month_expense_tip_format, month));
    }

    /**
     * 显示历史月份列表，在{@link PopupWindow}中弹出
     *
     * @param historyMonthList
     */
    private void showMonthSwitchPopupWindow(List<Month> historyMonthList) {
        View popupView = LayoutInflater.from(ChartActivity.this)
                .inflate(R.layout.layout_tally_chartview_monthpopup, null);
        ListView monthListView = (ListView) popupView.findViewById(R.id.listView);
        if (mMonthSwitchPopupWindow == null) {
            mMonthSwitchPopupWindow = new PopupWindow(getContext(), null, 0, R.style.Widget_PopupWindow);
            monthListView.setAdapter(new MonthAdapter(getContext(), historyMonthList));
            mMonthSwitchPopupWindow.setContentView(popupView);
            mMonthSwitchPopupWindow.setOutsideTouchable(true);
        }
        if (!mMonthSwitchPopupWindow.isShowing()) {
            mMonthSwitchPopupWindow.showAsDropDown(findViewById(R.id.ivMonthSwitch),
                    -AndroidUtils.dip2px(getContext(), 24 + 16 + 16),
                    // xOff = (width of ivMonthSwitch) + (padding of ivMonthSwitch) + (right padding)
                    0);
        }
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
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }

    private View.OnClickListener mOnClickListener = (view) -> {
        int id = view.getId();
        switch (id) {
            case R.id.ivMonthSwitch:
                mUserActionListener.onUserAction(ChartUserActionEnum.SHOW_HISTORY_MONTH_LIST, null);
                break;
        }
    };

    private class MonthAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Month> mMonthList;

        MonthAdapter(Context context, List<Month> monthList) {
            mMonthList = monthList;
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mMonthList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMonthList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_string_simple, parent, false);
            }
            Month month = mMonthList.get(position);
            TextView textView = (TextView) convertView.findViewById(R.id.tvText);
            textView.setText(month.getYear() + "/" + month.getMonth());
            convertView.setOnClickListener((view) -> {
                Bundle args = new Bundle(2);
                args.putInt(EXTRA_YEAR, month.getYear());
                args.putInt(EXTRA_MONTH, month.getMonth());
                mUserActionListener.onUserAction(ChartUserActionEnum.SWITCH_MONTH, args);
            });
            return convertView;
        }
    }

}

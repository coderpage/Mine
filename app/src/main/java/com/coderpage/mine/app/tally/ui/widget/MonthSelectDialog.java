package com.coderpage.mine.app.tally.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.coderpage.base.utils.CommonUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.chart.data.Month;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author lc. 2017-10-12 21:22
 * @since 0.5.1
 *
 * 选择年份和月份；用于 {@link com.coderpage.mine.app.tally.chart.ChartActivity}
 */

public class MonthSelectDialog extends Dialog {

    private static final String TAG = makeLogTag(MonthSelectDialog.class);

    /** 年份选择 TabLayout */
    private TabLayout mYearTabLayout;

    /** 月份选择 RecyclerView */
    private RecyclerView mMonthRecyclerView;

    private MonthRecyclerAdapter mMonthRecyclerAdapter;

    /** 所有可以选择的月份，需要渲染的数据 */
    private List<Month> mMonthList;

    /** 选中的年份，不一定和 {@link #mSelectedMonth} 年份相同 */
    private int mSelectedYear;

    /** 当前选中的月份 */
    private Month mSelectedMonth = null;

    private DateSelectListener mDateSelectListener = null;

    public MonthSelectDialog(Context context,
                             List<Month> monthList,
                             DateSelectListener dateSelectListener,
                             Month selectedMonth) {
        super(context, R.style.Widget_Dialog_MonthSelect);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tally_widget_dialog_month_select);
        // 设置 Window
        configWindowSizeAndPosition();

        mMonthList = monthList;
        mDateSelectListener = dateSelectListener;
        mSelectedMonth = selectedMonth;
        if (mSelectedMonth == null) {
            Calendar calendar = Calendar.getInstance();
            mSelectedMonth = new Month(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        }
        mSelectedYear = mSelectedMonth.getYear();

        initView();
    }

    /**
     * 日期切换监听器
     */
    public static abstract class DateSelectListener {
        /**
         * 当年份被选中时被回调
         *
         * @param year 被选中的年份
         */
        public void onYearSelect(MonthSelectDialog dialog, int year) {
            // no-op
        }

        /**
         * 当月份被选中时被回调
         *
         * @param month 被选中的年份
         */
        public void onMonthSelect(MonthSelectDialog dialog, Month month) {
            // no-op
        }
    }

    /**
     * 设置 Dialog 的位置大小等
     */
    private void configWindowSizeAndPosition() {
        Window window = getWindow();
        if (window == null) return;

        Point windowSize = UIUtils.getWindowSize(getContext());

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.TOP;
        attributes.width = windowSize.x;
        attributes.x = 0;
        attributes.y = getContext().getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        window.setAttributes(attributes);
    }

    /**
     * 初始化 View
     */
    private void initView() {
        View rootView = findViewById(R.id.lyContainer);

        mMonthRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerMonth);
        mMonthRecyclerAdapter = new MonthRecyclerAdapter(
                getMonthListByYear(mMonthList, mSelectedYear));
        mMonthRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mMonthRecyclerView.setAdapter(mMonthRecyclerAdapter);

        mYearTabLayout = (TabLayout) rootView.findViewById(R.id.tabYear);
        initYearTabs();

    }

    private void initYearTabs() {
        List<Integer> yearList = getYearList(mMonthList);

        mYearTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                TextView yearTv = (TextView) customView.findViewById(R.id.tvYear);
                yearTv.setSelected(true);
                mSelectedYear = CommonUtils.string2int(yearTv.getText().toString());
                mDateSelectListener.onYearSelect(MonthSelectDialog.this, mSelectedYear);
                mMonthRecyclerAdapter.refreshData(getMonthListByYear(mMonthList, mSelectedYear));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                TextView yearTv = (TextView) customView.findViewById(R.id.tvYear);
                yearTv.setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (Integer year : yearList) {
            TabLayout.Tab tab = mYearTabLayout.newTab();

            View tabView = getLayoutInflater().inflate(R.layout.tally_tab_year, null, false);
            ((TextView) tabView.findViewById(R.id.tvYear)).setText(String.valueOf(year));
            tabView.setOnClickListener(v -> tab.select());

            tab.setCustomView(tabView);
            mYearTabLayout.addTab(tab, year == mSelectedYear);
        }
    }

    private List<Month> getMonthListByYear(List<Month> source, int year) {
        List<Month> result = new ArrayList<>(12);
        if (source == null || source.isEmpty()) return result;
        for (Month month : source) {
            if (month.getYear() == year) {
                result.add(month);
            }
        }
        return result;
    }

    private List<Integer> getYearList(List<Month> source) {
        List<Integer> result = new ArrayList<>();
        if (source == null || source.isEmpty()) return result;
        for (Month month : source) {
            if (!result.contains(month.getYear())) {
                result.add(month.getYear());
            }
        }
//        source.forEach(month -> {
//            if (!result.contains(month.getYear())) {
//                result.add(month.getYear());
//            }
//        });
        return result;
    }

    private class MonthRecyclerAdapter extends RecyclerView.Adapter<MonthViewHolder>
            implements OnMonthSelectedListener {

        private LayoutInflater mInflater;

        private List<Month> mDataList = new ArrayList<>(12);

        private MonthRecyclerAdapter(List<Month> dataList) {
            mInflater = LayoutInflater.from(getContext());
            mDataList.clear();
            mDataList.addAll(dataList);
        }

        private void refreshData(List<Month> dataList) {
            if (dataList == null) return;
            mDataList.clear();
            mDataList.addAll(dataList);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthViewHolder(
                    mInflater.inflate(R.layout.tally_recycler_item_month_select, parent, false), this);
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, int position) {
            Month month = mDataList.get(position);
            holder.setData(month);
        }

        @Override
        public void onMonthSelected(MonthViewHolder holder, Month month) {
            int currentSelectedPosition = currentSelectedMonthPosition();

            holder.mRootView.setSelected(true);
            mSelectedMonth = month;
            mDateSelectListener.onMonthSelect(MonthSelectDialog.this, mSelectedMonth);

            if (currentSelectedPosition != -1) {
                notifyItemChanged(currentSelectedPosition);
            }
        }

        private int currentSelectedMonthPosition() {
            for (int i = 0; i < mDataList.size(); i++) {
                Month month = mDataList.get(i);
                if (CommonUtils.isEqual(mSelectedMonth, month)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private class MonthViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mRootView;

        private TextView mMonthTv;

        private Month mMonth;

        private OnMonthSelectedListener mMonthSelectedListener;

        private MonthViewHolder(View view, OnMonthSelectedListener listener) {
            super(view);
            mMonthSelectedListener = listener;
            mRootView = view;
            mRootView.setOnClickListener(this);
            mMonthTv = (TextView) view.findViewById(R.id.tvMonth);
        }

        @Override
        public void onClick(View v) {
            if (mRootView.isSelected()) {
                return;
            }
            mMonthSelectedListener.onMonthSelected(this, mMonth);
        }

        private void setData(Month month) {
            mMonth = month;
            mMonthTv.setText(mMonth.getYear() + "/" + month.getMonth());

            boolean isSelected = CommonUtils.isEqual(mMonth, mSelectedMonth);
            mRootView.setSelected(isSelected);
        }
    }

    private interface OnMonthSelectedListener {
        void onMonthSelected(MonthViewHolder holder, Month month);
    }
}

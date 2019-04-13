package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.utils.TallyUtils;
import com.coderpage.mine.app.tally.databinding.CommonBindAdapter;
import com.coderpage.mine.app.tally.module.chart.data.Month;
import com.coderpage.mine.app.tally.module.chart.data.MonthlyEntryData;
import com.coderpage.mine.common.Font;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * @author lc. 2019-04-08 23:25
 * @since 0.6.0
 *
 * 年账单折线图。每月的数据
 */

public class MarkerViewMonthData extends MarkerView {

    private TextView mTvData;
    private TextView mTvDate;

    public MarkerViewMonthData(Context context, int layoutResource) {
        super(context, layoutResource);
        mTvData = findViewById(R.id.tvData);
        mTvDate = findViewById(R.id.tvDate);
        CommonBindAdapter.setTypeFace(mTvData, Font.QUICKSAND_MEDIUM);
        CommonBindAdapter.setTypeFace(mTvDate, Font.QUICKSAND_BOLD);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        MonthlyEntryData entryData = (MonthlyEntryData) e.getData();
        Month month = entryData.getMonth();
        String monthInfo = ResUtils.getString(getContext(), R.string.tally_month_info_format,
                month.getYear(), month.getMonth());
        String moneyInfo = ResUtils.getString(getContext(), R.string.tally_income_and_expense_format,
                "¥" + TallyUtils.formatDisplayMoney(entryData.getIncomeAmount()),
                "¥" + TallyUtils.formatDisplayMoney(entryData.getExpenseAmount()));
        mTvDate.setText(monthInfo);
        mTvData.setText(moneyInfo);
        super.refreshContent(e, highlight);
    }
}

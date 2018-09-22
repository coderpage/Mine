package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.chart.data.DailyExpense;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author abner-l. 2017-07-02
 * @since 0.1.0
 */

public class DailyExpenseMarkerView extends MarkerView {

    private TextView mDateTv;
    private TextView mExpenseAmountTv;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    private SimpleDateFormat mDateFormat;

    public DailyExpenseMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mExpenseAmountTv = (TextView) findViewById(R.id.tvExpenseAmount);
        mDateTv = (TextView) findViewById(R.id.tvDate);
        mDateFormat = new SimpleDateFormat(
                ResUtils.getString(context, R.string.date_format_m_d_e), Locale.getDefault());
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DailyExpense expense = (DailyExpense) e.getData();
        mExpenseAmountTv.setText(mDecimalFormat.format(expense.getExpense()));
        mDateTv.setText(mDateFormat.format(new Date(expense.getTimeMillis())));
        super.refreshContent(e, highlight);
    }

}

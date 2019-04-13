package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.utils.TallyUtils;
import com.coderpage.mine.app.tally.databinding.CommonBindAdapter;
import com.coderpage.mine.app.tally.module.chart.data.DailyData;
import com.coderpage.mine.common.Font;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * @author lc. 2019-04-09 22:11
 * @since 0.6.0
 *
 * 月账单柱状图。每日的数据
 */

public class MarkerViewDailyData extends MarkerView {

    private TextView mTvData;
    private TextView mTvDate;

    public MarkerViewDailyData(Context context, int layoutResource) {
        super(context, layoutResource);
        mTvData = findViewById(R.id.tvData);
        mTvDate = findViewById(R.id.tvDate);
        CommonBindAdapter.setTypeFace(mTvData, Font.QUICKSAND_MEDIUM);
        CommonBindAdapter.setTypeFace(mTvDate, Font.QUICKSAND_BOLD);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DailyData dailyData = (DailyData) e.getData();
        String dayInfo = ResUtils.getString(getContext(), R.string.tally_day_info_format,
                dailyData.getYear(), dailyData.getMonth(), dailyData.getDayOfMonth());
        String moneyInfo = "¥" + TallyUtils.formatDisplayMoney(dailyData.getAmount());
        mTvDate.setText(dayInfo);
        mTvData.setText(moneyInfo);
        super.refreshContent(e, highlight);
    }
}

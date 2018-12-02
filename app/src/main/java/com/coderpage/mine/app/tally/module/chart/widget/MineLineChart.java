package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.github.mikephil.charting.charts.LineChart;

/**
 * @author lc. 2018-09-30 14:12
 * @since 0.6.0
 */

public class MineLineChart extends LineChart {

    public MineLineChart(Context context) {
        super(context);
    }

    public MineLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MineLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new MineLineChartRender(this, mAnimator, mViewPortHandler);
        ((MineLineChartRender) mRenderer).setValueGridLineColor(ResUtils.getColor(getContext(), R.color.chartGridLine));
    }

}

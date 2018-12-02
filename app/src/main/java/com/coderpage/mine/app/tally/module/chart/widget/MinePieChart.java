package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;

/**
 * @author lc. 2018-10-10 09:28
 * @since 0.6.0
 */

public class MinePieChart extends PieChart {

    public MinePieChart(Context context) {
        super(context);
    }

    public MinePieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinePieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new MinePieChartRenderer(this, mAnimator, mViewPortHandler);
    }
}

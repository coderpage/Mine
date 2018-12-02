package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

/**
 * @author lc. 2018-09-29 16:22
 * @since 0.6.0
 */

public class MineBarChart extends BarChart {

    public MineBarChart(Context context) {
        super(context);
    }

    public MineBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MineBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new MineBarChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        super.drawMarkers(canvas);
    }
}

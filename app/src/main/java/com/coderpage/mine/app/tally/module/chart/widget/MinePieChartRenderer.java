package com.coderpage.mine.app.tally.module.chart.widget;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author lc. 2018-10-10 09:30
 * @since 0.6.0
 */

public class MinePieChartRenderer extends PieChartRenderer {

    public MinePieChartRenderer(PieChart chart, ChartAnimator animator,
                                ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }
}

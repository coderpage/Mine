package com.coderpage.mine.app.tally.module.chart.widget;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;

/**
 * @author lc. 2019-05-19 14:59
 * @since 0.6.2
 */

public class MineLineChartTouchListener extends BarLineChartTouchListener {

    private boolean mHandlerByMarkerView = false;
    private float mDownX;
    private float mDownY;

    public MineLineChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends
            IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart, touchMatrix, dragTriggerDistance);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 处理 marker view 点击
        IMarker marker = mChart.getMarker();
        if (marker instanceof MarkViewMine) {
            MarkViewMine markerView = (MarkViewMine) marker;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    mHandlerByMarkerView = markerView.getBound().contains(event.getX(), event.getY());
                    if (mHandlerByMarkerView) {
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (mHandlerByMarkerView) {
                        if (Math.abs(mDownX - event.getX()) <= 5 && Math.abs(mDownY - event.getY()) <= 5) {
                            markerView.performClick();
                        }
                        return true;
                    }
                    break;

                default:
                    break;
            }
        }
        return super.onTouch(v, event);
    }
}

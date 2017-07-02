package com.coderpage.mine.app.tally.chart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.coderpage.mine.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

/**
 * @author abner-l. 2017-07-02
 * @since 0.1.0
 */

public class DailyExpenseLineChart extends LineChart {

    private DailyExpenseMarkerView mMarkerView;

    public DailyExpenseLineChart(Context context) {
        super(context);
    }

    public DailyExpenseLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DailyExpenseLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 参考 http://www.jianshu.com/p/fe3d109eb27e
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (mMarkerView == null) {
            mMarkerView = new DailyExpenseMarkerView(
                    getContext(), R.layout.widget_tally_daily_expense_markerview);
        }

        if (!isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
                continue;

            if (mMarkerView != null) {
                int measuredHeight = mMarkerView.getHeight();
                int measuredWidth = mMarkerView.getWidth();

                float x = highlight.getDrawX() - measuredWidth / 2;
                if (!mViewPortHandler.isInBoundsLeft(x)) {
                    x = mViewPortHandler.contentLeft();
                }
                if (x + measuredWidth >= mViewPortHandler.contentRight()) {
                    x = mViewPortHandler.contentRight() - measuredWidth;
                }

                float y = mViewPortHandler.contentTop() - measuredHeight;

                // check bounds
//                if (!mViewPortHandler.isInBounds(x, y))
//                    continue;

                // callbacks to update the content
                mMarkerView.refreshContent(e, highlight);

//                canvas.drawText("xxx", x, y, mBorderPaint);

                // draw the marker
                mMarkerView.draw(canvas, x, y);
            }
        }
    }
}

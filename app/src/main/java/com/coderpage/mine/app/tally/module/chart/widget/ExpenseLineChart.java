package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.IntDef;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author abner-l. 2017-07-02
 * @since 0.1.0
 */

public class ExpenseLineChart extends LineChart {

    /**
     * 显示的数据为为一个月的日消费折线图
     */
    public static final int TYPE_DAILY_OF_MONTH = 1;

    /**
     * 显示的数据为月消费折线图
     */
    public static final int TYPE_MONTHLY = 2;

    @IntDef(flag = true, value = {TYPE_DAILY_OF_MONTH, TYPE_MONTHLY})
    public @interface SourceType {
    }

    /**
     * 折线图显示数据的类型
     *
     * @see #TYPE_MONTHLY {@link #TYPE_DAILY_OF_MONTH}
     */
    private int mSourceType;

    private DailyExpenseMarkerView mMarkerView;

    public ExpenseLineChart(Context context) {
        super(context);
    }

    public ExpenseLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpenseLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new MyLineChartRenderer(
                -UIUtils.dp2px(getContext(), 8), this, mAnimator, mViewPortHandler);
    }

    /**
     * 设置折线图显示数据的类型。
     *
     * @param type {@link #TYPE_DAILY_OF_MONTH {@link #TYPE_MONTHLY}}
     */
    public void setSourceType(@SourceType int type) {
        mSourceType = type;
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
    public void setVisibleXRange(float minXRange, float maxXRange) {
        super.setVisibleXRange(minXRange, maxXRange);
    }

    public void resetVisibleXRange() {
        mViewPortHandler.setMinMaxScaleX(1, 1);
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (mSourceType == TYPE_DAILY_OF_MONTH) {
            drawDailyChartMarkers(canvas);
        }
    }

    private void drawDailyChartMarkers(Canvas canvas) {
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
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX()) {
                continue;
            }

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

    private class MyLineChartRenderer extends LineChartRenderer {

        @Px
        private int yOffset;

        private MyLineChartRenderer(int yOffset,
                                    LineDataProvider chart,
                                    ChartAnimator animator,
                                    ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);
            this.yOffset = yOffset;
        }

        @Override
        public void drawValue(Canvas c,
                              IValueFormatter formatter,
                              float value, Entry entry,
                              int dataSetIndex,
                              float x,
                              float y, int color) {
            super.drawValue(c, formatter, value, entry, dataSetIndex, x, y + this.yOffset, color);
        }

        @Override
        public void drawValues(Canvas c) {
            super.drawValues(c);
        }
    }
}

package com.coderpage.mine.app.tally.module.chart.widget;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author lc. 2018-09-29 16:24
 * @since 0.6.0
 */

public class MineBarChartRenderer extends BarChartRenderer {

    public MineBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                                ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    private RectF mBarShadowRectBuffer = new RectF();
    private RectF mBarRectBuffer = new RectF();
    private RectF mBarHighlightRectBuffer = new RectF();
    private float[] mBarRectRadii = new float[8];

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    continue;
                }

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) {
                    break;
                }

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                mBarShadowRectBuffer.left = mBarShadowRectBuffer.left + mBarShadowRectBuffer.width() / 2 - 0.5F;
                mBarShadowRectBuffer.right = mBarShadowRectBuffer.left + 1;

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        Path path = new Path();
        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                continue;
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break;
            }

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }

            // 绘制圆角矩形
            mBarRectBuffer.set(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]);

            float radii = mBarRectBuffer.width() / 2;
            mBarRectRadii[0] = radii;
            mBarRectRadii[1] = radii;
            mBarRectRadii[2] = radii;
            mBarRectRadii[3] = radii;
            mBarRectRadii[4] = 0;
            mBarRectRadii[5] = 0;
            mBarRectRadii[6] = 0;
            mBarRectRadii[7] = 0;

            path.addRoundRect(mBarRectBuffer, mBarRectRadii, Path.Direction.CW);
        }
        c.drawPath(path, mRenderPaint);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        BarData barData = mChart.getBarData();

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled()) {
                continue;
            }

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set)) {
                continue;
            }

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = high.getStackIndex() >= 0 && e.isStacked();

            final float y1;
            final float y2;

            if (isStack) {

                if (mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {

                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

            setHighlightDrawPos(high, mBarRect);

            Path path = new Path();
            float radii = mBarRect.width() / 2;
            mBarRectRadii[0] = radii;
            mBarRectRadii[1] = radii;
            mBarRectRadii[2] = radii;
            mBarRectRadii[3] = radii;
            mBarRectRadii[4] = 0;
            mBarRectRadii[5] = 0;
            mBarRectRadii[6] = 0;
            mBarRectRadii[7] = 0;

            path.addRoundRect(mBarRect, mBarRectRadii, Path.Direction.CW);

            float barWidth = barData.getBarWidth();
            float barWidthHalf = barWidth / 2.0f;
            float x = mBarRect.left + mBarRect.width() / 2;

            mBarHighlightRectBuffer.top = mViewPortHandler.contentTop();
            mBarHighlightRectBuffer.bottom = mViewPortHandler.contentBottom();
            mBarHighlightRectBuffer.left = x - barWidthHalf;
            mBarHighlightRectBuffer.right = x + barWidthHalf;
            path.addRect(mBarHighlightRectBuffer, Path.Direction.CW);

            c.drawPath(path, mHighlightPaint);
        }
    }


}

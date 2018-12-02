package com.coderpage.mine.app.tally.module.chart.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * @author lc. 2018-10-21 10:29
 * @since 0.6.0
 */

class MineLineChartRender extends LineChartRenderer {

    private int mValueGridLineColor =Color.GRAY;
    private float[] mValueGridLineBuffer = new float[2];
    private Paint mValueGridLinePaint = new Paint();
    private RectF mValueGridLineRect = new RectF();

    MineLineChartRender(LineDataProvider chart, ChartAnimator animator,
                        ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        mValueGridLinePaint.setColor(mValueGridLineColor);
        mValueGridLinePaint.setAntiAlias(true);
    }

    public void setValueGridLineColor(int color) {
        mValueGridLineColor = color;
        mValueGridLinePaint.setColor(color);
    }

    @Override
    public void drawExtras(Canvas c) {
        drawValueGridLine(c);
        drawCircles(c);
    }

    @Override
    protected void drawCircles(Canvas c) {
        super.drawCircles(c);
    }

    /**
     * 在每个 x值 位置绘制一条竖线
     */
    private void drawValueGridLine(Canvas c) {
        float phaseY = mAnimator.getPhaseY();
        mValueGridLineBuffer[0] = 0;
        mValueGridLineBuffer[1] = 0;

        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {

            ILineDataSet dataSet = dataSets.get(i);

            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled() ||
                    dataSet.getEntryCount() == 0) {
                continue;
            }

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            int boundsRangeCount = mXBounds.range + mXBounds.min;

            for (int j = mXBounds.min; j <= boundsRangeCount; j++) {

                Entry e = dataSet.getEntryForIndex(j);

                if (e == null) {
                    break;
                }

                mValueGridLineBuffer[0] = e.getX();
                mValueGridLineBuffer[1] = e.getY() * phaseY;

                trans.pointValuesToPixel(mValueGridLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mValueGridLineBuffer[0])) {
                    break;
                }

                if (!mViewPortHandler.isInBoundsLeft(mValueGridLineBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(mValueGridLineBuffer[1])) {
                    continue;
                }

                float left = mValueGridLineBuffer[0] - 0.5F;
                float top = mViewPortHandler.contentTop();
                float right = left + 1;
                float bottom = mViewPortHandler.contentBottom();
                mValueGridLineRect.set(left, top, right, bottom);
                c.drawRect(mValueGridLineRect, mValueGridLinePaint);
            }
        }
    }
}

package com.coderpage.mine.app.tally.module.chart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * @author lc. 2019-05-19 15:28
 * @since 0.6.2
 */

public class MarkViewMine extends MarkerView {

    private RectF mBound = new RectF();

    public MarkViewMine(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, posY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);

        float left = posX + offset.x;
        float top = posY + offset.y;
        float right = left + getMeasuredWidth();
        float bottom = top + getMeasuredHeight();
        mBound.set(left, top, right, bottom);
    }

    public RectF getBound() {
        return mBound;
    }
}

package com.coderpage.mine.app.tally.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.FrameLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-07-11 14:19
 * @since 0.6.0
 */

public class TopLinearPercentView extends FrameLayout {

    private int mEmptyTextSize = 14;
    private int mLabelTextSize = 11;

    private int mEmptyTextColor = Color.GRAY;
    private int mLabelTextColor = Color.GRAY;

    private int mDrawTopCount = 3;

    private double mTotalAmount;
    private double mTopTotalAmount;

    private int[] mColorArray = null;

    private Paint mChartPaint;

    private TextPaint mTextPaint;

    private RectF mDrawRectF = new RectF();

    private List<Pair<String, Double>> mDataList = new ArrayList<>();

    private LabelFormatter mLabelFormatter = new LabelFormatter();

    public TopLinearPercentView(Context context) {
        this(context, null);
    }

    public TopLinearPercentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopLinearPercentView(@NonNull Context context, @Nullable AttributeSet attrs,
                                @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(21)
    public TopLinearPercentView(@NonNull Context context, @Nullable AttributeSet attrs,
                                @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setWillNotDraw(false);
        mChartPaint = new Paint();
        mChartPaint.setAntiAlias(true);
        mChartPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
    }

    /** 设置空数据文字颜色 */
    public void setEmptyTextColor(int color) {
        this.mEmptyTextColor = color;
    }

    /** 设置空数据文字大小 */
    public void setEmptyTextSize(int size) {
        this.mEmptyTextSize = size;
    }

    /** 设置图例文字颜色 */
    public void setLabelTextColor(int color) {
        this.mLabelTextColor = color;
    }

    /** 设置图例文字大小 */
    public void setLabelTextSize(int size) {
        this.mLabelTextSize = size;
    }

    /** 设置图表颜色 */
    public void setColorArray(int... color) {
        mColorArray = color;
    }

    /** 设置数据集合 */
    public void setData(List<Pair<String, Double>> data) {
        if (data == null) {
            return;
        }
        mDataList.clear();
        mDataList.addAll(data);
        mDrawTopCount = Math.min(mDataList.size(), mDrawTopCount);

        mTopTotalAmount = 0;
        mTotalAmount = 0;
        for (int i = 0; i < data.size(); i++) {
            mTotalAmount += data.get(i).second;
            if (i < mDrawTopCount) {
                mTopTotalAmount += data.get(i).second;
            }
        }

        postInvalidate();
    }

    /** 设置绘制的数量 */
    public void setDrawTopCount(int drawTopCount) {
        mDrawTopCount = Math.min(mDrawTopCount, drawTopCount);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制无数据文字提示
        if (mDrawTopCount == 0 || mTopTotalAmount == 0) {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(mEmptyTextSize);
            mTextPaint.setColor(mEmptyTextColor);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textTop = fontMetrics.top;
            float textBottom = fontMetrics.bottom;
            int baseLineY = (int) (getHeight() / 2 - textTop / 2 - textBottom / 2);
            canvas.drawText("暂无数据~", getWidth() / 2, baseLineY, mTextPaint);
            return;
        }

        int color;
        // 图例高度
        int legendHeight = mLabelTextSize + dip2px(getContext(), 8);

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom() - legendHeight;

        mDrawRectF.set(left, top, left, bottom);
        int drawWidth = right - left;
        // 绘制前X比例图
        for (int i = 0; i < mDrawTopCount; i++) {
            color = (mColorArray == null || mColorArray.length == 0) ? Color.GRAY : mColorArray[i % mColorArray.length];
            mChartPaint.setColor(color);

            Pair<String, Double> pair = mDataList.get(i);
            double drawPercent = pair.second / mTotalAmount;

            mDrawRectF.left = mDrawRectF.right;
            mDrawRectF.right = mDrawRectF.left + (int) (drawWidth * drawPercent);
            canvas.drawRect(mDrawRectF, mChartPaint);
        }
        // 绘制其他的比例图
        color = (mColorArray == null || mColorArray.length == 0) ? Color.GRAY : mColorArray[mDrawTopCount % mColorArray.length];
        mChartPaint.setColor(color);
        double drawPercent = (mTotalAmount - mTopTotalAmount) / mTotalAmount;
        mDrawRectF.left = mDrawRectF.right;
        mDrawRectF.right = mDrawRectF.left + (int) (drawWidth * drawPercent);
        canvas.drawRect(mDrawRectF, mChartPaint);

        // 图例文字属性
        mTextPaint.setColor(mLabelTextColor);
        mTextPaint.setTextSize(mLabelTextSize);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textTop = fontMetrics.top;
        float textBottom = fontMetrics.bottom;
        int baseLineY = (int) (mDrawRectF.bottom + legendHeight + mDrawRectF.bottom - textBottom - textTop) / 2;

        // 图例图标大小
        int legendIconSize = dip2px(getContext(), 6);
        int legendPadding = dip2px(getContext(), 2);

        mDrawRectF.left = left;
        mDrawRectF.top = mDrawRectF.bottom + (legendHeight - legendIconSize) / 2;
        mDrawRectF.right = mDrawRectF.left + legendIconSize;
        mDrawRectF.bottom = mDrawRectF.top + legendIconSize;

        // 绘制图例
        for (int i = 0; i < mDrawTopCount; i++) {
            Pair<String, Double> pair = mDataList.get(i);

            color = (mColorArray == null || mColorArray.length == 0) ? Color.GRAY : mColorArray[i % mColorArray.length];
            mChartPaint.setColor(color);
            canvas.drawRect(mDrawRectF, mChartPaint);
            String labelText = mLabelFormatter.formatLabel(pair.first, pair.second, pair.second / mTotalAmount);
            canvas.drawText(labelText, mDrawRectF.right + legendPadding, baseLineY, mTextPaint);

            float textWidth = mTextPaint.measureText(labelText);
            mDrawRectF.left = mDrawRectF.right + legendIconSize * 3 + textWidth;
            mDrawRectF.right = mDrawRectF.left + legendIconSize;
        }

        // 绘制其他图例
        color = (mColorArray == null || mColorArray.length == 0) ? Color.GRAY : mColorArray[mDrawTopCount % mColorArray.length];
        mChartPaint.setColor(color);
        canvas.drawRect(mDrawRectF, mChartPaint);
        String labelText = mLabelFormatter.formatLabel("其他", mTotalAmount - mTopTotalAmount, (mTotalAmount - mTopTotalAmount) / mTotalAmount);
        canvas.drawText(labelText, mDrawRectF.right + legendPadding, baseLineY, mTextPaint);
    }

    private class LabelFormatter {
        private DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String formatLabel(String label, double value, double percent) {
            return label + "(" + decimalFormat.format(value) + ")";
        }
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

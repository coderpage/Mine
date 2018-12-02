package com.coderpage.mine.app.tally.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.coderpage.mine.R;


/**
 * @author lc. 2018-09-19 17:19
 * @since 0.6.0
 */

public class ViewPagerFocusView extends View {

    /** 指示器未选中颜色 */
    private int mIndicatorNormalColor = Color.GRAY;
    /** 指示器选中颜色 */
    private int mIndicatorSelectColor = Color.BLACK;
    /** 指示器宽度 */
    private int mIndicatorWidth = 9;
    /** 指示器高度 */
    private int mIndicatorHeight = 9;

    /** 指示器间隔 */
    private int mIndicatorInterval = 18;

    private ViewPager mViewPager;
    private PagerChangeListener mPageChangeListener;
    private PageAdapterChangeListener mPageAdapterChangeListener;
    private PageDataObserver mPageDataObserver;
    private PagerAdapter mPagerAdapter;
    private Paint mIndicatorPaint;
    private RectF mIndicatorRect;

    public ViewPagerFocusView(Context context) {
        this(context, null);
    }

    public ViewPagerFocusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorRect = new RectF();

        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerFocusView);

        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.ViewPagerFocusView_vpfIndicatorWidth, mIndicatorWidth);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.ViewPagerFocusView_vpfIndicatorHeight, mIndicatorHeight);
        mIndicatorInterval = typedArray.getDimensionPixelSize(R.styleable.ViewPagerFocusView_vpfIndicatorInterval, mIndicatorInterval);
        mIndicatorNormalColor = typedArray.getColor(R.styleable.ViewPagerFocusView_vpfIndicatorColorNormal, mIndicatorNormalColor);
        mIndicatorSelectColor = typedArray.getColor(R.styleable.ViewPagerFocusView_vpfIndicatorColorSelect, mIndicatorSelectColor);

        typedArray.recycle();
    }

    public void setupWithViewPager(ViewPager viewPager) {
        if (viewPager == mViewPager) {
            return;
        }

        // clear
        if (mViewPager != null) {
            if (mPageChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mPageChangeListener);
            }
            if (mPagerAdapter != null && mPageDataObserver != null) {
                mPagerAdapter.unregisterDataSetObserver(mPageDataObserver);
            }
            if (mPageAdapterChangeListener != null) {
                mViewPager.removeOnAdapterChangeListener(mPageAdapterChangeListener);
            }

            mPagerAdapter = null;
        }

        // set new
        mViewPager = viewPager;
        if (mViewPager != null) {
            mPageChangeListener = new PagerChangeListener();
            mPageAdapterChangeListener = new PageAdapterChangeListener();

            mViewPager.addOnPageChangeListener(mPageChangeListener);
            mViewPager.addOnAdapterChangeListener(mPageAdapterChangeListener);

            mPagerAdapter = mViewPager.getAdapter();
            if (mPagerAdapter != null) {
                mPageDataObserver = new PageDataObserver();
                mPagerAdapter.registerDataSetObserver(mPageDataObserver);
            }
        }

        update();
    }

    private void setViewPagerAdapter(@Nullable PagerAdapter adapter) {
        if (mPagerAdapter != null && mPageDataObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(mPageDataObserver);
        }
        mPagerAdapter = adapter;
        if (mPagerAdapter != null) {
            if (mPageDataObserver == null) {
                mPageDataObserver = new PageDataObserver();
            }
            mPagerAdapter.registerDataSetObserver(mPageDataObserver);
        }
        update();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mPagerAdapter == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int count = mPagerAdapter.getCount();
        if (count <= 1) {
            setMeasuredDimension(0, 0);
            return;
        }

        int width = count * mIndicatorWidth + (count - 1) * mIndicatorInterval + getPaddingLeft() + getPaddingRight();
        int height = mIndicatorHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    private void update() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null || mPagerAdapter == null || mPagerAdapter.getCount() <= 1) {
            return;
        }
        mIndicatorPaint.setColor(mIndicatorNormalColor);
        mIndicatorRect.set(getLeft(), getPaddingTop(), mIndicatorWidth, getPaddingTop() + mIndicatorHeight);

        int currentItem = mViewPager.getCurrentItem();
        int count = mPagerAdapter.getCount();
        for (int i = 0; i < count; i++) {

            int left = getPaddingLeft() + (mIndicatorWidth + mIndicatorInterval) * i;
            int right = left + mIndicatorWidth;
            int top = getPaddingTop();
            int bottom = top + mIndicatorHeight;

            mIndicatorRect.set(left, top, right, bottom);

            mIndicatorPaint.setColor(i == currentItem ? mIndicatorSelectColor : mIndicatorNormalColor);
            canvas.drawRoundRect(mIndicatorRect, mIndicatorHeight, mIndicatorHeight, mIndicatorPaint);
        }
    }

    private class PagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            update();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class PageDataObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            update();
        }

        @Override
        public void onInvalidated() {
            update();
        }
    }

    private class PageAdapterChangeListener implements ViewPager.OnAdapterChangeListener {
        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            if (viewPager == mViewPager) {
                setViewPagerAdapter(newAdapter);
            }
        }
    }
}

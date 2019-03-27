package com.coderpage.mine.app.tally.ui.refresh;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.lcodecore.tkrefreshlayout.IBottomView;

/**
 * @author lc. 2019-03-25 20:28
 * @since 0.6.0
 */

public class RefreshFootView extends FrameLayout implements IBottomView {

    private AppCompatImageView mRefreshArrow;
    private TextView mRefreshTextView;

    private String mPullUpMessage = "";
    private String mReleaseLoadMoreMessage = "";
    private String mLoadingMoreMessage = "";

    public RefreshFootView(Context context) {
        this(context, null);
    }

    public RefreshFootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPullUpMessage = ResUtils.getString(getContext(), R.string.refresh_message_push_2_load_more);
        mReleaseLoadMoreMessage = ResUtils.getString(getContext(), R.string.refresh_message_release_2_load_more);
        mLoadingMoreMessage = ResUtils.getString(getContext(), R.string.refresh_message_loading_more);

        View rootView = View.inflate(getContext(), R.layout.layout_refresh_foot, null);
        mRefreshTextView = rootView.findViewById(R.id.tvRefreshText);
        mRefreshArrow = rootView.findViewById(R.id.ivArrow);
        addView(rootView);
    }


    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingUp(float fraction, float maxBottomHeight, float bottomHeight) {
        if (fraction < 1f) {
            mRefreshTextView.setText(mPullUpMessage);
        }
        if (fraction > 1f) {
            mRefreshTextView.setText(mReleaseLoadMoreMessage);
        }
        mRefreshArrow.setRotation(fraction * bottomHeight / maxBottomHeight * 180);
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            mRefreshTextView.setText(mPullUpMessage);
            mRefreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
            if (mRefreshArrow.getVisibility() == INVISIBLE) {
                mRefreshArrow.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        mRefreshTextView.setText(mLoadingMoreMessage);
        mRefreshArrow.setVisibility(VISIBLE);
        mRefreshArrow.setVisibility(INVISIBLE);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void reset() {
        mRefreshTextView.setText(mPullUpMessage);
        mRefreshArrow.setVisibility(VISIBLE);
    }


}

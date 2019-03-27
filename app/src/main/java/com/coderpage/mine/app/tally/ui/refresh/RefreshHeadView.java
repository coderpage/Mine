package com.coderpage.mine.app.tally.ui.refresh;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;

/**
 * @author lc. 2019-03-25 20:28
 * @since 0.6.0
 */
public class RefreshHeadView extends FrameLayout implements IHeaderView {

    private AppCompatImageView mRefreshArrow;
    private TextView mRefreshTextView;

    private String mPullDownMessage = "";
    private String mReleaseRefreshMessage = "";
    private String mRefreshingMessage = "";

    public RefreshHeadView(Context context) {
        this(context, null);
    }

    public RefreshHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPullDownMessage = ResUtils.getString(getContext(), R.string.refresh_message_pull_2_refresh);
        mReleaseRefreshMessage = ResUtils.getString(getContext(), R.string.refresh_message_release_2_refresh);
        mRefreshingMessage = ResUtils.getString(getContext(), R.string.refresh_message_refreshing);

        View rootView = View.inflate(getContext(), R.layout.layout_refresh_head, null);
        mRefreshTextView = rootView.findViewById(R.id.tvRefreshText);
        mRefreshArrow = rootView.findViewById(R.id.ivArrow);
        addView(rootView);
    }


    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            mRefreshTextView.setText(mPullDownMessage);
        }
        if (fraction > 1f) {
            mRefreshTextView.setText(mReleaseRefreshMessage);
        }
        mRefreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            mRefreshTextView.setText(mPullDownMessage);
            mRefreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
            if (mRefreshArrow.getVisibility() == INVISIBLE) {
                mRefreshArrow.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        mRefreshTextView.setText(mRefreshingMessage);
        mRefreshArrow.setVisibility(VISIBLE);
        mRefreshArrow.setVisibility(INVISIBLE);
    }

    @Override
    public void onFinish(OnAnimEndListener listener) {
        listener.onAnimEnd();
    }

    @Override
    public void reset() {
        mRefreshTextView.setText(mPullDownMessage);
        mRefreshArrow.setVisibility(VISIBLE);
    }
}

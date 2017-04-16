package com.coderpage.mine.app.tally.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import static com.coderpage.framework.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-04-15
 */

public class LoadMoreRecyclerView extends RecyclerView {
    private static final String TAG = makeLogTag(LoadMoreRecyclerView.class);

    private PullActionListener mPullActionListener;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new LoadMoreController());
    }

    public void setPullActionListener(PullActionListener listener) {
        mPullActionListener = listener;
    }

    private class LoadMoreController extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int firstCompletelyVisibleItemPosition = manager.findFirstCompletelyVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition == 0) {
                    if (mPullActionListener != null) {
                        mPullActionListener.onPullToRefresh();
                    }
                    return;
                }
                int lastCompletelyVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();
                if (lastCompletelyVisibleItemPosition == manager.getItemCount() - 1) {
                    if (mPullActionListener != null) {
                        mPullActionListener.onPullUpLoadMore();
                    }
                }
            }
        }
    }

    public static class PullActionListener {
        public void onPullToRefresh() {
        }

        public void onPullUpLoadMore() {
        }
    }
}

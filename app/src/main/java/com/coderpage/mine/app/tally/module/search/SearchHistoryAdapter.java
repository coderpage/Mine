package com.coderpage.mine.app.tally.module.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-09-22 23:41
 * @since 0.5.0
 */

class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.HistoryVH> {

    private static final int ITEM_TYPE_NORMAL = 1;
    private static final int ITEM_TYPE_BOTTOM = 2;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mItems = new ArrayList<>();

    private UpdatableView.UserActionListener<SearchModel.UserActions> mUserActionListener;

    SearchHistoryAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    void setUserActionListener(
            UpdatableView.UserActionListener<SearchModel.UserActions> mUserActionListener) {
        this.mUserActionListener = mUserActionListener;
    }

    void refresh(List<String> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size() > 0 ? mItems.size() + 1 : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mItems.size() ? ITEM_TYPE_BOTTOM : ITEM_TYPE_NORMAL;
    }

    @Override
    public HistoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            return new HistoryVH(
                    mInflater.inflate(R.layout.tally_recycler_item_search_history, parent, false));
        } else {
            return new HistoryVH(
                    mInflater.inflate(R.layout.layout_tally_search_history_clear, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(HistoryVH holder, int position) {
        if (position == mItems.size()) {
            holder.setType(ITEM_TYPE_BOTTOM);
        } else {
            holder.setType(ITEM_TYPE_NORMAL);
            holder.setKeyword(mItems.get(position));
        }
    }

    class HistoryVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mKeywordTv;


        private int type; // NORMAL or BOTTOM
        private String keyword;

        HistoryVH(View view) {
            super(view);
            mKeywordTv = (TextView) view.findViewById(R.id.tvKeyword);
            View removeIv = view.findViewById(R.id.ivRemove);

            view.setOnClickListener(this);
            if (mKeywordTv != null) {
                mKeywordTv.setOnClickListener(this);
            }
            if (removeIv != null) {
                removeIv.setOnClickListener(this);
            }
        }

        void setType(int type) {
            this.type = type;
        }

        void setKeyword(String keyword) {
            this.keyword = keyword;
            if (mKeywordTv != null) {
                mKeywordTv.setText(keyword);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvKeyword:
                    Bundle bundle = new Bundle(1);
                    bundle.putString(SearchModel.EXTRA_KEYWORD, keyword);
                    mUserActionListener.onUserAction(SearchModel.UserActions.SEARCH, bundle);
                    return;
                case R.id.ivRemove:
                    bundle = new Bundle(1);
                    bundle.putString(SearchModel.EXTRA_KEYWORD, keyword);
                    mUserActionListener.onUserAction(SearchModel.UserActions.SEARCH_HISTORY_REMOVE, bundle);
                    return;
            }

            if (this.type == ITEM_TYPE_BOTTOM) {
                mUserActionListener.onUserAction(SearchModel.UserActions.SEARCH_HISTORY_CLEAR, null);
            }
        }

    }
}

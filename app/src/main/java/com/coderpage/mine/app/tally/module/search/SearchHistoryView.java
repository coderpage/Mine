package com.coderpage.mine.app.tally.module.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.coderpage.base.common.IError;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;

import java.util.List;

import static com.coderpage.mine.app.tally.module.search.SearchModel.Queries;
import static com.coderpage.mine.app.tally.module.search.SearchModel.UserActions;

/**
 * @author lc. 2017-09-22 23:20
 * @since 0.5.0
 */

public class SearchHistoryView extends FrameLayout implements
        UpdatableView<SearchModel, Queries, UserActions, IError> {

    private RecyclerView mHistoryRecyclerView;
    private SearchHistoryAdapter mAdapter;

    private UserActionListener<UserActions> mUserActionListener;

    public SearchHistoryView(Context context) {
        this(context, null);
    }

    public SearchHistoryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchHistoryView(@NonNull Context context, @Nullable AttributeSet attrs,
                             @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(21)
    public SearchHistoryView(@NonNull Context context, @Nullable AttributeSet attrs,
                             @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.layout_tally_search_history_view, this);
        mHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerHistory);
        mHistoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SearchHistoryAdapter(context);
        mHistoryRecyclerView.setAdapter(mAdapter);
    }

    private void refreshView(List<String> history) {
        if (history.isEmpty()) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mAdapter.refresh(history);
        }
    }

    @Override
    public void displayData(SearchModel model, Queries query) {
        switch (query) {
            case LOAD_SEARCH_HISTORY:
                refreshView(model.getSearchHistory());
                break;
        }
    }

    @Override
    public void displayErrorMessage(Queries query, IError error) {

    }

    @Override
    public void displayUserActionResult(SearchModel model,
                                        Bundle args,
                                        UserActions userAction,
                                        boolean success,
                                        IError error) {
        switch (userAction) {
            case SEARCH:
                setVisibility(GONE);
                break;
            case SEARCH_HISTORY_ADD:
                if (success) {
                    refreshView(model.getSearchHistory());
                }
                break;
            case SEARCH_HISTORY_REMOVE:
                if (success) {
                    refreshView(model.getSearchHistory());
                }
                break;
            case SEARCH_HISTORY_CLEAR:
                if (success) {
                    refreshView(model.getSearchHistory());
                }
                break;
        }
    }

    @Override
    public Uri getDataUri(Queries query) {
        return null;
    }

    @Override
    public void addListener(UserActionListener<UserActions> listener) {
        mUserActionListener = listener;
        mAdapter.setUserActionListener(mUserActionListener);
    }
}

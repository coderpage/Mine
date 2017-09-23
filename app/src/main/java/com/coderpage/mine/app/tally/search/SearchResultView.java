package com.coderpage.mine.app.tally.search;

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
import android.widget.TextView;

import com.coderpage.base.common.IError;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.error.ErrorUtils;
import com.coderpage.mine.app.tally.data.Expense;

import java.util.List;

import static com.coderpage.mine.app.tally.search.SearchModel.Queries;
import static com.coderpage.mine.app.tally.search.SearchModel.UserActions;

/**
 * @author lc. 2017-09-23 00:16
 * @since 0.5.0
 */

public class SearchResultView extends FrameLayout implements
        UpdatableView<SearchModel, Queries, UserActions, IError> {

    private TextView mNoDataTipTv;
    private RecyclerView mResultRecycler;
    private SearchResultAdapter mSearchResultAdapter;

    private UserActionListener<UserActions> mUserActionListener;

    public SearchResultView(Context context) {
        this(context, null);
    }

    public SearchResultView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchResultView(@NonNull Context context, @Nullable AttributeSet attrs,
                            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(21)
    public SearchResultView(@NonNull Context context, @Nullable AttributeSet attrs,
                            @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.layout_tally_search_result_view, this);
        mNoDataTipTv = (TextView) findViewById(R.id.tvNoData);
        mResultRecycler = (RecyclerView) findViewById(R.id.recyclerResult);
        mResultRecycler.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mSearchResultAdapter = new SearchResultAdapter(context);
        mResultRecycler.setAdapter(mSearchResultAdapter);
    }

    private void refresh(List<Expense> expenseList) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        if (expenseList.isEmpty()) {
            // 显示搜索为空
            mNoDataTipTv.setVisibility(View.VISIBLE);
            mResultRecycler.setVisibility(INVISIBLE);
        } else {
            // 显示搜索结果
            mNoDataTipTv.setVisibility(View.INVISIBLE);
            mResultRecycler.setVisibility(VISIBLE);
            mSearchResultAdapter.refresh(expenseList);
        }
    }

    @Override
    public void displayData(SearchModel model, Queries query) {

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
                if (success) {
                    refresh(model.getResults());
                } else {
                    UIUtils.showToastShort(getContext(), ErrorUtils.formatDisplayMsg(error));
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
    }

}

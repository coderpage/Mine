package com.coderpage.mine.app.tally.module.search;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.coderpage.base.widget.LoadingLayout;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.records.RecordsAdapter;
import com.coderpage.mine.app.tally.ui.refresh.RefreshFootView;
import com.coderpage.mine.app.tally.ui.refresh.RefreshHeadView;
import com.coderpage.mine.module.search.SearchActivityBinding;
import com.coderpage.mine.ui.BaseActivity;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

public class SearchActivity extends BaseActivity {

    private EditText mSearchEt;
    private LoadingLayout mLoadingLayout;
    private TwinklingRefreshLayout mRefreshLayout;
    private RecyclerView mSearchResultRecycler;
    private RecyclerView mSearchHistoryRecycler;

    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RecordsAdapter mSearchResultAdapter;

    private SearchActivityBinding mBinding;
    private SearchViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        overridePendingTransition(0, 0);

        initView();
        subScribeUi();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private void initView() {
        setToolbarAsBack(v -> finish());
        // 搜索框
        mSearchEt = mBinding.etSearch;
        setupSearchView();

        mLoadingLayout = mBinding.loadingLayout;
        LoadingLayout.Config emptyConfig = mLoadingLayout.getConfig(LoadingLayout.STATUS_EMPTY);
        emptyConfig.setButtonPositiveText("");
        emptyConfig.setButtonNegativeText("");
        mLoadingLayout.setUserActionListener(new LoadingLayout.BaseUserActionListener() {
            @Override
            public void onPositiveButtonClick(LoadingLayout layout, View view) {
                if (layout.getStatus() == LoadingLayout.STATUS_ERROR) {
                    mViewModel.load();
                }
            }

            @Override
            public void onIconClick(LoadingLayout layout, View view) {
                if (layout.getStatus() == LoadingLayout.STATUS_EMPTY) {
                    mViewModel.load();
                }
            }
        });

        mRefreshLayout = mBinding.refreshLayout;
        mRefreshLayout.setHeaderView(new RefreshHeadView(this));
        mRefreshLayout.setBottomView(new RefreshFootView(this));
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                mViewModel.refresh();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                mViewModel.loadMore();
            }
        });

        mSearchHistoryRecycler = mBinding.recyclerHistory;
        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mViewModel);
        mSearchHistoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSearchHistoryRecycler.setAdapter(mSearchHistoryAdapter);

        mSearchResultRecycler = mBinding.recyclerResult;
        mSearchResultAdapter = new RecordsAdapter(this);
        mSearchResultRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSearchResultRecycler.setAdapter(mSearchResultAdapter);
    }

    private void setupSearchView() {
        mSearchEt.setFocusable(true);
        mSearchEt.setFocusableInTouchMode(true);
        mSearchEt.requestFocus();
        showSoftKeyBoard();

        mSearchEt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String keyword = mSearchEt.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    mViewModel.onSearchClick();
                }
                hideSoftKeyBoard();
            }
            return false;
        });
    }

    private void subScribeUi() {
        mBinding.setVm(mViewModel);
        mViewModel.getLoadingStatus().observe(this, loadingStatus -> {
            if (loadingStatus != null) {
                mLoadingLayout.setStatus(loadingStatus);
            }
        });
        mViewModel.getRefreshing().observe(this, refreshing -> {
            if (refreshing != null && !refreshing) {
                mRefreshLayout.finishRefreshing();
            }
        });
        mViewModel.getLoadingMore().observe(this, loadMore -> {
            if (loadMore != null && !loadMore) {
                mRefreshLayout.finishLoadmore();
            }
        });
        mViewModel.getSearchHistoryList().observe(this, historyList -> {
            if (historyList != null) {
                mSearchHistoryAdapter.refresh(historyList);
            }
        });
        mViewModel.getSearchResultList().observe(this, resultList -> {
            if (resultList != null) {
                mSearchResultAdapter.setDataList(resultList);
            }
        });
    }

    private void showSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.showSoftInputFromInputMethod(mSearchEt.getWindowToken(), 0);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
    }

//                String keyword = args.getString(SearchModel.EXTRA_KEYWORD, "");
//                mSearchEt.setText(keyword);
//                mSearchEt.setSelection(keyword.length());
//                hideSoftKeyBoard();
}

package com.coderpage.mine.app.tally.module.records;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coderpage.base.widget.LoadingLayout;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.router.TallyRouter;
import com.coderpage.mine.app.tally.ui.refresh.RefreshFootView;
import com.coderpage.mine.app.tally.ui.refresh.RefreshHeadView;
import com.coderpage.mine.module.records.RecordsActivityBinding;
import com.coderpage.mine.ui.BaseActivity;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

/**
 * @author lc. 2019-01-05 10:24
 * @since 0.6.0
 *
 * 记录页
 */
@Route(path = TallyRouter.RECORDS)
public class RecordsActivity extends BaseActivity {

    static final String EXTRA_QUERY = "extra_query";

    private RecordsActivityBinding mBinding;
    private RecordsViewModel mViewModel;

    private LoadingLayout mLoadingLayout;
    private TwinklingRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecordsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(self(), R.layout.tally_module_records_activity);
        mViewModel = ViewModelProviders.of(this).get(RecordsViewModel.class);
        getLifecycle().addObserver(mViewModel);
        initView();
        subscribeUi();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsClose(v -> finish());
    }

    /**
     * 打开记录页
     *
     * @param context context
     * @param query   记录的查询条件
     */
    public static void open(Context context, RecordQuery query) {
        Intent intent = new Intent(context, RecordsActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_QUERY, query);
        context.startActivity(intent);
    }

    private void initView() {
        mLoadingLayout = mBinding.loadingLayout;
        mRefreshLayout = mBinding.refreshLayout;
        mRecyclerView = mBinding.recyclerView;

        // 空数据页面点击事件处理
        mLoadingLayout.setUserActionListener(new LoadingLayout.BaseUserActionListener() {
            @Override
            public void onPositiveButtonClick(LoadingLayout layout, View view) {
                mViewModel.load();
            }

            @Override
            public void onIconClick(LoadingLayout layout, View view) {
                mViewModel.load();
            }
        });

        mRefreshLayout.setAutoLoadMore(true);
        mRefreshLayout.setHeaderView(new RefreshHeadView(this));
        mRefreshLayout.setHeaderHeight(120);
        mRefreshLayout.setBottomView(new RefreshFootView(this));
        mRefreshLayout.setBottomHeight(120);
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

        mAdapter = new RecordsAdapter(self());
        LinearLayoutManager layoutManager = new LinearLayoutManager(self(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void subscribeUi() {
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
        mViewModel.getLoadingMore().observe(this, loadingMore -> {
            if (loadingMore != null && !loadingMore) {
                mRefreshLayout.finishLoadmore();
            }
        });
        mViewModel.getRecordList().observe(this, recordList -> {
            if (recordList != null) {
                mAdapter.setDataList(recordList);
            }
        });
        mViewModel.getToolbarTitle().observe(this, subTitle -> {
            if (TextUtils.isEmpty(subTitle)) {
                return;
            }
            setToolbarTitle(subTitle);
        });
    }
}

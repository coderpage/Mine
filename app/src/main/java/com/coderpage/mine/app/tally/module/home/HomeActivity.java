package com.coderpage.mine.app.tally.module.home;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.records.RecordItemViewModel;
import com.coderpage.mine.ui.BaseActivity;

/**
 * @author lc. 2018-07-07 11:04
 * @since 0.6.0
 *
 * 记账本首页
 */

public class HomeActivity extends BaseActivity {

    private HomeViewModel mViewModel;
    private HomeActivityBinding mBinding;
    private HomeAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.module_home_activity_home);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        getLifecycle().addObserver(mViewModel);

        initView();
        subscribeUi();
    }

    private void initView() {
        mRefreshLayout = mBinding.refreshLayout;
        mRefreshLayout.setOnRefreshListener(() -> mViewModel.refresh());

        RecyclerView recyclerView = mBinding.recyclerView;
        mAdapter = new HomeAdapter(this, mViewModel, ViewModelProviders.of(this).get(RecordItemViewModel.class));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    private void subscribeUi() {
        mBinding.setActivity(this);
        mBinding.setVm(mViewModel);
        mViewModel.observableDataList().observe(this,
                dataList -> mAdapter.setDataList(dataList));
        mViewModel.observableRefreshing().observe(this,
                refreshing -> mRefreshLayout.setRefreshing(refreshing != null && refreshing));
    }
}

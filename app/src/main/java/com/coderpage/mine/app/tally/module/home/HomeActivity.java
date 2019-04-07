package com.coderpage.mine.app.tally.module.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.coderpage.base.cache.Cache;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.records.RecordItemViewModel;
import com.coderpage.mine.app.tally.module.search.SearchActivity;
import com.coderpage.mine.app.tally.ui.refresh.RefreshHeadView;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.recyclerview.ItemMarginDecoration;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

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
    private TwinklingRefreshLayout mRefreshLayout;

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
        mRefreshLayout.setHeaderView(new RefreshHeadView(this));
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                mViewModel.refresh();
            }
        });

        RecyclerView recyclerView = mBinding.recyclerView;
        mAdapter = new HomeAdapter(this, mViewModel, ViewModelProviders.of(this).get(RecordItemViewModel.class));

        ItemMarginDecoration itemMarginDecoration = new ItemMarginDecoration(0, 0, 0, 0);
        // 最后一个 ITEM 距离底部距离大一些，防止被底部按钮遮挡
        itemMarginDecoration.setLastItemOffset(0, 0, 0, UIUtils.dp2px(this, 80));
        recyclerView.addItemDecoration(itemMarginDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    private void subscribeUi() {
        mBinding.setActivity(this);
        mBinding.setVm(mViewModel);
        mViewModel.observableDataList().observe(this,
                dataList -> mAdapter.setDataList(dataList));
        mViewModel.observableRefreshing().observe(this, refreshing -> {
            if (refreshing == null || !refreshing) {
                mRefreshLayout.finishRefreshing();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tally_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyDatabaseFileToSdcard() {
        MineExecutors.ioExecutor().execute(() -> {
            File oldfile = self().getDatabasePath("sql_tally");
            try {
                int bytesum = 0;
                int byteread = 0;

                String newPath = Cache.getCacheFolder(self()).getAbsolutePath() + "/sql_tally.db";
                if (oldfile.exists()) {
                    InputStream inStream = new FileInputStream(oldfile);
                    FileOutputStream fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1444];
                    int length;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread;
                        System.out.println(bytesum);
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

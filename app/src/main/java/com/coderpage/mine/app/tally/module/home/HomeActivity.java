package com.coderpage.mine.app.tally.module.home;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.permission.PermissionReqHandler;
import com.coderpage.mine.app.tally.module.debug.DebugActivity;
import com.coderpage.mine.app.tally.module.records.RecordItemViewModel;
import com.coderpage.mine.app.tally.module.search.SearchActivity;
import com.coderpage.mine.app.tally.ui.dialog.PermissionReqDialog;
import com.coderpage.mine.app.tally.ui.refresh.RefreshHeadView;
import com.coderpage.mine.app.tally.update.UpdateUtils;
import com.coderpage.mine.app.tally.utils.SecurityUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.recyclerview.ItemMarginDecoration;
import com.coderpage.mine.utils.AndroidUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lc. 2018-07-07 11:04
 * @since 0.6.0
 *
 * 记账本首页
 */

public class HomeActivity extends BaseActivity {

    private PermissionReqHandler mPermissionReqHandler;
    private String[] mNeedPermissionArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    private HomeViewModel mViewModel;
    private com.coderpage.mine.app.tally.module.home.HomeActivityBinding mBinding;
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

        mPermissionReqHandler = new PermissionReqHandler(self());
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        UpdateUtils.checkPersistedNewVersionAndShowUpdateConfirmDialog(this);
        handlePermission();
    }

    private void handlePermission() {
        // 检查授权，请求权限
        String[] notGrantedPermissionArray = mPermissionReqHandler.getNotGrantedPermissionArray(self(), mNeedPermissionArray);
        if (notGrantedPermissionArray.length == 0) {
            return;
        }

        // 读存储权限 写存储权限，显示一条即可
        List<String> permissionList = new ArrayList<>(Arrays.asList(notGrantedPermissionArray));
        if (permissionList.contains(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionList.remove(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        new PermissionReqDialog(self(), permissionList)
                .setTitleText(ResUtils.getString(self(), R.string.permission_req_title_format, ResUtils.getString(self(), R.string.app_name)))
                .setPositiveText(ResUtils.getString(self(), R.string.permission_req_open))
                .setListener(new PermissionReqDialog.Listener() {
                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConfirmClick(DialogInterface dialog) {
                        dialog.dismiss();
                        // 请求权限
                        mPermissionReqHandler.requestPermission(true, notGrantedPermissionArray, new PermissionReqHandler.Listener() {
                            @Override
                            public void onGranted(boolean grantedAll, String[] permissionArray) {
                                // 全部授权 no-op
                            }

                            @Override
                            public void onDenied(String[] permissionArray) {
                                showPermissionNeedDialog(permissionArray);
                            }
                        });
                    }
                }).show();
    }

    private void showPermissionNeedDialog(String[] permissionArray) {
        // 读存储权限 写存储权限，显示一条即可
        List<String> permissionList = new ArrayList<>(Arrays.asList(permissionArray));
        if (permissionList.contains(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                && permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionList.remove(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        new PermissionReqDialog(self(), permissionList)
                .setTitleText(ResUtils.getString(self(), R.string.permission_req_title_format, ResUtils.getString(self(), R.string.app_name)))
                .setPositiveText(ResUtils.getString(self(), R.string.permission_req_go_open))
                .setListener(new PermissionReqDialog.Listener() {
                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConfirmClick(DialogInterface dialog) {
                        dialog.dismiss();
                        AndroidUtils.openAppSettingPage(self());
                    }
                })
                .show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionReqHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tally_main, menu);
        MenuItem debugMenu = menu.findItem(R.id.menu_debug);
        debugMenu.setVisible(BuildConfig.DEBUG);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_debug:
                startActivity(new Intent(this, DebugActivity.class));
                break;
            case R.id.menu_search:
                SecurityUtils.executeAfterFingerprintAuth(self(), ()->{
                    startActivity(new Intent(this, SearchActivity.class));
                });
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

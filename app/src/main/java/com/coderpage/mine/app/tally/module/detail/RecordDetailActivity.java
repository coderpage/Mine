package com.coderpage.mine.app.tally.module.detail;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.coderpage.mine.R;
import com.coderpage.mine.tally.module.detail.RecordDetailActivityBinding;
import com.coderpage.mine.ui.BaseActivity;

/**
 * @author lc. 2018-09-22 22:19
 * @since 0.6.0
 *
 * 记录详情页
 */

public class RecordDetailActivity extends BaseActivity {

    private RecordDetailActivityBinding mBinding;
    private RecordDetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.tally_module_detail_record_detail_activity);
        mViewModel = ViewModelProviders.of(this).get(RecordDetailViewModel.class);
        getLifecycle().addObserver(mViewModel);

        initView();
        subscribeUi();
    }

    /**
     * 打开支出记录详情
     *
     * @param context   context
     * @param expenseId 支出 ID
     */
    public static void openExpenseDetail(Context context, long expenseId) {
        Intent intent = new Intent(context, RecordDetailActivity.class);
        intent.putExtra(RecordDetailViewModel.EXTRA_RECORD_TYPE, RecordDetailViewModel.TYPE_EXPENSE);
        intent.putExtra(RecordDetailViewModel.EXTRA_RECORD_ID, expenseId);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 打开收入记录详情
     *
     * @param context  context
     * @param incomeId 收入 ID
     */
    public static void openIncomeDetail(Context context, long incomeId) {
        Intent intent = new Intent(context, RecordDetailActivity.class);
        intent.putExtra(RecordDetailViewModel.EXTRA_RECORD_TYPE, RecordDetailViewModel.TYPE_INCOME);
        intent.putExtra(RecordDetailViewModel.EXTRA_RECORD_ID, incomeId);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tally_record_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_trash:
                mViewModel.onDeleteClick(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        boolean handled = mViewModel.onBackPressed(this);
        if (!handled) {
            super.onBackPressed();
        }
    }

    private void initView() {
        setToolbarAsClose(v -> onBackPressed());
    }

    private void subscribeUi() {
        mBinding.setActivity(this);
        mBinding.setVm(mViewModel);
        mViewModel.getRecordData().observe(this, recordData -> {
            if (recordData != null) {
                mBinding.setData(recordData);
            }
        });
    }
}

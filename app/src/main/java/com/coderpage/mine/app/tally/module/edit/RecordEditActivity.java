package com.coderpage.mine.app.tally.module.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.widget.MTabLayout;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.RecordType;
import com.coderpage.mine.app.tally.module.edit.record.RecordEditFragment;
import com.coderpage.mine.tally.module.edit.RecordEditActivityBinding;
import com.coderpage.mine.ui.BaseActivity;

import java.util.Arrays;

/**
 * @author lc. 2018-08-29 19:26
 * @since 0.6.0
 *
 * 记账页面
 */

public class RecordEditActivity extends BaseActivity {

    private static final String EXTRA_MODE = "extra_mode";
    private static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    private static final String EXTRA_INCOME_ID = "extra_income_id";

    private static final int MODE_EXPENSE = 0;
    private static final int MODE_INCOME = 1;

    private ViewPager mViewPager;
    private RecordEditActivityBinding mBinding;
    private RecordViewPageAdapter mPageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.tally_module_edit_activity_record_edit);
        initView();
    }

    public static void openAsAddNewExpense(Context context) {
        Intent intent = new Intent(context, RecordEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_MODE, MODE_EXPENSE);
        context.startActivity(intent);
    }

    public static void openAsUpdateExpense(Context context, long expenseId) {
        Intent intent = new Intent(context, RecordEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_MODE, MODE_EXPENSE);
        intent.putExtra(EXTRA_EXPENSE_ID, expenseId);
        context.startActivity(intent);
    }

    public static void openAsAddNewIncome(Context context) {
        Intent intent = new Intent(context, RecordEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_MODE, MODE_INCOME);
        context.startActivity(intent);
    }

    public static void openAsUpdateIncome(Context context, long incomeId) {
        Intent intent = new Intent(context, RecordEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_MODE, MODE_INCOME);
        intent.putExtra(EXTRA_INCOME_ID, incomeId);
        context.startActivity(intent);
    }

    private void initView() {
        setToolbarAsClose(v -> finish());
        setTitle("");

        int mode = getIntent().getIntExtra(EXTRA_MODE, MODE_EXPENSE);
        long expenseId = getIntent().getLongExtra(EXTRA_EXPENSE_ID, -1);
        long incomeId = getIntent().getLongExtra(EXTRA_INCOME_ID, -1);

        RecordEditFragment expenseFragment = RecordEditFragment.instance(expenseId, RecordType.EXPENSE);
        RecordEditFragment incomeFragment = RecordEditFragment.instance(incomeId,RecordType.INCOME);

        mViewPager = mBinding.viewPager;
        mPageAdapter = new RecordViewPageAdapter(getSupportFragmentManager());
        mPageAdapter.setData(Arrays.asList(expenseFragment, incomeFragment),
                Arrays.asList(ResUtils.getString(this, R.string.tally_edit_title_expense),
                        ResUtils.getString(this, R.string.tally_edit_title_income)));
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(mode);

        MTabLayout tabLayout = mBinding.tabLayout;
        tabLayout.setupWithViewPager(mViewPager);
    }
}

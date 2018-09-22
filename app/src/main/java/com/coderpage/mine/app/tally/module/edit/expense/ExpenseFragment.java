package com.coderpage.mine.app.tally.module.edit.expense;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.ui.widget.NumInputView;
import com.coderpage.mine.tall.module.edit.ExpenseFragmentBinding;

/**
 * @author lc. 2018-08-29 19:30
 * @since 0.6.0
 *
 * 支出页面
 */

public class ExpenseFragment extends Fragment {

    private static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private ExpenseViewModel mViewModel;
    private ExpenseFragmentBinding mBinding;
    private ExpenseCategoryPageAdapter mCategoryPageAdapter;

    public static ExpenseFragment instance(long expenseId) {
        Bundle args = new Bundle(1);
        args.putLong(EXTRA_EXPENSE_ID, expenseId);

        ExpenseFragment fragment = new ExpenseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        long expenseId = arguments.getLong(EXTRA_EXPENSE_ID, -1);

        mViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        mViewModel.setExpenseId(expenseId);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.tally_module_edit_fragment_expense, container, false);

        initView();
        subscribeUi();
        return mBinding.getRoot();
    }

    private void initView() {
        NumInputView inputView = mBinding.numInputView;
        inputView.setInputListener(mViewModel.getInputListener());

        mCategoryPageAdapter = new ExpenseCategoryPageAdapter(getActivity(), mViewModel);
        mBinding.categoryViewpager.setAdapter(mCategoryPageAdapter);
    }

    private void subscribeUi() {
        mBinding.setActivity(getActivity());
        mBinding.setVm(mViewModel);
        mViewModel.getCategoryList().observe(this, categoryList -> {
            mCategoryPageAdapter.setCategoryList(categoryList);
        });
        mViewModel.getCurrentSelectCategory().observe(this, category -> {
            mBinding.setCategory(category);
        });
        mViewModel.getActivityRelayTask().observe(this, task -> {
            if (task != null) {
                task.execute(getActivity());
            }
        });
    }
}

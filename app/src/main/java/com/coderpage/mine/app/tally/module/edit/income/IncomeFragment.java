package com.coderpage.mine.app.tally.module.edit.income;

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
import com.coderpage.mine.tall.module.edit.IncomeFragmentBinding;

/**
 * @author lc. 2018-09-02 21:14
 * @since 0.6.0
 */

public class IncomeFragment extends Fragment {

    private static final String EXTRA_INCONE_ID = "extra_income_id";

    private IncomeViewModel mViewModel;
    private IncomeFragmentBinding mBinding;
    private IncomeCategoryPageAdapter mCategoryPageAdapter;

    public static IncomeFragment instance(long incomeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(EXTRA_INCONE_ID, incomeId);
        IncomeFragment fragment = new IncomeFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        long expenseId = arguments.getLong(EXTRA_INCONE_ID, -1);

        mViewModel = ViewModelProviders.of(this).get(IncomeViewModel.class);
        mViewModel.setIncomeId(expenseId);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.tally_module_edit_fragment_income, container, false);

        initView();
        subscribeUi();
        return mBinding.getRoot();
    }

    private void initView() {
        NumInputView inputView = mBinding.numInputView;
        inputView.setInputListener(mViewModel.getInputListener());

        mCategoryPageAdapter = new IncomeCategoryPageAdapter(getActivity(), mViewModel);
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

package com.coderpage.mine.app.tally.module.edit.record;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.RecordType;
import com.coderpage.mine.tall.module.edit.EditFragmentBinding;

/**
 * @author lc. 2018-08-29 19:30
 * @since 0.6.0
 *
 * 支出页面
 */

public class RecordEditFragment extends Fragment {

    static final String EXTRA_RECORD_ID = "extra_record_id";
    static final String EXTRA_RECORD_TYPE = "extra_record_type";

    private RecordType mRecordType;
    private RecordViewModel mViewModel;
    private EditFragmentBinding mBinding;
    private RecordCategoryPageAdapter mCategoryPageAdapter;

    public static RecordEditFragment instance(long recordId, RecordType type) {
        Bundle args = new Bundle(1);
        args.putLong(EXTRA_RECORD_ID, recordId);
        args.putSerializable(EXTRA_RECORD_TYPE, type);

        RecordEditFragment fragment = new RecordEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.tally_module_edit_fragment, container, false);
        getLifecycle().addObserver(mViewModel);

        mRecordType = (RecordType) getArguments().getSerializable(EXTRA_RECORD_TYPE);
        initView();
        subscribeUi();
        return mBinding.getRoot();
    }

    private void initView() {
        mCategoryPageAdapter = new RecordCategoryPageAdapter(getActivity(), mViewModel);
        mBinding.categoryViewpager.setAdapter(mCategoryPageAdapter);
        mBinding.focusView.setupWithViewPager(mBinding.categoryViewpager);
        // UIUtils.disableShowSoftInput(mBinding.etAmount);
    }

    private void subscribeUi() {
        mBinding.setType(mRecordType);
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

package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;

import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.module.records.RecordItemViewModel;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.tally.module.records.RecordItemBinding;

/**
 * @author lc. 2019-03-25 17:04
 * @since 0.6.0
 */

public class ViewHolderRecordItem extends BaseViewHolder {

    private Activity mActivity;
    private RecordItemBinding mBinding;
    private RecordItemViewModel mViewModel;

    ViewHolderRecordItem(Activity activity, RecordItemViewModel viewModel, RecordItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
        mActivity = activity;
        mViewModel = viewModel;
    }

    void bindData(HomeDisplayData data) {
        if (data != null && data.getInternal() != null && data.getInternal() instanceof Record) {
            Record record = (Record) data.getInternal();
            mBinding.setActivity(mActivity);
            mBinding.setData(record);
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }
}

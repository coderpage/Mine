package com.coderpage.mine.app.tally.module.records;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.coderpage.mine.MineApp;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.tally.module.records.RecordDateTitleItemBinding;
import com.coderpage.mine.tally.module.records.RecordItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-02-13 10:55
 * @since 0.6.0
 * 记录列表适配器
 */

public class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** ITEM 类型：记录 */
    private static final int ITEM_TYPE_RECORD = 1;
    /** ITEM 类型：日期标题 */
    private static final int ITEM_TYPE_DATE_TITLE = 2;

    private Activity mActivity;
    private LayoutInflater mInflater;
    private RecordItemViewModel mViewModel;

    private List<Object> mDataList = new ArrayList<>();

    public RecordsAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mViewModel = new RecordItemViewModel(MineApp.getAppContext());
    }

    public void setDataList(List<Object> list){
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mDataList.get(position);
        return item instanceof Record ? ITEM_TYPE_RECORD : ITEM_TYPE_DATE_TITLE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_RECORD) {
            return new RecordsViewHolder(DataBindingUtil.inflate(
                    mInflater, R.layout.tally_item_record_common, parent, false));
        }

        return new DateTitleViewHolder(DataBindingUtil.inflate(
                mInflater, R.layout.tally_item_record_date_title, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = mDataList.get(position);
        if (holder instanceof RecordsViewHolder && item instanceof Record) {
            ((RecordsViewHolder) holder).bind((Record) item);
        }
        if (holder instanceof DateTitleViewHolder && item instanceof RecordsDateTitle) {
            ((DateTitleViewHolder) holder).bind((RecordsDateTitle) item);
        }
    }

    /** 记录 ViewHolder */
    private class RecordsViewHolder extends RecyclerView.ViewHolder {

        private RecordItemBinding mBinding;

        RecordsViewHolder(RecordItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(Record record) {
            mBinding.setActivity(mActivity);
            mBinding.setData(record);
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }

    /** 日期标题 ViewHolder */
    private class DateTitleViewHolder extends RecyclerView.ViewHolder {

        private RecordDateTitleItemBinding mBinding;

        DateTitleViewHolder(RecordDateTitleItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(RecordsDateTitle title) {
            mBinding.setActivity(mActivity);
            mBinding.setData(title);
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }
}

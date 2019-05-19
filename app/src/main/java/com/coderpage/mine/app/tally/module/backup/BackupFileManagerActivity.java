package com.coderpage.mine.app.tally.module.backup;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.router.TallyRouter;
import com.coderpage.mine.module.backup.BackupFileManagerActivityBinding;
import com.coderpage.mine.module.backup.ItemBackupFileManagerBinding;
import com.coderpage.mine.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-05-19 17:10
 * @since 0.6.2
 *
 * 备份文件管理页面
 */

@Route(path = TallyRouter.BACKUP_FILE_MANAGER)
public class BackupFileManagerActivity extends BaseActivity {

    private BackupFileManagerActivityBinding mBinding;
    private BackupFileManagerViewModel mViewModel;

    private FileItemAadapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.tally_module_backup_activity_file_manager);
        mViewModel = ViewModelProviders.of(this).get(BackupFileManagerViewModel.class);
        getLifecycle().addObserver(mViewModel);

        initView();
        subscribeUi();
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, BackupFileManagerActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsClose(v -> finish());
    }

    private void initView() {
        RecyclerView recyclerView = mBinding.recyclerView;
        mAdapter = new FileItemAadapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(self(), LinearLayoutManager.VERTICAL, false));
    }

    private void subscribeUi() {
        mViewModel.getBackupFileList().observe(this, list -> {
            mAdapter.setDataList(list);
        });
        mViewModel.getViewReliedTask().observe(this, task -> {
            if (task != null) {
                task.execute(self());
            }
        });
    }

    private class FileItemAadapter extends RecyclerView.Adapter<FileItemViewHolder> {

        private List<BackupFileManagerItem> mDataList = new ArrayList<>();

        private void setDataList(List<BackupFileManagerItem> list) {
            if (list == null) {
                return;
            }
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @NonNull
        @Override
        public FileItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FileItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.tally_module_backup_item_manager_file, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FileItemViewHolder holder, int position) {
            holder.bind(mDataList.get(position));
        }
    }

    private class FileItemViewHolder extends RecyclerView.ViewHolder {

        private ItemBackupFileManagerBinding mBinding;

        FileItemViewHolder(ItemBackupFileManagerBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(BackupFileManagerItem item) {
            mBinding.setVm(mViewModel);
            mBinding.setItem(item);
            mBinding.executePendingBindings();
        }
    }
}

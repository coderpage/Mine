package com.coderpage.mine.app.tally.module.backup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.module.backup.ItemBackupFileSelectBinding;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author lc. 2019-06-22 14:04
 * @since 0.7.0
 */
public class BackupFileSelectDialog extends Dialog {

    private ItemViewModel mViewModel;
    private Listener mListener;

    BackupFileSelectDialog(Activity activity, List<File> fileList) {
        super(activity, R.style.Widget_Dialog_BottomSheet);
        mViewModel = new ItemViewModel(activity);
        initView(activity, fileList);
    }

    public BackupFileSelectDialog setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    public void initView(Activity activity, List<File> fileList) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.tally_dialog_backup_file_select, null);
        // 取消、从本地选择 点击
        contentView.findViewById(R.id.tvButtonCancel).setOnClickListener(v -> mListener.onCancelClick(this));
        contentView.findViewById(R.id.tvButtonSelectFromLocal).setOnClickListener(v -> mListener.onSelectFromLocalClick(this));

        // 初始化文件列表
        List<BackupFileItem> backupFileItemList = new ArrayList<>(fileList.size());
        ArrayUtils.forEach(fileList, (count, index, item) -> backupFileItemList.add(new BackupFileItem(item)));
        Collections.sort(backupFileItemList, (o1, o2) -> {
            if (o1.getCreateTime() == o2.getCreateTime()) {
                return 0;
            }
            return o1.getCreateTime() > o2.getCreateTime() ? -1 : 1;
        });

        RecyclerView recyclerViewFile = contentView.findViewById(R.id.recyclerFile);
        FileAdapter adapter = new FileAdapter(activity);
        adapter.setDataList(backupFileItemList);
        recyclerViewFile.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        recyclerViewFile.setAdapter(adapter);

        setContentView(contentView);

        // 计算 dialog 高度
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int dialogHeight = contentView.getMeasuredHeight();

        View fileItemView = LayoutInflater.from(activity).inflate(R.layout.tally_dialog_backup_file_select_item, null);
        fileItemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        dialogHeight = dialogHeight + fileItemView.getMeasuredHeight() * fileList.size();
        initWindow(dialogHeight);
    }

    private void initWindow(int height) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = window.getAttributes();

        attributes.width = window.getWindowManager().getDefaultDisplay().getWidth();
        attributes.height = Math.min(height, (int) (window.getWindowManager().getDefaultDisplay().getHeight() * 0.8));
        window.setAttributes(attributes);
    }

    private class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {

        private List<BackupFileItem> mDataList = new ArrayList<>();
        private Context mContext;

        FileAdapter(Context context) {
            mContext = context;
        }

        void setDataList(List<BackupFileItem> list) {
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
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemBackupFileSelectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.tally_dialog_backup_file_select_item, parent, false);
            return new FileViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            holder.bindData(mDataList.get(position));
        }
    }

    private class FileViewHolder extends RecyclerView.ViewHolder {
        private ItemBackupFileSelectBinding binding;

        FileViewHolder(ItemBackupFileSelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindData(BackupFileItem item) {
            binding.setItem(item);
            binding.setVm(mViewModel);
            binding.executePendingBindings();
        }
    }

    public class ItemViewModel {

        ItemViewModel(Context context) {
            mFileTimeDateFormat = new SimpleDateFormat(
                    ResUtils.getString(context, R.string.date_format_y_m_d_hh_mm), Locale.getDefault());
        }

        private SimpleDateFormat mFileTimeDateFormat;

        public void onItemClick(BackupFileItem item) {
            mListener.onFileSelect(BackupFileSelectDialog.this, item.getFile());
        }

        /** 格式化文件时间 */
        public synchronized String formatBackupTime(BackupFileItem item) {
            if (item == null || item.getFile() == null) {
                return "";
            }
            return mFileTimeDateFormat.format(new Date(item.getCreateTime()));
        }

        /** 格式化文件大小 */
        public String formatBackupFileSize(BackupFileItem item) {
            if (item == null || item.getFile() == null) {
                return "0 KB";
            }

            long size = item.getSize();
            double sizeKB = size / 1024D;
            double sizeM = sizeKB / 1024D;
            if (sizeM > 1) {
                return new DecimalFormat("0.00M").format(sizeM);
            }
            if (sizeKB > 1) {
                return new DecimalFormat("0.00KB").format(sizeKB);
            }
            return size + "B";
        }
    }

    public interface Listener {
        /**
         * 取消点击
         *
         * @param dialog dialog
         */
        void onCancelClick(BackupFileSelectDialog dialog);

        /**
         * 从本地选取点击
         *
         * @param dialog dialog
         */
        void onSelectFromLocalClick(BackupFileSelectDialog dialog);

        /**
         * 备份文件选中点击
         *
         * @param dialog dialog
         * @param file   选中的文件
         */
        void onFileSelect(BackupFileSelectDialog dialog, File file);
    }
}

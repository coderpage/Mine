package com.coderpage.mine.app.tally.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.coderpage.mine.R;
import com.coderpage.mine.dialog.TextEditDialogBinding;

/**
 * @author lc. 2019-04-14 17:38
 * @since 0.6.0
 *
 * 文字编辑弹框
 */

public class TextEditDialog extends Dialog {

    private Listener mListener;
    private TextEditDialogBinding mBinding;

    public TextEditDialog(Activity activity) {
        super(activity, R.style.Widget_Dialog_BottomSheet);
        initView(activity);
    }

    private void initView(Activity activity) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity),
                R.layout.tally_dialog_text_edit, null, false);

        // 输入框获取焦点时，弹出软键盘
        mBinding.etContent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && getWindow() != null) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        // 取消
        mBinding.tvCancel.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onNegativeClick(this);
            }
        });
        mBinding.tvConfirm.setOnClickListener(v -> {
            String content = mBinding.etContent.getText().toString();
            // 回调
            if (mListener != null) {
                mListener.onPositiveClick(this, content);
            }
        });

        View contentView = mBinding.getRoot();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setContentView(contentView);
        initWindow(contentView.getMeasuredHeight());
    }

    private void initWindow(int height) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = window.getWindowManager().getDefaultDisplay().getWidth();
        attributes.height = height;
        window.setAttributes(attributes);
    }

    @Override
    public void show() {
        super.show();
        mBinding.etContent.setFocusable(true);
        mBinding.etContent.requestFocus();
    }

    public TextEditDialog setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    public TextEditDialog setTitle(String title) {
        mBinding.setTitle(title);
        return this;
    }

    public TextEditDialog setContent(String content) {
        mBinding.setContent(content);
        mBinding.executePendingBindings();
        mBinding.etContent.setSelection(mBinding.etContent.getText().length());
        return this;
    }

    public TextEditDialog setHint(String hint) {
        mBinding.setHint(hint);
        return this;
    }

    public interface Listener {
        /**
         * 确定按钮回调
         *
         * @param dialog dialog
         * @param text   输入内容
         */
        void onPositiveClick(DialogInterface dialog, String text);

        /**
         * 取消按钮回调
         *
         * @param dialog dialog
         */
        void onNegativeClick(DialogInterface dialog);
    }
}

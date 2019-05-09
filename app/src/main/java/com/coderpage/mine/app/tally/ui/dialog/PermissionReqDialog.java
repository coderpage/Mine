package com.coderpage.mine.app.tally.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.permission.PermissionUtils;

import java.util.List;

/**
 * @author lc. 2019-05-09 19:06
 * @since 0.6.2
 */
public class PermissionReqDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mCancelButton;
    private TextView mConfirmButton;

    private Listener mListener;

    public PermissionReqDialog(Activity activity, List<String> permissionArray) {
        super(activity, R.style.Widget_Dialog);
        initView(activity, permissionArray);
    }

    public PermissionReqDialog setTitleText(@Nullable CharSequence title) {
        mTitleTv.setText(title);
        return this;
    }

    public PermissionReqDialog setPositiveText(@Nullable CharSequence buttonText) {
        mConfirmButton.setText(buttonText);
        return this;
    }

    public PermissionReqDialog setNegativeText(@Nullable CharSequence buttonText) {
        mCancelButton.setText(buttonText);
        return this;
    }

    public PermissionReqDialog setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    private void initView(Activity activity, List<String> permissionArray) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_permission_req, null, false);
        mTitleTv = contentView.findViewById(R.id.tvTitle);
        mCancelButton = contentView.findViewById(R.id.tvCancel);
        mConfirmButton = contentView.findViewById(R.id.tvConfirm);

        LinearLayout lyPermissions = contentView.findViewById(R.id.lyPermissions);
        ArrayUtils.forEach(permissionArray, (count, index, item) -> {
            View itemView = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_permission_req_item, null, false);
            TextView nameTv = itemView.findViewById(R.id.tvPermissionName);
            TextView descTv = itemView.findViewById(R.id.tvPermissionDesc);
            nameTv.setText(PermissionUtils.getPermissionName(activity, item));
            descTv.setText(PermissionUtils.getPermissionDesc(activity, item));
            lyPermissions.addView(itemView);
        });

        mConfirmButton.setOnClickListener(v -> mListener.onConfirmClick(this));
        mCancelButton.setOnClickListener(v -> mListener.onCancelClick(this));

        setContentView(contentView);
        initWindow();
    }

    private void initWindow() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = (int) (window.getWindowManager().getDefaultDisplay().getWidth() * 0.8f);
        attributes.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
    }

    public interface Listener {
        /**
         * 取消点击
         *
         * @param dialog dialog
         */
        void onCancelClick(DialogInterface dialog);

        /**
         * 确定点击
         *
         * @param dialog dialog
         */
        void onConfirmClick(DialogInterface dialog);
    }
}

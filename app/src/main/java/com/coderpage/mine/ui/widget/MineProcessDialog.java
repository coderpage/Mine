package com.coderpage.mine.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;

/**
 * @author lc. 2019-04-27 10:08
 * @since 0.6.2
 *
 * 加载弹框
 */

public class MineProcessDialog extends Dialog {

    private TextView mMessageTv;

    private MineProcessDialog(@NonNull Context context) {
        super(context, R.style.Widget_Dialog_Process);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_process, null);
        mMessageTv = view.findViewById(R.id.tvText);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int viewHeight = view.getMeasuredHeight();

        setContentView(view);

        // 设置大小，位置
        Window window = getWindow();
        if (window == null) {
            return;
        }
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int dialogWidth = display.widthPixels / 2;
        // 多余的 30DP 用于提示文字换行
        int dialogHeight = viewHeight + UIUtils.dp2px(context, 30);
        window.setLayout(dialogWidth, dialogHeight);
        window.setGravity(Gravity.CENTER);
    }

    public void setMessage(String message) {
        mMessageTv.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(message)) {
            mMessageTv.setText(message);
        }
    }

    public static class Builder {
        private Context context;
        private String message;
        private boolean isCancelable = false;
        private boolean isCancelOutside = false;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置提示信息
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置是否可以按返回键取消
         */

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        /**
         * 设置是否可以取消
         */
        public Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public MineProcessDialog create() {
            MineProcessDialog loadingDialog = new MineProcessDialog(context);
            loadingDialog.setCancelable(isCancelable);
            loadingDialog.setCanceledOnTouchOutside(isCancelOutside);
            loadingDialog.setMessage(message);
            return loadingDialog;
        }
    }
}

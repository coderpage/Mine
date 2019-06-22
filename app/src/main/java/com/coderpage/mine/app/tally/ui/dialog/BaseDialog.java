package com.coderpage.mine.app.tally.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author lc. 2019-06-22 14:05
 * @since 0.7.0
 */
public abstract class BaseDialog extends Dialog {

    public BaseDialog(Activity activity, @StyleRes int styleRes) {
        super(activity, styleRes);
        init(activity);
    }

    protected void init(Activity activity) {
        View contentView = initView(activity);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setContentView(contentView);
        initWindow(contentView.getMeasuredHeight());
    }

    private void initWindow(int height) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setGravity(getGravity());

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = window.getWindowManager().getDefaultDisplay().getWidth();
        attributes.height = height;
        window.setAttributes(attributes);
    }

    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    /**
     * 初始化 View
     *
     * @param activity activity
     * @return contentView
     */
    public abstract View initView(Activity activity);
}

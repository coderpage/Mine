package com.coderpage.mine.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coderpage.mine.R;


/**
 * @author abner-l. 2017-01-10
 */

public class ButtonGroupDialog extends Dialog {

    private LinearLayout mViewGroup;

    public ButtonGroupDialog(@NonNull Context context) {
        this(context, R.style.Widget_Dialog);
    }

    public ButtonGroupDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.widget_btn_group_dialog);
        mViewGroup = (LinearLayout) findViewById(R.id.lyGroupContainer);
    }

    public void addItem(int txtRes, OnItemClickListener onClickListener) {
        String txt = getContext().getString(txtRes);
        addItem(txt, onClickListener);
    }

    public void addItem(CharSequence txt, OnItemClickListener onClickListener) {
        if (onClickListener != null) onClickListener.setDialog(this);
        View view = getLayoutInflater().inflate(R.layout.widget_btn_group_dialog_item, null);
        view.setOnClickListener(onClickListener);
        ((TextView) view.findViewById(R.id.tvBtnText)).setText(txt);
        mViewGroup.addView(view);
    }

    @Override
    public void show() {
        int childCount = mViewGroup.getChildCount();
        if (childCount > 0) {
            View divider = mViewGroup.getChildAt(childCount - 1).findViewById(R.id.viewDivider);
            divider.setVisibility(View.INVISIBLE);
        }
        super.show();
    }

    public static abstract class OnItemClickListener implements View.OnClickListener {
        private DialogInterface mDialog;

        public abstract void onClick(DialogInterface dialog, View v);

        @Override
        public void onClick(View v) {
            onClick(mDialog, v);
        }

        private void setDialog(DialogInterface dialog) {
            mDialog = dialog;
        }
    }
}

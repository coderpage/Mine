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
import android.widget.Toast;

import com.coderpage.base.utils.CommonUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.utils.TallyUtils;
import com.coderpage.mine.app.tally.databinding.CommonBindAdapter;
import com.coderpage.mine.app.tally.persistence.preference.SettingPreference;
import com.coderpage.mine.common.Font;
import com.coderpage.mine.dialog.BudgetMonthSetDialogBinding;

/**
 * @author lc. 2019-04-07 07:49
 * @since 0.6.0
 *
 * 设置月预算弹框
 */

public class SetBudgetMonthDialog extends Dialog {

    private Listener mListener;
    private BudgetMonthSetDialogBinding mBinding;

    public SetBudgetMonthDialog(Activity activity) {
        super(activity, R.style.Widget_Dialog_BottomSheet);
        initView(activity);
    }

    private void initView(Activity activity) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity),
                R.layout.tally_dialog_set_budget_month, null, false);
        CommonBindAdapter.setTypeFace(mBinding.tvBudgetUnit, Font.QUICKSAND_REGULAR);
        CommonBindAdapter.setTypeFace(mBinding.etBudget, Font.QUICKSAND_MEDIUM);

        // 显示设置的预算
        float budgetMonth = SettingPreference.getBudgetMonth(activity);
        if (budgetMonth > 0) {
            mBinding.etBudget.setText(TallyUtils.formatDisplayMoney(budgetMonth));
            mBinding.etBudget.setSelection(mBinding.etBudget.getText().toString().length());
        }

        mBinding.tvBudgetUnit.setText("¥");
        // 输入框获取焦点时，弹出软键盘
        mBinding.etBudget.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && getWindow() != null) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        mBinding.ivClose.setOnClickListener(v -> dismiss());
        mBinding.tvConfirm.setOnClickListener(v -> {
            String budgetStr = mBinding.etBudget.getText().toString();
            float budget = CommonUtils.string2float(budgetStr, -1);
            // 输入预算 <= 0 提示错误信息
            if (budget <= 0) {
                Toast.makeText(activity, R.string.dialog_budget_error_illegal_input, Toast.LENGTH_SHORT).show();
                return;
            }
            // 缓存预算到本地
            SettingPreference.setBudgetMonth(activity, budget);
            // 回调
            if (mListener != null) {
                mListener.onBudgetUpdate(this, budget);
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
        mBinding.etBudget.setFocusable(true);
        mBinding.etBudget.requestFocus();
    }

    public SetBudgetMonthDialog setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    public interface Listener {
        /**
         * 预算更新回调
         *
         * @param dialog dialog
         * @param budget 预算金额
         */
        void onBudgetUpdate(DialogInterface dialog, float budget);
    }
}

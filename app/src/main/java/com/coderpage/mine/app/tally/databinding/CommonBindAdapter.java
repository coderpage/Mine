package com.coderpage.mine.app.tally.databinding;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderpage.mine.app.tally.common.utils.TallyUtils;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;

import java.text.DecimalFormat;

/**
 * @author lc. 2018-07-21 12:15
 * @since 0.6.0
 */

public class CommonBindAdapter {

    private static final DecimalFormat MONEY_DECIMAL_FORMAT = new DecimalFormat("0.00");

    /** 设置分类 ICON */
    @BindingAdapter(value = {"categoryIcon"}, requireAll = false)
    public static void setCategoryIcon(ImageView imageView, String categoryIconName) {
        int resId = CategoryIconHelper.resId(categoryIconName);
        imageView.setImageResource(resId);
    }

    /**
     * 显示金额
     */
    @BindingAdapter(value = {"selected"}, requireAll = false)
    public static void setViewSelect(View view, boolean selected) {
        if (view != null) {
            view.setSelected(selected);
        }
    }

    /**
     * 显示金额
     */
    @BindingAdapter(value = {"textMoney", "format"}, requireAll = false)
    public static void setMoneyText(TextView textView, Object money, String format) {
        if (money == null) {
            textView.setText("");
            return;
        }

        if (TextUtils.isEmpty(format)) {
            textView.setText(MONEY_DECIMAL_FORMAT.format(money));
        } else {
            textView.setText(String.format(format, MONEY_DECIMAL_FORMAT.format(money)));
        }
    }

    /**
     * 显示时间
     *
     * @param timeMills unix 时间戳（单位：毫秒）
     */
    @BindingAdapter(value = {"textDisplayTime"}, requireAll = false)
    public static void setDisplayTimeText(TextView textView, long timeMills) {
        textView.setText(TallyUtils.formatDisplayTime(timeMills));
    }
}

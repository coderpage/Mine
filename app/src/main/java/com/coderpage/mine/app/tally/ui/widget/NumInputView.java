package com.coderpage.mine.app.tally.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-03-11
 */

public class NumInputView extends LinearLayout {

    private String mExpression = "";
    private InputListener mListener;

    public NumInputView(Context context) {
        this(context, null);
    }

    public NumInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.layout_tally_input, this);

        findViewById(R.id.tvNum0).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum1).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum2).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum3).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum4).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum5).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum6).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum7).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum8).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvNum9).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvClear).setOnClickListener(mOnclickListener);
        findViewById(R.id.ivDelete).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvDot).setOnClickListener(mOnclickListener);
        findViewById(R.id.tvOk).setOnClickListener(mOnclickListener);
    }

    public void setInputListener(InputListener listener) {
        mListener = listener;
    }

    private float calculateResult() {
        if (TextUtils.isEmpty(mExpression)) {
            return 0.0F;
        }
        try {
            return Float.parseFloat(mExpression);
        } catch (NumberFormatException e) {
            return 0.0F;
        }
    }

    private OnClickListener mOnclickListener = (v) -> {
        int id = v.getId();
        switch (id) {
            case R.id.tvNum0:
                mExpression += "0";
                callback(KeyEvent.KEYCODE_0, calculateResult());
                break;
            case R.id.tvNum1:
                mExpression += "1";
                callback(KeyEvent.KEYCODE_1, calculateResult());
                break;
            case R.id.tvNum2:
                mExpression += "2";
                callback(KeyEvent.KEYCODE_2, calculateResult());
                break;
            case R.id.tvNum3:
                mExpression += "3";
                callback(KeyEvent.KEYCODE_3, calculateResult());
                break;
            case R.id.tvNum4:
                mExpression += "4";
                callback(KeyEvent.KEYCODE_4, calculateResult());
                break;
            case R.id.tvNum5:
                mExpression += "5";
                callback(KeyEvent.KEYCODE_5, calculateResult());
                break;
            case R.id.tvNum6:
                mExpression += "6";
                callback(KeyEvent.KEYCODE_6, calculateResult());
                break;
            case R.id.tvNum7:
                mExpression += "7";
                callback(KeyEvent.KEYCODE_7, calculateResult());
                break;
            case R.id.tvNum8:
                mExpression += "8";
                callback(KeyEvent.KEYCODE_8, calculateResult());
                break;
            case R.id.tvNum9:
                mExpression += "9";
                callback(KeyEvent.KEYCODE_9, calculateResult());
                break;
            case R.id.tvClear:
                mExpression = "";
                callback(KeyEvent.KEYCODE_CLEAR, calculateResult());
                break;
            case R.id.ivDelete:
                // no nothing for now
                if (!TextUtils.isEmpty(mExpression)) {
                    mExpression = mExpression.substring(0, mExpression.length() - 1);
                    callback(KeyEvent.KEYCODE_DEL, calculateResult());
                }
                break;
            case R.id.tvDot:
                if (!mExpression.contains(".")) {
                    mExpression += ".";
                    callback(KeyEvent.KEYCODE_NUMPAD_DOT, calculateResult());
                }
                break;
            case R.id.tvOk:
                callback(KeyEvent.KEYCODE_ENTER, calculateResult());
                break;
        }
    };

    private void callback(int code, float newNumber) {
        if (mListener == null) {
            return;
        }
        mListener.onKeyClick(code);
        if (code != KeyEvent.KEYCODE_ENTER) {
            mListener.onNumChange(newNumber);
        }
    }

    public interface InputListener {

        void onKeyClick(int code);

        void onNumChange(float newNum);
    }
}

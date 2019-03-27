package com.coderpage.base.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coderpage.base.R;

/**
 * @author lc. 2019-01-05 10:24
 * @since 0.6.0
 */

public class LoadingLayout extends FrameLayout {

    /** 加载状态: 加载成功 */
    public static final int STATUS_SUCCESS = 0;
    /** 加载状态: 正在加载中... */
    public static final int STATUS_LOADING = 1;
    /** 加载状态: 数据为空 */
    public static final int STATUS_EMPTY = 2;
    /** 加载状态: 加载失败 */
    public static final int STATUS_ERROR = 3;

    /** 全局配置 */
    private static final SparseArray<Config> GLOBAL_CONFIG = new SparseArray<>();

    /** 当前加载状态 */
    private int mCurrentStatus = STATUS_EMPTY;
    /** 当前{@link LoadingLayout}配置 */
    private SparseArray<Config> mConfig = new SparseArray<>();

    /** 用户事件监听 */
    private BaseUserActionListener mUserActionListener;

    private View mContainerView;
    private ProgressBar mProgressBar;
    private View mLoadResultContainerLy;
    private AppCompatImageView mStatusIconIv;
    private TextView mStatusMessageTv;
    private TextView mButtonNegative;
    private TextView mButtonPositive;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 返回全局的配置信息
     *
     * @return 全局配置信息
     */
    public static SparseArray<Config> getGlobalConfig() {
        return GLOBAL_CONFIG;
    }

    /**
     * 返回当前{@link LoadingLayout}配置信息
     *
     * @param statusCode 状态码
     */
    public Config getConfig(int statusCode) {
        Config config = mConfig.get(statusCode);
        if (config == null) {
            config = new Config();
            mConfig.append(statusCode, config);
        }
        return config;
    }

    /**
     * 设置用户事件监听
     */
    public void setUserActionListener(BaseUserActionListener listener) {
        this.mUserActionListener = listener;
    }

    /**
     * 返回当前状态
     */
    public int getStatus() {
        return mCurrentStatus;
    }

    /**
     * 设置当前状态
     */
    public void setStatus(int status) {
        mCurrentStatus = status;
        update();
    }

    /** 刷新加载状态 UI */
    public void update() {
        switch (mCurrentStatus) {
            // 加载成功
            case STATUS_SUCCESS:
                mLoadResultContainerLy.setVisibility(GONE);
                mProgressBar.setVisibility(GONE);
                return;
            // 加载中...
            case STATUS_LOADING:
                mLoadResultContainerLy.setVisibility(GONE);
                mProgressBar.setVisibility(VISIBLE);
                return;
            default:
                mLoadResultContainerLy.setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
                break;
        }

        Config config = getConfig(mCurrentStatus);

        // 设置状态图标
        if (config.iconRes != 0) {
            mStatusIconIv.setImageResource(config.iconRes);
        }
        // 设置 negative button text style
        if (config.buttonNegativeTextStyle != 0) {
            mButtonNegative.setTextAppearance(getContext(), config.buttonNegativeTextStyle);
        }
        // 设置 positive button text style
        if (config.buttonPositiveTextStyle != 0) {
            mButtonPositive.setTextAppearance(getContext(), config.buttonPositiveTextStyle);
        }

        // 设置 negative button background
        if (config.buttonNegativeBackgroundRes != 0) {
            mButtonNegative.setBackgroundResource(config.buttonNegativeBackgroundRes);
        }
        // 设置 positive button background
        if (config.buttonPositiveBackgroundRes != 0) {
            mButtonPositive.setBackgroundResource(config.buttonPositiveBackgroundRes);
        }
        // 设置 message text style
        if (config.messageTextStyle != 0) {
            mStatusMessageTv.setTextAppearance(getContext(), config.messageTextStyle);
        }

        mStatusMessageTv.setVisibility(TextUtils.isEmpty(config.message) ? View.GONE : View.VISIBLE);
        mStatusMessageTv.setText(config.message);
        mButtonNegative.setVisibility(TextUtils.isEmpty(config.buttonNegativeText) ? View.GONE : View.VISIBLE);
        mButtonNegative.setText(config.buttonNegativeText);
        mButtonPositive.setVisibility(TextUtils.isEmpty(config.buttonPositiveText) ? View.GONE : View.VISIBLE);
        mButtonPositive.setText(config.buttonPositiveText);
    }

    private void init(Context context) {
        mConfig.append(STATUS_SUCCESS, GLOBAL_CONFIG.get(STATUS_SUCCESS, new Config()).copy());
        mConfig.append(STATUS_LOADING, GLOBAL_CONFIG.get(STATUS_LOADING, new Config()).copy());
        mConfig.append(STATUS_EMPTY, GLOBAL_CONFIG.get(STATUS_EMPTY, new Config()).copy());
        mConfig.append(STATUS_ERROR, GLOBAL_CONFIG.get(STATUS_ERROR, new Config()).copy());

        mContainerView = LayoutInflater.from(context).inflate(R.layout.base_layout_loading, null, false);
        mProgressBar = mContainerView.findViewById(R.id.progress_bar);
        mLoadResultContainerLy = mContainerView.findViewById(R.id.ly_status_container);
        mStatusIconIv = mContainerView.findViewById(R.id.iv_status_icon);
        mStatusMessageTv = mContainerView.findViewById(R.id.tv_status_message);
        mButtonNegative = mContainerView.findViewById(R.id.tv_button_negative);
        mButtonPositive = mContainerView.findViewById(R.id.tv_button_positive);

        mButtonNegative.setOnClickListener(v -> {
            if (mUserActionListener != null) {
                mUserActionListener.onNegativeButtonClick(LoadingLayout.this, v);
            }
        });
        mButtonPositive.setOnClickListener(v -> {
            if (mUserActionListener != null) {
                mUserActionListener.onPositiveButtonClick(LoadingLayout.this, v);
            }
        });
        mStatusIconIv.setOnClickListener(v -> {
            if (mUserActionListener != null) {
                mUserActionListener.onIconClick(LoadingLayout.this, v);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(mContainerView);
    }

    /**
     * 用户事件监听
     */
    public abstract static class BaseUserActionListener {

        /**
         * callback when positive button click
         *
         * @param layout {@link LoadingLayout}
         * @param view   view
         */
        public void onPositiveButtonClick(LoadingLayout layout, View view) {
            // no-op
        }

        /**
         * callback when negative button click
         *
         * @param layout {@link LoadingLayout}
         * @param view   view
         */
        public void onNegativeButtonClick(LoadingLayout layout, View view) {
            // no-op
        }

        /**
         * callback when icon click
         *
         * @param layout {@link LoadingLayout}
         * @param view   view
         */
        public void onIconClick(LoadingLayout layout, View view) {
            // no-op
        }
    }

    public static class Config {

        private int iconRes;
        private int buttonPositiveBackgroundRes;
        private int buttonNegativeBackgroundRes;
        private int buttonPositiveTextStyle;
        private int buttonNegativeTextStyle;
        private int messageTextStyle;
        private String message;
        private String buttonPositiveText;
        private String buttonNegativeText;

        public Config() {

        }

        public Config copy() {
            Config config = new Config();
            config.iconRes = this.iconRes;
            config.buttonPositiveBackgroundRes = this.buttonPositiveBackgroundRes;
            config.buttonNegativeBackgroundRes = this.buttonNegativeBackgroundRes;
            config.buttonPositiveTextStyle = this.buttonPositiveTextStyle;
            config.buttonNegativeTextStyle = this.buttonNegativeTextStyle;
            config.messageTextStyle = this.messageTextStyle;
            config.message = this.message;
            config.buttonPositiveText = this.buttonPositiveText;
            config.buttonNegativeText = this.buttonNegativeText;
            return config;
        }

        public Config setIconRes(int iconRes) {
            this.iconRes = iconRes;
            return this;
        }

        public Config setButtonPositiveBackgroundRes(@DrawableRes int buttonPositiveBackgroundRes) {
            this.buttonPositiveBackgroundRes = buttonPositiveBackgroundRes;
            return this;
        }

        public Config setButtonNegativeBackgroundRes(@DrawableRes int buttonNegativeBackgroundRes) {
            this.buttonNegativeBackgroundRes = buttonNegativeBackgroundRes;
            return this;
        }

        public Config setButtonPositiveTextStyle(@StyleRes int buttonPositiveTextStyle) {
            this.buttonPositiveTextStyle = buttonPositiveTextStyle;
            return this;
        }

        public Config setButtonNegativeTextStyle(@StyleRes int buttonNegativeTextStyle) {
            this.buttonNegativeTextStyle = buttonNegativeTextStyle;
            return this;
        }

        public Config setMessageTextStyle(int messageTextStyle) {
            this.messageTextStyle = messageTextStyle;
            return this;
        }

        public Config setButtonPositiveText(String buttonPositiveText) {
            this.buttonPositiveText = buttonPositiveText;
            return this;
        }

        public Config setButtonNegativeText(String buttonNegativeText) {
            this.buttonNegativeText = buttonNegativeText;
            return this;
        }


        public Config setMessage(String message) {
            this.message = message;
            return this;
        }
    }
}

package com.coderpage.mine.ui;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.utils.StatusBarUtils;
import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-01-22
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(statusBarColor());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    protected void setToolbarAsBack(View.OnClickListener clickListener) {
        getToolbar();

        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(clickListener);
    }

    protected void setToolbarAsClose(View.OnClickListener clickListener) {
        getToolbar();

        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mToolbar.setNavigationOnClickListener(clickListener);
    }


    private void setStatusBarColor(@ColorRes int resId) {
        StatusBarUtils.setStatusBarColor(this, resId);

        // 自动根据状态栏颜色的深浅来设置状态栏颜色
        int color = ResUtils.getColor(this, resId);
        float a = color >>> 24;
        float r = (color & 0xff0000) >> 16;
        float g = (color & 0xff00) >> 8;
        float b = color & 0xff;

        // 状态栏颜色较浅，使用黑色的状态栏文字
        boolean lightColor = r * 0.299 + g * 0.578 + b * 0.114 >= 192;
        setStatusBarLightMode(lightColor);
    }

    /**
     * 设置状态栏模式。
     *
     * @param lightMode true-亮色模式，状态栏文字显示为黑色 false-暗色模式，状态栏文字颜色为白色
     */
    protected void setStatusBarLightMode(boolean lightMode) {
        if (lightMode) {
            StatusBarUtils.setStatusBarLightMode(getWindow(), statusBarColor());
        } else {
            StatusBarUtils.setStatusBarDarkMode(getWindow(), statusBarColor());
        }
    }

    /**
     * 状态栏颜色
     *
     * @return 状态栏颜色
     */
    @ColorRes
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    protected BaseActivity self() {
        return this;
    }
}

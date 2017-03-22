package com.coderpage.mine.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-01-22
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;

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
        // Initialise the toolbar
        getToolbar();

        mToolbar.setNavigationIcon(R.drawable.ic_back);
//        mToolbar.setNavigationContentDescription(R.string.close_and_go_back);
        mToolbar.setNavigationOnClickListener(clickListener);
    }

    protected void setToolbarAsClose(View.OnClickListener clickListener) {
        // Initialise the toolbar
        getToolbar();

        mToolbar.setNavigationIcon(R.drawable.ic_close);
//        mToolbar.setNavigationContentDescription(R.string.close_and_go_back);
        mToolbar.setNavigationOnClickListener(clickListener);
    }


    private void setStatusBarColor(@ColorRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(resId);
        }
    }

    protected int statusBarColor() {
        return R.color.colorPrimary;
    }


}

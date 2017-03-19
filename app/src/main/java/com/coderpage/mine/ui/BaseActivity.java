package com.coderpage.mine.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;

import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-01-22
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (WelcomeActivity.shouldDisplay(this)) {
//            Intent intent = new Intent(this, WelcomeActivity.class);
//            startActivity(intent);
//            finish();
//        }
        setStatusBarColor(statusBarColor());
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

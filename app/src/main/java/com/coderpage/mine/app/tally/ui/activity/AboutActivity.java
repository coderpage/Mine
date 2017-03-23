package com.coderpage.mine.app.tally.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.R;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

/**
 * @author abner-l. 2017-03-23
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_about);
        setTitle(R.string.tally_toolbar_title_about);
        TextView aboutMainTv = ((TextView) findViewById(R.id.tvAboutMain));
        Spanned spanned = Html.fromHtml(getString(R.string.tally_about_main, BuildConfig.VERSION_NAME));
        aboutMainTv.setText(spanned);

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((View v) -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        DrawShadowFrameLayout drawShadowFrameLayout =
                ((DrawShadowFrameLayout) findViewById(R.id.main_content));
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);
    }

    private void setContentTopClearance(int clearance) {
        View rootView = findViewById(R.id.lyContainer);
        if (rootView != null) {
            rootView.setPadding(rootView.getPaddingLeft(), clearance,
                    rootView.getPaddingRight(), rootView.getPaddingBottom());
        }
    }
}

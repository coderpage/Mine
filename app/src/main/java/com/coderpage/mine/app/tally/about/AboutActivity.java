package com.coderpage.mine.app.tally.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.R;
import com.coderpage.mine.ui.BaseActivity;

/**
 * @author abner-l. 2017-03-23
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_about);
        TextView aboutMainTv = ((TextView) findViewById(R.id.tvAboutMain));
        Spanned spanned = Html.fromHtml(getString(R.string.tally_about_main, BuildConfig.VERSION_NAME));
        aboutMainTv.setText(spanned);

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((View v) -> finish());
    }
}

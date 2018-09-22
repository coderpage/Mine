package com.coderpage.mine.app.tally.module.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.coderpage.lib.update.ApkModel;
import com.coderpage.lib.update.Updater;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.update.UpdateUtils;
import com.coderpage.mine.ui.BaseActivity;

import java.util.Locale;

/**
 * @author abner-l. 2017-03-23
 */

public class AboutActivity extends BaseActivity {

    private TextView mNewVersionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_about);

        // 当前版本号信息
        TextView appVersionTv = (TextView) findViewById(R.id.tvAppVersion);
        String version = String.format(Locale.US, "%s (%d)",
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        appVersionTv.setText(version);

        // 新版本信息
        mNewVersionTv = (TextView) findViewById(R.id.tvCheckNewVersion);
        if (Updater.hasNewVersion(this)) {
            ApkModel apkModel = Updater.getNewVersionApkModelPersisted(this);
            mNewVersionTv.setText(getString(R.string.tally_about_find_new_version,
                    apkModel.getVersion(), apkModel.getBuildCode()));
            mNewVersionTv.setTextColor(getResources().getColor(R.color.libupdate_warning));
        }

        findViewById(R.id.lyAppInfo).setOnClickListener(mOnClickListener);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((View v) -> finish());
    }

    private View.OnClickListener mOnClickListener = (v) -> {
        int id = v.getId();
        switch (id) {
            // 检查更新
            case R.id.lyAppInfo:
                UpdateUtils.startNewClientVersionCheck(AboutActivity.this, new Updater.NewVersionCheckCallBack() {
                    @Override
                    public void onFindNewVersion(ApkModel apkModel) {
                        mNewVersionTv.setText(getString(R.string.tally_about_find_new_version,
                                apkModel.getVersion(), apkModel.getBuildCode()));
                        mNewVersionTv.setTextColor(getResources().getColor(R.color.libupdate_warning));
                    }
                });
                break;
        }
    };
}

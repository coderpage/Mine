package com.coderpage.mine.app.tally.module.about;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coderpage.lib.update.ApkModel;
import com.coderpage.lib.update.Updater;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.router.TallyRouter;
import com.coderpage.mine.app.tally.update.UpdateUtils;
import com.coderpage.mine.ui.BaseActivity;

import java.util.Locale;

/**
 * @author abner-l. 2017-03-23
 */
@Route(path = TallyRouter.ABOUT)
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
        findViewById(R.id.lyAppInfo).setOnLongClickListener(v -> {
            Toast.makeText(this, BuildConfig.FLAVOR, Toast.LENGTH_SHORT).show();
            return true;
        });
        findViewById(R.id.lyWeChatInfo).setOnClickListener(mOnClickListener);
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

            // 微信公众号点击
            case R.id.lyWeChatInfo:
                copyWeChatNumber();
                Toast.makeText(this, R.string.tally_about_wechat_copied, Toast.LENGTH_SHORT).show();
                break;
        }
    };

    /** 复制微信公众号 */
    public void copyWeChatNumber() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return;
        }
        ClipData.Item clipItem = new ClipData.Item("MINE应用");
        ClipData clipData = new ClipData("微信公众号", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, clipItem);
        clipboardManager.setPrimaryClip(clipData);
    }
}

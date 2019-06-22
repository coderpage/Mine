package com.coderpage.mine.app.tally.common.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.utils.MimeUtils;

import java.io.File;

/**
 * @author lc. 2019-06-20 18:32
 * @since 0.7.0
 */
public class ShareProxy {

    public void shareFile(Context context, File file) {
        try {
            Uri uriToFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uriToFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            } else {
                uriToFile = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uriToFile);
            intent.setType(MimeUtils.getMimeBySuffix(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "分享"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

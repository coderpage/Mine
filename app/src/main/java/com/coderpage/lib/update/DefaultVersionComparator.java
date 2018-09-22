package com.coderpage.lib.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author lc. 2017-09-23 23:45
 * @since 0.5.0
 */

public class DefaultVersionComparator implements VersionComparator {

    @Override
    public boolean compare(Context context, ApkModel apkModel) {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long clientBuildCode = packageInfo.versionCode;
            long latestVersionCode = apkModel.getBuildCode();
            return clientBuildCode < latestVersionCode;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}

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
            String clientVersion = packageInfo.versionName;
            long clientBuildCode = packageInfo.versionCode;

            String latestVersionName = apkModel.getVersion();
            long latestVersionCode = apkModel.getBuildCode();

            int compareResult = compareVersion(latestVersionName, clientVersion);
            if (compareResult > 0) {
                return true;
            }
            if (compareResult == 0) {
                return clientBuildCode < latestVersionCode;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 比较版本大小
     *
     * @param version1 版本1
     * @param version2 版本2
     *
     * @return 如果相等返回0，如果 version1>version2 返回1，如果 version1<version2 返回-1
     */
    static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        int[] versionInt1Array = new int[version1Array.length];
        int[] versionInt2Array = new int[version2Array.length];

        for (int index = 0; index < version1Array.length; index++) {
            String versionItem = version1Array[index];
            try {
                int versionInt = Integer.parseInt(versionItem);
                versionInt1Array[index] = versionInt;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                versionInt1Array[index] = 0;
            }
        }
        for (int index = 0; index < version2Array.length; index++) {
            String versionItem = version2Array[index];
            try {
                int versionInt = Integer.parseInt(versionItem);
                versionInt2Array[index] = versionInt;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                versionInt2Array[index] = 0;
            }
        }

        int minLen = Math.min(versionInt1Array.length, versionInt2Array.length);
        int index;
        for (index = 0; index < minLen; index++) {
            int compare = versionInt1Array[index] - versionInt2Array[index];
            if (compare > 0) {
                return 1;
            }
            if (compare < 0) {
                return -1;
            }
        }
        if (versionInt1Array.length > minLen) {
            for (; index < versionInt1Array.length; index++) {
                if (versionInt1Array[index] > 0) {
                    return 1;
                }
                if (versionInt1Array[index] < 0) {
                    return -1;
                }
            }
        }
        if (versionInt2Array.length > minLen) {
            for (; index < versionInt2Array.length; index++) {
                if (versionInt2Array[index] > 0) {
                    return -1;
                }
                if (versionInt2Array[index] < 0) {
                    return 1;
                }
            }
        }
        return 0;
    }
}

package com.coderpage.lib.update;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author lc. 2017-10-06 23:29
 * @since 0.5.0
 */

class NetWorkUtils {

    /**
     * 获取网络是否可用
     */
    public static boolean isNetworkOK(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取网络状态信息
     */
    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * 获取 WiFi 是否连接
     */
    public static boolean isWifiOK(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo == null) {
            return false;
        }
        return (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) &&
                (networkInfo.isConnected());
    }

    /**
     * 获取手机网络是否连接
     */
    public static boolean isMobileOK(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo == null) {
            return false;
        }
        return (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) &&
                (networkInfo.isConnected());
    }
}

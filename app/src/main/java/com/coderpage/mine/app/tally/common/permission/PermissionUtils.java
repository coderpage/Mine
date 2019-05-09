package com.coderpage.mine.app.tally.common.permission;

import android.content.Context;
import android.text.TextUtils;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.mine.R;

/**
 * @author : liuchao
 * created on 2019/4/29 8:22 PM
 */
public class PermissionUtils {

    /**
     * 通过权限获取对应的名称
     *
     * @param permission 权限
     */
    public static String getPermissionName(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }

        switch (permission) {

            case android.Manifest.permission.READ_EXTERNAL_STORAGE:
            case android.Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return ResUtils.getString(context, R.string.permission_storage);

            case android.Manifest.permission.READ_PHONE_STATE:
                return ResUtils.getString(context, R.string.permission_phone);

            default:
                return "";
        }
    }

    /**
     * 通过权限获取对应的说明
     *
     * @param permission 权限
     */
    public static String getPermissionDesc(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }

        switch (permission) {

            case android.Manifest.permission.READ_EXTERNAL_STORAGE:
            case android.Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return ResUtils.getString(context, R.string.permission_storage_desc);

            case android.Manifest.permission.READ_PHONE_STATE:
                return ResUtils.getString(context, R.string.permission_phone_desc);

            default:
                return "";
        }
    }
}

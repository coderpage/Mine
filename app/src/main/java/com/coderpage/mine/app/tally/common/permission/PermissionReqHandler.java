package com.coderpage.mine.app.tally.common.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.coderpage.base.utils.ArrayUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-05-09 16:49
 * @since 0.6.2
 */
public class PermissionReqHandler {

    private static final int REQUEST_CODE_PERMISSION = 110;

    /** 强制请求 */
    private boolean mIsForceReq;
    /** 申请权限监听 */
    private Listener mListener;
    /** activity */
    private WeakReference<Activity> mActivityRef;

    private String[] mPermissionArray;

    public PermissionReqHandler(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    /**
     * 请求权限
     *
     * @param forceReq        是否强制请求权限（拒绝后会再次请求）
     * @param permissionArray 请求的权限列表
     * @param listener        请求权限的监听器
     */
    public void requestPermission(boolean forceReq, String[] permissionArray, Listener listener) {
        Activity activity = mActivityRef.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        mIsForceReq = forceReq;
        mPermissionArray = permissionArray;
        mListener = listener;
        // 获取当前未授权的权限列表
        String[] notGrantedPermissionArray = getNotGrantedPermissionArray(activity, permissionArray);
        // 全部已经授权，返回成功
        if (notGrantedPermissionArray.length == 0) {
            mListener.onGranted(true, permissionArray);
            return;
        }
        // 请求权限
        ActivityCompat.requestPermissions(activity, notGrantedPermissionArray, REQUEST_CODE_PERMISSION);
    }

    /** 获取当前未授权的权限数组 */
    public String[] getNotGrantedPermissionArray(Activity activity, String[] permissionArray) {
        // 低于 M 不需要动态请求权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new String[0];
        }
        // 未授权的权限列表
        List<String> notGrantedPermission = new ArrayList<>();
        ArrayUtils.forEach(permissionArray, (count, index, permission) -> {
            // 是否已经授权
            boolean granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                    && PermissionChecker.isPermissionGranted(activity, permission);
            if (!granted) {
                notGrantedPermission.add(permission);
            }
        });
        String[] notGrantedPermissionArray = new String[notGrantedPermission.size()];
        ArrayUtils.forEach(notGrantedPermission, (count, index, item) -> notGrantedPermissionArray[index] = item);
        return notGrantedPermissionArray;
    }

    /**
     * 请求权限回调
     *
     * @param requestCode  请求 code
     * @param permissions  请求的权限列表
     * @param grantResults 授权结果
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE_PERMISSION) {
            return;
        }
        Activity activity = mActivityRef.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        // 非强制请求权限，只请求一次
        if (!mIsForceReq) {
            List<String> deniedPermissionList = new ArrayList<>();
            List<String> grantedPermissionList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissionList.add(permission);
                } else {
                    deniedPermissionList.add(permission);
                }
            }
            String[] grantedPermissionArr = new String[grantedPermissionList.size()];
            String[] deniedPermissionArr = new String[deniedPermissionList.size()];
            ArrayUtils.forEach(deniedPermissionList, (count, index, item) -> deniedPermissionArr[index] = item);
            ArrayUtils.forEach(grantedPermissionList, (count, index, item) -> grantedPermissionArr[index] = item);

            if (grantedPermissionArr.length > 0) {
                mListener.onGranted(grantedPermissionArr.length == permissions.length, grantedPermissionArr);
            }
            if (deniedPermissionArr.length > 0) {
                mListener.onDenied(deniedPermissionArr);
            }
            return;
        }

        // 用户拒绝的权限
        List<String> notGrantedPermissionList = new ArrayList<>();
        // 用户拒绝且勾选不再提示的权限
        List<String> forceRefusedPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            int grantResult = grantResults[i];
            String permission = permissions[i];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                notGrantedPermissionList.add(permissions[i]);
            } else {
                forceRefusedPermissionList.add(permission);
            }
        }

        // 用户拒绝，再次请求
        if (!notGrantedPermissionList.isEmpty()) {
            String[] permissionArray = new String[notGrantedPermissionList.size()];
            ArrayUtils.forEach(notGrantedPermissionList, (count, index, item) -> permissionArray[index] = item);
            requestPermission(mIsForceReq, permissionArray, mListener);
            return;
        }

        // 全部授权完成
        if (forceRefusedPermissionList.isEmpty()) {
            mListener.onGranted(true, mPermissionArray);
            return;
        }

        String[] permissionArray = new String[forceRefusedPermissionList.size()];
        ArrayUtils.forEach(forceRefusedPermissionList, (count, index, item) -> permissionArray[index] = item);
        mListener.onDenied(permissionArray);
    }

    public interface Listener {

        void onGranted(boolean grantedAll, String[] permissionArray);

        void onDenied(String[] permissionArray);
    }
}

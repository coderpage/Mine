package com.coderpage.base.utils;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author lc. 2018-09-03 23:20
 * @since 0.6.0
 */

public class StatusBarUtils {
    /**
     * 设置状态栏文字颜色为黑色
     *
     * @param activity       activity
     * @param statusBarColor 状态栏的颜色
     */
    public static void setStatusBarLightMode(Activity activity, @ColorRes int statusBarColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        // 状态栏文字颜色设置结果
        boolean lightModeSetSuccess = true;
        FitUtils.Brand brand = FitUtils.getBrand();
        if (brand == FitUtils.Brand.XIAO_MI) {
            // 小米 状态栏颜色
            lightModeSetSuccess = setStatusBarLightModeMIUI(activity, true);
        } else if (brand == FitUtils.Brand.MEI_ZU) {
            // 魅族 状态栏颜色
            lightModeSetSuccess = setStatusBarLightModeFlyMe(activity.getWindow(), true);
        } else {
            // Android 6.0 之后的原生方式
            setStatusBarLightModeOrigin(activity, true);
        }

        if (lightModeSetSuccess) {
            // 黑色字体设置成功
            setStatusBarColor(activity, statusBarColor);
        }
    }

    /**
     * 设置状态栏文字颜色为白色
     *
     * @param activity       activity
     * @param statusBarColor 状态栏的颜色
     */
    public static void setStatusBarDarkMode(Activity activity, @ColorRes int statusBarColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        // 状态栏文字颜色设置结果
        boolean darkModeSetSuccess = true;
        FitUtils.Brand brand = FitUtils.getBrand();
        if (brand == FitUtils.Brand.XIAO_MI) {
            // 小米 状态栏颜色
            darkModeSetSuccess = setStatusBarLightModeMIUI(activity, false);
        } else if (brand == FitUtils.Brand.MEI_ZU) {
            // 魅族 状态栏颜色
            darkModeSetSuccess = setStatusBarLightModeFlyMe(activity.getWindow(), false);
        } else {
            // Android 6.0 之后的原生方式
            setStatusBarLightModeOrigin(activity, false);
        }

        setStatusBarColor(activity, statusBarColor);
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     */
    public static void setStatusBarColor(Activity activity, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(ResUtils.getColor(activity, colorId));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window    需要设置的窗口
     * @param lightMode 是否把状态栏文字及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean setStatusBarLightModeFlyMe(Window window, boolean lightMode) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (lightMode) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 需要MIUIV6以上
     *
     * @param lightMode 是否把状态栏文字及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean setStatusBarLightModeMIUI(Activity activity, boolean lightMode) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int lightModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                lightModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (lightMode) {
                    extraFlagField.invoke(window, lightModeFlag, lightModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, lightModeFlag);//清除黑色字体
                }
                result = true;

                //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以使用原生系统方法再设置一次
                setStatusBarLightModeOrigin(activity, lightMode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /** 使用原生系统 API 来设置状态栏文字颜色 */
    private static void setStatusBarLightModeOrigin(Activity activity, boolean lightMode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        View decorView = activity.getWindow().getDecorView();
        int originVisibility = decorView.getSystemUiVisibility();
        // 亮色模式，使用黑色文字
        if (lightMode && (originVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) == 0) {
            originVisibility = originVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        // 暗色模式，使用白色文字
        if (!lightMode && (originVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0) {
            originVisibility = originVisibility ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        decorView.setSystemUiVisibility(originVisibility);
    }
}

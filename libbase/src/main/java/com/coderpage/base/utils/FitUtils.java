package com.coderpage.base.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * @author lc. 2019-03-25 15:40
 * @since 0.6.0
 */


public class FitUtils {

    /** 手机品牌枚举 */
    public enum Brand {
        // 未知品牌
        UNKNOWN,
        // 华为
        HUAWEI,
        // 小米
        XIAO_MI,
        // vivo
        VIVO,
        // oppo
        OPPO,
        // 魅族
        MEI_ZU,
        // 三星
        SAMSUNG,
        // 诺基亚
        NOKIA,
        // google 亲儿子
        NEXUS,
    }

    /** 返回手机系统的品牌 */
    public static Brand getBrand() {
        Brand brand;
        String brandString = Build.BRAND;
        if (TextUtils.isEmpty(brandString)) {
            return Brand.UNKNOWN;
        }
        switch (brandString.toLowerCase()) {
            case "huawei":
                return Brand.HUAWEI;

            case "xiaomi":
                return Brand.XIAO_MI;

            case "vivo":
                return Brand.VIVO;

            case "oppo":
                return Brand.OPPO;

            case "meizu":
                return Brand.MEI_ZU;

            case "samsung":
                return Brand.SAMSUNG;

            case "google":
                return Brand.NEXUS;

            case "nokia":
                return Brand.NOKIA;

            default:
                return Brand.UNKNOWN;
        }
    }

    /** 返回手机屏幕是否带有刘海 */
    public static boolean isWindowHasFringe(Context context) {
        Brand brand = getBrand();

        switch (brand) {
            case XIAO_MI:
                return isWindowHasFringeXiaoMi();

            case OPPO:
                return isWindowHasFringeOppo(context);

            case VIVO:
                return isWindowHasFringeVivo();

            case HUAWEI:
                return isWindowHasFringeHuawei(context);

            default:
                return false;
        }
    }

    /**
     * 小米手机是否有刘海
     * https://dev.mi.com/console/doc/detail?pId=1293
     */
    private static boolean isWindowHasFringeXiaoMi() {
        try {
            Class<?> systemPropertiesCls = Class.forName("android.os.SystemProperties");
            Method getIntMethod = systemPropertiesCls.getMethod("getInt", new Class[]{String.class, int.class});
            int value = (int) getIntMethod.invoke(null, "ro.miui.notch", 0);
            return value == 1;
        } catch (Exception e) {
            // no-op
        }
        return false;
    }


    /**
     * Vivo 手机是否有刘海
     *
     * 参考文档 https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     */
    private static boolean isWindowHasFringeVivo() {
        try {
            Class cls = Class.forName("android.util.FtFeature");
            Method method = cls.getMethod("isFeatureSupport", int.class);
            return (boolean) method.invoke(null, 0x00000020);
        } catch (Exception e) {
            // no-op
        }
        return false;
    }

    /**
     * Oppo 手机是否有刘海
     *
     * 参考文档 https://open.oppomobile.com/wiki/doc#id=10139
     */
    private static boolean isWindowHasFringeOppo(Context context) {
        try {
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            // no-op
        }
        return false;
    }

    /**
     * 华为手机是否有刘海
     *
     * 参考 https://blog.csdn.net/li15225271052/article/details/79967647
     */
    private static boolean isWindowHasFringeHuawei(Context context) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class cls = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method method = cls.getMethod("hasNotchInScreen");
            return (boolean) method.invoke(cls);
        } catch (Exception e) {
            // no-op
        }
        return false;
    }
}

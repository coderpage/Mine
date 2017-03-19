package com.coderpage.mine.app.tally.utils;

import android.content.Context;

import com.coderpage.mine.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @author abner-l. 2017-03-19
 */

public class CategoryPicUtils {

    private static WeakReference<HashMap<String, Integer>> categoryIconResMapRef
            = new WeakReference<>(null);

    public static HashMap<String, Integer> getCategoryIconResMap(Context context) {
        HashMap<String, Integer> map = categoryIconResMapRef.get();
        if (map == null || map.isEmpty()) {
            map = new HashMap<>(16);
            map.put(context.getString(R.string.tyIcOther), R.drawable.ic_category_other);
            map.put(context.getString(R.string.tyIcCanYin), R.drawable.ic_category_canyin);
            map.put(context.getString(R.string.tyIcJiaoTong), R.drawable.ic_category_jiaotong);
            map.put(context.getString(R.string.tyIcGouWu), R.drawable.ic_category_gouwu);
            map.put(context.getString(R.string.tyIcFuShi), R.drawable.ic_category_fushi);
            map.put(context.getString(R.string.tyIcRiYongPin), R.drawable.ic_category_riyongpin);
            map.put(context.getString(R.string.tyIcYuLe), R.drawable.ic_category_yule);
            map.put(context.getString(R.string.tyIcShiCai), R.drawable.ic_category_shicai);
            map.put(context.getString(R.string.tyIcLingShi), R.drawable.ic_category_lingshi);
            map.put(context.getString(R.string.tyIcYanJiuCha), R.drawable.ic_category_yanjiucha);
            map.put(context.getString(R.string.tyIcXueXi), R.drawable.ic_category_xuexi);
            map.put(context.getString(R.string.tyIcYiLiao), R.drawable.ic_category_yiliao);
            map.put(context.getString(R.string.tyIcZhuFang), R.drawable.ic_category_zhufang);
            map.put(context.getString(R.string.tyIcShuiDianMei), R.drawable.ic_category_shuidianmei);
            map.put(context.getString(R.string.tyIcTongXun), R.drawable.ic_category_tongxun);
            map.put(context.getString(R.string.tyIcRenQing), R.drawable.ic_category_renqing);
            categoryIconResMapRef = new WeakReference<>(map);
        }
        return map;
    }

//    public static Drawable getCategoryIcon(Context context, String name) {
//        HashMap<String, Integer> map = getCategoryIconResMap(context);
//        Integer iconResId = map.get(name);
//        if (iconResId == null) {
//            iconResId = R.drawable.ic_category_other;
//        }
//        return context.getResources().getDrawable(iconResId);
//    }

}

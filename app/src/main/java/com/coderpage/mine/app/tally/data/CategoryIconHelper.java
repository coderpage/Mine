package com.coderpage.mine.app.tally.data;

import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-03-23
 */

public class CategoryIconHelper {

    public static final String IC_NAME_OTHER = "Other"; // 其他
    public static final String IC_NAME_CAN_YIN = "CanYin";// 餐饮
    public static final String IC_NAME_JIAO_TONG = "JiaoTong";// 交通
    public static final String IC_NAME_GOU_WU = "GouWu";// 住房
    public static final String IC_NAME_FU_SHI = "FuShi";// 服饰
    public static final String IC_NAME_RI_YONG_PIN = "RiYongPin";// 日用品
    public static final String IC_NAME_YU_LE = "YuLe";// 娱乐
    public static final String IC_NAME_SHI_CAI = "ShiCai";// 食材
    public static final String IC_NAME_LING_SHI = "LingShi";// 零食
    public static final String IC_NAME_YAN_JIU_CHA = "YanJiuCha";// 烟酒茶
    public static final String IC_NAME_XUE_XI = "XueXi";// 学习
    public static final String IC_NAME_YI_LIAO = "YiLiao";// 医疗
    public static final String IC_NAME_ZHU_FANG = "ZhuFang";// 住房
    public static final String IC_NAME_SHUI_DIAN_MEI = "ShuiDianMei";// 水电煤
    public static final String IC_NAME_TONG_XUN = "TongXun";// 通讯
    public static final String IC_NAME_REN_QING = "RenQing";// 人情来往

    public static int resId(String iconName) {
        switch (iconName) {
            case IC_NAME_OTHER:
                return R.drawable.ic_category_other;
            case IC_NAME_CAN_YIN:
                return R.drawable.ic_category_canyin;
            case IC_NAME_JIAO_TONG:
                return R.drawable.ic_category_jiaotong;
            case IC_NAME_GOU_WU:
                return R.drawable.ic_category_gouwu;
            case IC_NAME_FU_SHI:
                return R.drawable.ic_category_fushi;
            case IC_NAME_RI_YONG_PIN:
                return R.drawable.ic_category_riyongpin;
            case IC_NAME_YU_LE:
                return R.drawable.ic_category_yule;
            case IC_NAME_SHI_CAI:
                return R.drawable.ic_category_shicai;
            case IC_NAME_LING_SHI:
                return R.drawable.ic_category_lingshi;
            case IC_NAME_YAN_JIU_CHA:
                return R.drawable.ic_category_yanjiucha;
            case IC_NAME_XUE_XI:
                return R.drawable.ic_category_xuexi;
            case IC_NAME_YI_LIAO:
                return R.drawable.ic_category_yiliao;
            case IC_NAME_ZHU_FANG:
                return R.drawable.ic_category_zhufang;
            case IC_NAME_SHUI_DIAN_MEI:
                return R.drawable.ic_category_shuidianmei;
            case IC_NAME_TONG_XUN:
                return R.drawable.ic_category_tongxun;
            case IC_NAME_REN_QING:
                return R.drawable.ic_category_renqing;
            default:
                return R.drawable.ic_category_other;
        }
    }
}

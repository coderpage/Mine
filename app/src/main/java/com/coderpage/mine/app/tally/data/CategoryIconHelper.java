package com.coderpage.mine.app.tally.data;

import com.coderpage.mine.R;

/**
 * @author abner-l. 2017-03-23
 */

public class CategoryIconHelper {

    /** 其他 */
    public static final String IC_NAME_OTHER = "Other";
    /** 餐饮 */
    public static final String IC_NAME_CAN_YIN = "CanYin";
    /** 交通 */
    public static final String IC_NAME_JIAO_TONG = "JiaoTong";
    /** 住房 */
    public static final String IC_NAME_GOU_WU = "GouWu";
    /** 服饰 */
    public static final String IC_NAME_FU_SHI = "FuShi";
    /** 日用品 */
    public static final String IC_NAME_RI_YONG_PIN = "RiYongPin";
    /** 娱乐 */
    public static final String IC_NAME_YU_LE = "YuLe";
    /** 食材 */
    public static final String IC_NAME_SHI_CAI = "ShiCai";
    /** 零食 */
    public static final String IC_NAME_LING_SHI = "LingShi";
    /** 烟酒茶 */
    public static final String IC_NAME_YAN_JIU_CHA = "YanJiuCha";
    /** 学习 */
    public static final String IC_NAME_XUE_XI = "XueXi";
    /** 医疗 */
    public static final String IC_NAME_YI_LIAO = "YiLiao";
    /** 住房 */
    public static final String IC_NAME_ZHU_FANG = "ZhuFang";
    /** 水电煤 */
    public static final String IC_NAME_SHUI_DIAN_MEI = "ShuiDianMei";
    /** 通讯 */
    public static final String IC_NAME_TONG_XUN = "TongXun";
    /** 人情来往 */
    public static final String IC_NAME_REN_QING = "RenQing";

    /** 薪资 */
    public static final String IC_NAME_XIN_ZI = "XinZi";
    /** 奖金 */
    public static final String IC_NAME_JIANG_JIN = "JiangJin";
    /** 借入 */
    public static final String IC_NAME_JIE_RU = "JieRu";
    /** 收债 */
    public static final String IC_NAME_SHOU_ZHAI = "ShouZhai";
    /** 利息收入 */
    public static final String IC_NAME_LI_XIN_SHOU_RU = "LixiShouRu";
    /** 投资回收 */
    public static final String IC_NAME_TOU_ZI_HUI_SHOU = "TouZiHuiShou";
    /** 意外所得 */
    public static final String IC_NAME_YI_WAI_SUO_DE = "YiWaiSuoDe";
    /** 投资收益 */
    public static final String IC_NAME_TOU_ZI_SHOU_YI = "TouZiShouYi";

    public static int resId(String iconName) {
        if (iconName == null) {
            return R.drawable.ic_category_expense_other;
        }
        switch (iconName) {
            // 支出
            case IC_NAME_OTHER:
                return R.drawable.ic_category_expense_other;
            case IC_NAME_CAN_YIN:
                return R.drawable.ic_category_expense_food;
            case IC_NAME_JIAO_TONG:
                return R.drawable.ic_category_expense_traffic;
            case IC_NAME_GOU_WU:
                return R.drawable.ic_category_expense_shopping;
            case IC_NAME_FU_SHI:
                return R.drawable.ic_category_expense_clothes;
            case IC_NAME_RI_YONG_PIN:
                return R.drawable.ic_category_expense_daily_necessities;
            case IC_NAME_YU_LE:
                return R.drawable.ic_category_expense_entertainment;
            case IC_NAME_SHI_CAI:
                return R.drawable.ic_category_expense_food_ingredients;
            case IC_NAME_LING_SHI:
                return R.drawable.ic_category_expense_snack;
            case IC_NAME_YAN_JIU_CHA:
                return R.drawable.ic_category_expense_tobacco_tea;
            case IC_NAME_XUE_XI:
                return R.drawable.ic_category_expense_study;
            case IC_NAME_YI_LIAO:
                return R.drawable.ic_category_expense_medical;
            case IC_NAME_ZHU_FANG:
                return R.drawable.ic_category_expense_house;
            case IC_NAME_SHUI_DIAN_MEI:
                return R.drawable.ic_category_expense_electricity;
            case IC_NAME_TONG_XUN:
                return R.drawable.ic_category_expense_communication;
            case IC_NAME_REN_QING:
                return R.drawable.ic_category_expense_favor_pattern;

            // 收入
            case IC_NAME_XIN_ZI:
                return R.drawable.ic_category_income_salary;
            case IC_NAME_JIANG_JIN:
                return R.drawable.ic_category_income_reward;
            case IC_NAME_JIE_RU:
                return R.drawable.ic_category_income_lend;
            case IC_NAME_SHOU_ZHAI:
                return R.drawable.ic_category_income_dun;
            case IC_NAME_LI_XIN_SHOU_RU:
                return R.drawable.ic_category_income_interest;
            case IC_NAME_TOU_ZI_HUI_SHOU:
                return R.drawable.ic_category_income_invest_recovery;
            case IC_NAME_YI_WAI_SUO_DE:
                return R.drawable.ic_category_income_unexpected;
            case IC_NAME_TOU_ZI_SHOU_YI:
                return R.drawable.ic_category_income_invest_profit;
            default:
                return R.drawable.ic_category_expense_other;
        }
    }
}

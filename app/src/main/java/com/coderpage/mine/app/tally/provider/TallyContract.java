package com.coderpage.mine.app.tally.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author abner-l. 2017-02-12
 * @since 0.1.0
 */

public class TallyContract {

    public static final String CONTENT_TYPE_APP_BASE = "minetally.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    /**
     * 消费记录表字段
     */
    interface ExpenseColumns {
        /** 分类 ID */
        String CATEGORY_ID = "category_id";
        /** 金额 */
        String AMOUNT = "expense_amount";
        /** 分类 */
        String CATEGORY = "expense_category";
        /** 描述 */
        String DESC = "expense_desc";
        /** 时间 */
        String TIME = "expense_time";
    }

    /**
     * 消费记录分类表字段
     */
    interface CategoryColumns {
        /** 分类名称 */
        String NAME = "category_name";
        /** 图标 */
        String ICON = "category_icon";
        /** 排序 */
        String ORDER = "category_order";
    }

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static final String CONTENT_AUTHORITY = "com.coderpage.mine.apps.tally";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_EXPENSE = "expense";
    private static final String PATH_CATEGORY = "category";

    public static class Expense implements ExpenseColumns, BaseColumns {
        public static final String CONTENT_TYPE_ID = "expense";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXPENSE).build();

        public static final Uri buildExpenseUri(String actionId) {
            return CONTENT_URI.buildUpon().appendPath(actionId).build();
        }
    }

    public static class Category implements CategoryColumns, BaseColumns {
        public static final String CONTENT_TYPE_ID = "category";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final Uri buildCategoryUri(String actionId) {
            return CONTENT_URI.buildUpon().appendPath(actionId).build();
        }
    }
}

package com.coderpage.mine.app.tally.persistence.sql;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.coderpage.base.utils.LogUtils;
import com.coderpage.mine.MineApp;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.CategoryContant;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.persistence.sql.dao.CategoryDao;
import com.coderpage.mine.app.tally.persistence.sql.dao.RecordDao;
import com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity;
import com.coderpage.mine.app.tally.persistence.sql.entity.RecordEntity;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-05-20 15:28
 * @since 0.6.0
 */

@Database(entities = {RecordEntity.class, CategoryEntity.class}, version = 60, exportSchema = false)
public abstract class TallyDatabase extends RoomDatabase {
    /** sqlite db name */
    private static final String DATABASE_NAME = "sql_tally";

    /** db version of app version 0.1.0 */
    private static final int VERSION_0_1_0 = 1;
    /** db version of app version 0.4.0 */
    private static final int VERSION_0_4_0 = 40;
    /** db version of app version 0.6.0 */
    private static final int VERSION_0_6_0 = 60;

    private static TallyDatabase sInstance = null;

    /**
     * 记录操作
     *
     * @return 记录操作
     */
    public abstract RecordDao recordDao();

    /**
     * 分类表
     *
     * @return 分类表操作
     */
    public abstract CategoryDao categoryDao();

    public static TallyDatabase getInstance() {
        if (sInstance == null) {
            synchronized (TallyDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            MineApp.getAppContext(),
                            TallyDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_010_040, MIGRATION_040_060)
                            .addCallback(mTallDatabaseCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return sInstance;
    }

    private static Callback mTallDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Context context = MineApp.getAppContext();

            List<CategoryItem> categoryList = new ArrayList<>();
            // 初始化支出分类
            categoryList.add(expenseCategory(CategoryContant.NAME_OTHER_EXPENSE, context.getString(R.string.tyOther), CategoryIconHelper.IC_NAME_OTHER));
            categoryList.add(expenseCategory(CategoryContant.NAME_CAN_YIN, context.getString(R.string.tyFoodAndBeverage), CategoryIconHelper.IC_NAME_CAN_YIN));
            categoryList.add(expenseCategory(CategoryContant.NAME_JIAO_TONG, context.getString(R.string.tyTraffic), CategoryIconHelper.IC_NAME_JIAO_TONG));
            categoryList.add(expenseCategory(CategoryContant.NAME_GOU_WU, context.getString(R.string.tyShopping), CategoryIconHelper.IC_NAME_GOU_WU));
            categoryList.add(expenseCategory(CategoryContant.NAME_FU_SHI, context.getString(R.string.tyClothes), CategoryIconHelper.IC_NAME_FU_SHI));
            categoryList.add(expenseCategory(CategoryContant.NAME_RI_YONG_PIN, context.getString(R.string.tyDailyNecessities), CategoryIconHelper.IC_NAME_RI_YONG_PIN));
            categoryList.add(expenseCategory(CategoryContant.NAME_YU_LE, context.getString(R.string.tyEntertainment), CategoryIconHelper.IC_NAME_YU_LE));
            categoryList.add(expenseCategory(CategoryContant.NAME_SHI_CAI, context.getString(R.string.tyFoodIngredients), CategoryIconHelper.IC_NAME_SHI_CAI));
            categoryList.add(expenseCategory(CategoryContant.NAME_LING_SHI, context.getString(R.string.tySnacks), CategoryIconHelper.IC_NAME_LING_SHI));
            categoryList.add(expenseCategory(CategoryContant.NAME_YAN_JIU_CHA, context.getString(R.string.tyTobaccoAnTea), CategoryIconHelper.IC_NAME_YAN_JIU_CHA));
            categoryList.add(expenseCategory(CategoryContant.NAME_XUE_XI, context.getString(R.string.tyStudy), CategoryIconHelper.IC_NAME_XUE_XI));
            categoryList.add(expenseCategory(CategoryContant.NAME_YI_LIAO, context.getString(R.string.tyMedical), CategoryIconHelper.IC_NAME_YI_LIAO));
            categoryList.add(expenseCategory(CategoryContant.NAME_ZHU_FANG, context.getString(R.string.tyHouse), CategoryIconHelper.IC_NAME_ZHU_FANG));
            categoryList.add(expenseCategory(CategoryContant.NAME_SHUI_DIAN_MEI, context.getString(R.string.tyWaterElectricityCoal), CategoryIconHelper.IC_NAME_SHUI_DIAN_MEI));
            categoryList.add(expenseCategory(CategoryContant.NAME_TONG_XUN, context.getString(R.string.tyCommunication), CategoryIconHelper.IC_NAME_TONG_XUN));
            categoryList.add(expenseCategory(CategoryContant.NAME_REN_QING, context.getString(R.string.tyTheFavorPattern), CategoryIconHelper.IC_NAME_REN_QING));
            // 初始化收入分类
            categoryList.add(incomeCategory(CategoryContant.NAME_OTHER_IN_COME, context.getString(R.string.tyOther), CategoryIconHelper.IC_NAME_OTHER));
            categoryList.add(incomeCategory(CategoryContant.NAME_XIN_ZI, context.getString(R.string.tyIncomeSalary), CategoryIconHelper.IC_NAME_XIN_ZI));
            categoryList.add(incomeCategory(CategoryContant.NAME_JIANG_JIN, context.getString(R.string.tyIncomeReward), CategoryIconHelper.IC_NAME_JIANG_JIN));
            categoryList.add(incomeCategory(CategoryContant.NAME_JIE_RU, context.getString(R.string.tyIncomeLend), CategoryIconHelper.IC_NAME_JIE_RU));
            categoryList.add(incomeCategory(CategoryContant.NAME_SHOU_ZHAI, context.getString(R.string.tyIncomeDun), CategoryIconHelper.IC_NAME_SHOU_ZHAI));
            categoryList.add(incomeCategory(CategoryContant.NAME_LI_XIN_SHOU_RU, context.getString(R.string.tyIncomeInterest), CategoryIconHelper.IC_NAME_LI_XIN_SHOU_RU));
            categoryList.add(incomeCategory(CategoryContant.NAME_TOU_ZI_HUI_SHOU, context.getString(R.string.tyIncomeInvestRecovery), CategoryIconHelper.IC_NAME_TOU_ZI_HUI_SHOU));
            categoryList.add(incomeCategory(CategoryContant.NAME_TOU_ZI_SHOU_YI, context.getString(R.string.tyIncomeInvestProfit), CategoryIconHelper.IC_NAME_TOU_ZI_SHOU_YI));
            categoryList.add(incomeCategory(CategoryContant.NAME_YI_WAI_SUO_DE, context.getString(R.string.tyIncomeUnexpected), CategoryIconHelper.IC_NAME_YI_WAI_SUO_DE));

            // 插入记录事务
            db.beginTransaction();
            // 插入分类
            for (CategoryItem categoryItem : categoryList) {
                ContentValues values = new ContentValues(4);
                values.put("category_unique_name", categoryItem.uniqueName);
                values.put("category_name", categoryItem.name);
                values.put("category_icon", categoryItem.icon);
                values.put("category_type", categoryItem.type);
                values.put("category_order", 0);
                values.put("category_account_id", 0);
                values.put("category_sync_status", 0);
                long id = db.insert("category", SQLiteDatabase.CONFLICT_NONE, values);

                LogUtils.LOGI("TallyDatabase", "insert expense category. id:" + id + " name:" + categoryItem.name);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    };

    private static final Migration MIGRATION_010_040 = new Migration(VERSION_0_1_0, VERSION_0_4_0) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE expense1" + " ("
                    + TallyContract.Expense._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TallyContract.Expense.CATEGORY_ID + " INTEGER NOT NULL,"
                    + TallyContract.Expense.CATEGORY + " TEXT NOT NULL,"
                    + TallyContract.Expense.AMOUNT + " DOUBLE NOT NULL,"
                    + TallyContract.Expense.DESC + " TEXT NOT NULL DEFAULT '',"
                    + TallyContract.Expense.TIME + " DOUBLE NOT NULL,"
                    + TallyContract.Expense.ACCOUNT_ID + " INTEGER NOT NULL DEFAULT 0,"
                    + TallyContract.Expense.SYNC_ID + " TEXT NOT NULL,"
                    + TallyContract.Expense.SYNCED + " INTEGER NOT NULL DEFAULT 0,"
                    + "UNIQUE (" + TallyContract.Expense.SYNC_ID + ") ON CONFLICT IGNORE)"
            );

            database.execSQL("CREATE TABLE category1" + " ("
                    + TallyContract.Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TallyContract.Category.NAME + " TEXT NOT NULL,"
                    + TallyContract.Category.ICON + " TEXT,"
                    + TallyContract.Category.ORDER + " INTEGER NOT NULL DEFAULT(0),"
                    + "UNIQUE (" + TallyContract.Category.NAME + ") ON CONFLICT IGNORE)");


            // 生成 UUID
            String uuid = "hex( randomblob(4)) || '-' || " +
                    "hex( randomblob(2))|| '-' || '4' || " +
                    "substr( hex( randomblob(2)), 2) || '-' || " +
                    "substr('AB89', 1 + (abs(random()) % 4) , 1)  || " +
                    "substr(hex(randomblob(2)), 2) || '-' || " +
                    "hex(randomblob(6))";

            database.execSQL("INSERT INTO expense1 ("
                    + TallyContract.Expense._ID + ","
                    + TallyContract.Expense.CATEGORY_ID + ","
                    + TallyContract.Expense.CATEGORY + ","
                    + TallyContract.Expense.AMOUNT + ","
                    + TallyContract.Expense.DESC + ","
                    + TallyContract.Expense.TIME + ","
                    + TallyContract.Expense.SYNC_ID + ") SELECT "
                    + TallyContract.Expense._ID + ","
                    + TallyContract.Expense.CATEGORY_ID + ","
                    + TallyContract.Expense.CATEGORY + ","
                    + TallyContract.Expense.AMOUNT + ","
                    + TallyContract.Expense.DESC + ","
                    + TallyContract.Expense.TIME + ","
                    + uuid + " FROM expense"
            );

            database.execSQL("INSERT INTO category1" + " SELECT *  FROM category");

            database.execSQL("DROP TABLE expense");
            database.execSQL("DROP TABLE category");
            database.execSQL("ALTER TABLE expense1" + " RENAME TO expense");
            database.execSQL("ALTER TABLE category1" + " RENAME TO category");
        }
    };

    private static final Migration MIGRATION_040_060 = new Migration(VERSION_0_4_0, VERSION_0_6_0) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 支出表、分类表 更名 expense040 category040
            // 通过重建表方式升级表结构
            database.execSQL("ALTER TABLE expense RENAME TO expense040");
            database.execSQL("ALTER TABLE category RENAME TO category040");

            // 创建记录表
            database.execSQL("CREATE TABLE record ("
                    + "record_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    // 分类-唯一名称
                    + "record_category_unique_name TEXT,"
                    + "record_amount REAL NOT NULL,"
                    + "record_desc TEXT NOT NULL DEFAULT '',"
                    + "record_time INTEGER NOT NULL,"
                    + "record_account_id INTEGER NOT NULL DEFAULT 0,"
                    + "record_sync_id TEXT NOT NULL,"
                    + "record_sync_status INTEGER NOT NULL DEFAULT 0,"
                    + "record_delete INTEGER NOT NULL DEFAULT 0,"
                    + "record_type INTEGER NOT NULL DEFAULT 0,"
                    + "UNIQUE (record_sync_id) ON CONFLICT IGNORE)"
            );

            // 创建分类表
            database.execSQL("CREATE TABLE category ("
                    + "category_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "category_unique_name TEXT NOT NULL,"
                    + "category_name TEXT,"
                    + "category_icon TEXT NOT NULL,"
                    + "category_order INTEGER NOT NULL DEFAULT(0),"
                    + "category_type INTEGER NOT NULL DEFAULT(0),"
                    + "category_account_id INTEGER NOT NULL DEFAULT(0),"
                    + "category_sync_status INTEGER NOT NULL DEFAULT(0),"
                    + "UNIQUE (category_unique_name) ON CONFLICT IGNORE)");

            database.execSQL("CREATE UNIQUE INDEX index_record_record_sync_id on record(record_sync_id)");
            database.execSQL("CREATE UNIQUE INDEX index_category_category_unique_name on category(category_unique_name)");

            // 同步记录表数据。新加的 record_category_unique_name 使用 category_icon 填充
            database.execSQL("INSERT INTO record (" +
                    "record_id,record_category_unique_name,record_amount,record_desc,record_time,record_account_id,record_sync_id,record_sync_status,record_delete,record_type) SELECT " +
                    "expense_id,category_icon,expense_amount,expense_desc,expense_time,expense_account_id,expense_sync_id,expense_synced,0,0 " +
                    "FROM expense040 LEFT OUTER JOIN category040 ON expense040.category_id=category040.category_id");

            // 同步分类表数据。 0.6.0 版本之前没有 category_unique_name，使用 categoryIcon 字段填充
            database.execSQL("INSERT INTO category (" +
                    "category_id,category_name,category_unique_name,category_icon,category_order) SELECT " +
                    "category_id,category_name,category_icon,category_icon,category_order FROM category040");

            // 插入默认的收入分类
            Context context = MineApp.getAppContext();
            List<CategoryItem> incomeCategoryList = new ArrayList<>();
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_OTHER_IN_COME, context.getString(R.string.tyOther), CategoryIconHelper.IC_NAME_OTHER));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_XIN_ZI, context.getString(R.string.tyIncomeSalary), CategoryIconHelper.IC_NAME_XIN_ZI));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_JIANG_JIN, context.getString(R.string.tyIncomeReward), CategoryIconHelper.IC_NAME_JIANG_JIN));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_JIE_RU, context.getString(R.string.tyIncomeLend), CategoryIconHelper.IC_NAME_JIE_RU));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_SHOU_ZHAI, context.getString(R.string.tyIncomeDun), CategoryIconHelper.IC_NAME_SHOU_ZHAI));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_LI_XIN_SHOU_RU, context.getString(R.string.tyIncomeInterest), CategoryIconHelper.IC_NAME_LI_XIN_SHOU_RU));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_TOU_ZI_HUI_SHOU, context.getString(R.string.tyIncomeInvestRecovery), CategoryIconHelper.IC_NAME_TOU_ZI_HUI_SHOU));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_TOU_ZI_SHOU_YI, context.getString(R.string.tyIncomeInvestProfit), CategoryIconHelper.IC_NAME_TOU_ZI_SHOU_YI));
            incomeCategoryList.add(incomeCategory(CategoryContant.NAME_YI_WAI_SUO_DE, context.getString(R.string.tyIncomeUnexpected), CategoryIconHelper.IC_NAME_YI_WAI_SUO_DE));

            database.beginTransaction();
            for (CategoryItem categoryIncome : incomeCategoryList) {
                ContentValues values = new ContentValues(4);
                values.put("category_unique_name", categoryIncome.uniqueName);
                values.put("category_name", categoryIncome.name);
                values.put("category_icon", categoryIncome.icon);
                values.put("category_type", categoryIncome.type);
                database.insert("category", SQLiteDatabase.CONFLICT_IGNORE, values);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    };

    private static class CategoryItem {
        private String uniqueName;
        private String name = "";
        private String icon = "";
        private int type;

        CategoryItem(String uniqueName, String name, String icon) {
            this.uniqueName = uniqueName;
            this.name = name;
            this.icon = icon;
        }
    }

    private static CategoryItem expenseCategory(String uniqueName, String name, String icon) {
        CategoryItem categoryItem = new CategoryItem(uniqueName, name, icon);
        categoryItem.type = CategoryEntity.TYPE_EXPENSE;
        return categoryItem;
    }

    private static CategoryItem incomeCategory(String uniqueName, String name, String icon) {
        CategoryItem categoryItem = new CategoryItem(uniqueName, name, icon);
        categoryItem.type = CategoryEntity.TYPE_INCOME;
        return categoryItem;
    }
}

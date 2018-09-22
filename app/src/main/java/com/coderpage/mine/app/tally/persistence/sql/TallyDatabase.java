package com.coderpage.mine.app.tally.persistence.sql;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteQueryBuilder;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.coderpage.mine.MineApp;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.CategoryContant;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.persistence.sql.dao.CategoryDao;
import com.coderpage.mine.app.tally.persistence.sql.dao.ExpenseDao;
import com.coderpage.mine.app.tally.persistence.sql.dao.IncomeDao;
import com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity;
import com.coderpage.mine.app.tally.persistence.sql.entity.ExpenseEntity;
import com.coderpage.mine.app.tally.persistence.sql.entity.InComeEntity;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-05-20 15:28
 * @since 0.6.0
 */

@Database(entities = {ExpenseEntity.class, InComeEntity.class, CategoryEntity.class}, version = 60, exportSchema = false)
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
     * 支出表
     *
     * @return 支出表操作
     */
    public abstract ExpenseDao expenseDao();

    /**
     * 收入表
     *
     * @return 收入表操作
     */
    public abstract IncomeDao incomeDao();

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
                            .allowMainThreadQueries()
                            .build();
                    sInstance.initDatabase();
                }
            }
        }
        return sInstance;
    }

    private void initDatabase() {

    }

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

            // 创建支出表
            database.execSQL("CREATE TABLE expense ("
                    + "expense_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "expense_category_id INTEGER NOT NULL,"
                    // 分类-唯一名称
                    + "expense_category_unique_name TEXT,"
                    + "expense_amount REAL NOT NULL,"
                    + "expense_desc TEXT NOT NULL DEFAULT '',"
                    + "expense_time INTEGER NOT NULL,"
                    + "expense_account_id INTEGER NOT NULL DEFAULT 0,"
                    + "expense_sync_id TEXT NOT NULL,"
                    + "expense_sync_status INTEGER NOT NULL DEFAULT 0,"
                    + "expense_delete INTEGER NOT NULL DEFAULT 0,"
                    + "UNIQUE (expense_sync_id) ON CONFLICT IGNORE)"
            );

            // 创建收入表
            database.execSQL("CREATE TABLE income ("
                    + "income_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "income_category_id INTEGER NOT NULL,"
                    + "income_category_unique_name TEXT,"
                    + "income_amount REAL NOT NULL,"
                    + "income_desc TEXT NOT NULL DEFAULT '',"
                    + "income_time INTEGER NOT NULL,"
                    + "income_account_id INTEGER NOT NULL DEFAULT 0,"
                    + "income_sync_id TEXT NOT NULL,"
                    + "income_sync_status INTEGER NOT NULL DEFAULT 0,"
                    + "income_delete INTEGER NOT NULL DEFAULT 0,"
                    + "UNIQUE (income_sync_id) ON CONFLICT IGNORE)"
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
                    + "UNIQUE (category_unique_name,category_name) ON CONFLICT IGNORE)");

            database.execSQL("CREATE UNIQUE INDEX index_expense_expense_sync_id on expense(expense_sync_id)");
            database.execSQL("CREATE UNIQUE INDEX index_income_income_sync_id on income(income_sync_id)");
            database.execSQL("CREATE UNIQUE INDEX index_category_category_unique_name on category(category_unique_name)");
            database.execSQL("CREATE UNIQUE INDEX index_category_category_name on category(category_name)");

            // 同步支出表数据
            database.execSQL("INSERT INTO expense (" +
                    "expense_id,expense_category_id,expense_amount,expense_desc,expense_time,expense_account_id,expense_sync_id,expense_sync_status) SELECT " +
                    "expense_id,category_id,expense_amount,expense_desc,expense_time,expense_account_id,expense_sync_id,expense_synced FROM expense040");

            // 同步分类表数据
            database.execSQL("INSERT INTO category (" +
                    "category_id,category_name,category_unique_name,category_icon,category_order) SELECT " +
                    "category_id,category_name,category_icon,category_icon,category_order FROM category040");

            // 插入默认的收入分类
            Context context = MineApp.getAppContext();
            List<CategoryIncome> incomeCategoryList = new ArrayList<>();
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_XIN_ZI, context.getString(R.string.tyIncomeSalary), CategoryIconHelper.IC_NAME_XIN_ZI));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_JIANG_JIN, context.getString(R.string.tyIncomeReward), CategoryIconHelper.IC_NAME_JIANG_JIN));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_JIE_RU, context.getString(R.string.tyIncomeLend), CategoryIconHelper.IC_NAME_JIE_RU));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_SHOU_ZHAI, context.getString(R.string.tyIncomeDun), CategoryIconHelper.IC_NAME_SHOU_ZHAI));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_LI_XIN_SHOU_RU, context.getString(R.string.tyIncomeInterest), CategoryIconHelper.IC_NAME_LI_XIN_SHOU_RU));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_TOU_ZI_HUI_SHOU, context.getString(R.string.tyIncomeInvestRecovery), CategoryIconHelper.IC_NAME_TOU_ZI_HUI_SHOU));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_TOU_ZI_SHOU_YI, context.getString(R.string.tyIncomeInvestProfit), CategoryIconHelper.IC_NAME_TOU_ZI_SHOU_YI));
            incomeCategoryList.add(new CategoryIncome(CategoryContant.NAME_YI_WAI_SUO_DE, context.getString(R.string.tyIncomeUnexpected), CategoryIconHelper.IC_NAME_YI_WAI_SUO_DE));

            database.beginTransaction();
            for (CategoryIncome categoryIncome : incomeCategoryList) {
                ContentValues values = new ContentValues(4);
                values.put("category_unique_name", categoryIncome.uniqueName);
                values.put("category_name", categoryIncome.name);
                values.put("category_icon", categoryIncome.icon);
                values.put("category_type", CategoryEntity.TYPE_INCOME);
                database.insert("category", SQLiteDatabase.CONFLICT_IGNORE, values);
            }
            database.setTransactionSuccessful();
            database.endTransaction();

            // 更新支出表新加字段 category_unique_name
            database.beginTransaction();
            Cursor cursor = database.query(SupportSQLiteQueryBuilder.builder("category")
                    .columns(new String[]{"category_id", "category_unique_name"})
                    .selection("category_type=1", null)
                    .create());
            while (cursor.moveToNext()) {
                long categoryId = cursor.getLong(cursor.getColumnIndex("category_id"));
                String uniqueName = cursor.getString(cursor.getColumnIndex("category_unique_name"));
                database.execSQL("update expense set expense_category_unique_name = '" + uniqueName + "' where expense_category_id=" + categoryId);
            }
            cursor.close();

            database.setTransactionSuccessful();
            database.endTransaction();
        }
    };

    private static class CategoryIncome {
        private String uniqueName;
        private String name = "";
        private String icon = "";

        CategoryIncome(String uniqueName, String name, String icon) {
            this.uniqueName = uniqueName;
            this.name = name;
            this.icon = icon;
        }
    }

}

package com.coderpage.mine.app.tally.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coderpage.mine.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author abner-l. 2017-01-23
 * @since 0.1.0
 */

public class TallyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sql_tally";
    private static final int CURRENT_VERSION = 1;

    private Context mContext;

    interface Tables {
        String CATEGORY = "category";
        String EXPENSE = "expense";

        String EXPENSE_JOIN_CATEGORY = "expense "
                + "LEFT OUTER JOIN category ON expense.category_id=category.category_id";
    }

    public TallyDatabase(Context context) {
        this(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    private TallyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.EXPENSE + " ("
                + TallyContract.Expense._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TallyContract.Expense.CATEGORY_ID + " INTEGER NOT NULL,"
                + TallyContract.Expense.CATEGORY + " TEXT NOT NULL,"
                + TallyContract.Expense.AMOUNT + " DOUBLE NOT NULL,"
                + TallyContract.Expense.DESC + " TEXT NOT NULL DEFAULT '',"
                + TallyContract.Expense.TIME + " DOUBLE NOT NULL)"
        );

        db.execSQL("CREATE TABLE " + Tables.CATEGORY + " ("
                + TallyContract.Category._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TallyContract.Category.NAME + " TEXT NOT NULL,"
                + TallyContract.Category.ICON + " TEXT,"
                + TallyContract.Category.ORDER + " INTEGER NOT NULL DEFAULT(0),"
                + "UNIQUE (" + TallyContract.Category.NAME + ") ON CONFLICT REPLACE)");

        initDb(db);
    }

    private void initDb(SQLiteDatabase db) {
        HashMap<String, String> nameAndIconPairs = new LinkedHashMap<>();
        nameAndIconPairs.put(mContext.getString(R.string.tyOther), mContext.getString(R.string.tyIcOther));
        nameAndIconPairs.put(mContext.getString(R.string.tyFoodAndBeverage), mContext.getString(R.string.tyIcCanYin));
        nameAndIconPairs.put(mContext.getString(R.string.tyTraffic), mContext.getString(R.string.tyIcJiaoTong));
        nameAndIconPairs.put(mContext.getString(R.string.tyShopping), mContext.getString(R.string.tyIcGouWu));
        nameAndIconPairs.put(mContext.getString(R.string.tyClothes), mContext.getString(R.string.tyIcFuShi));
        nameAndIconPairs.put(mContext.getString(R.string.tyDailyNecessities), mContext.getString(R.string.tyIcRiYongPin));
        nameAndIconPairs.put(mContext.getString(R.string.tyEntertainment), mContext.getString(R.string.tyIcYuLe));
        nameAndIconPairs.put(mContext.getString(R.string.tyFoodIngredients), mContext.getString(R.string.tyIcShiCai));
        nameAndIconPairs.put(mContext.getString(R.string.tySnacks), mContext.getString(R.string.tyIcLingShi));
        nameAndIconPairs.put(mContext.getString(R.string.tyTobaccoAnTea), mContext.getString(R.string.tyIcYanJiuCha));
        nameAndIconPairs.put(mContext.getString(R.string.tyStudy), mContext.getString(R.string.tyIcXueXi));
        nameAndIconPairs.put(mContext.getString(R.string.tyMedical), mContext.getString(R.string.tyIcYiLiao));
        nameAndIconPairs.put(mContext.getString(R.string.tyHouse), mContext.getString(R.string.tyIcZhuFang));
        nameAndIconPairs.put(mContext.getString(R.string.tyWaterElectricityCoal), mContext.getString(R.string.tyIcShuiDianMei));
        nameAndIconPairs.put(mContext.getString(R.string.tyCommunication), mContext.getString(R.string.tyIcTongXun));
        nameAndIconPairs.put(mContext.getString(R.string.tyTheFavorPattern), mContext.getString(R.string.tyIcRenQing));

        db.beginTransaction();
        for (Map.Entry<String, String> entry : nameAndIconPairs.entrySet()) {
            ContentValues values = new ContentValues(2);
            values.put(TallyContract.Category.NAME, entry.getKey());
            values.put(TallyContract.Category.ICON, entry.getValue());
            db.insert(Tables.CATEGORY, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}

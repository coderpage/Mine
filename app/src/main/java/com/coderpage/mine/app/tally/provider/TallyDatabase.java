package com.coderpage.mine.app.tally.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;

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

        nameAndIconPairs.put(mContext.getString(R.string.tyOther), CategoryIconHelper.IC_NAME_OTHER);
        nameAndIconPairs.put(mContext.getString(R.string.tyFoodAndBeverage), CategoryIconHelper.IC_NAME_CAN_YIN);
        nameAndIconPairs.put(mContext.getString(R.string.tyTraffic), CategoryIconHelper.IC_NAME_JIAO_TONG);
        nameAndIconPairs.put(mContext.getString(R.string.tyShopping), CategoryIconHelper.IC_NAME_GOU_WU);
        nameAndIconPairs.put(mContext.getString(R.string.tyClothes), CategoryIconHelper.IC_NAME_FU_SHI);
        nameAndIconPairs.put(mContext.getString(R.string.tyDailyNecessities), CategoryIconHelper.IC_NAME_RI_YONG_PIN);
        nameAndIconPairs.put(mContext.getString(R.string.tyEntertainment), CategoryIconHelper.IC_NAME_YU_LE);
        nameAndIconPairs.put(mContext.getString(R.string.tyFoodIngredients), CategoryIconHelper.IC_NAME_SHI_CAI);
        nameAndIconPairs.put(mContext.getString(R.string.tySnacks), CategoryIconHelper.IC_NAME_LING_SHI);
        nameAndIconPairs.put(mContext.getString(R.string.tyTobaccoAnTea), CategoryIconHelper.IC_NAME_YAN_JIU_CHA);
        nameAndIconPairs.put(mContext.getString(R.string.tyStudy), CategoryIconHelper.IC_NAME_XUE_XI);
        nameAndIconPairs.put(mContext.getString(R.string.tyMedical), CategoryIconHelper.IC_NAME_YI_LIAO);
        nameAndIconPairs.put(mContext.getString(R.string.tyHouse), CategoryIconHelper.IC_NAME_ZHU_FANG);
        nameAndIconPairs.put(mContext.getString(R.string.tyWaterElectricityCoal), CategoryIconHelper.IC_NAME_SHUI_DIAN_MEI);
        nameAndIconPairs.put(mContext.getString(R.string.tyCommunication), CategoryIconHelper.IC_NAME_TONG_XUN);
        nameAndIconPairs.put(mContext.getString(R.string.tyTheFavorPattern), CategoryIconHelper.IC_NAME_REN_QING);

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

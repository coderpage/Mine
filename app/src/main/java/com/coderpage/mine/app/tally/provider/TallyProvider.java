package com.coderpage.mine.app.tally.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.coderpage.utils.LogUtils;
import com.coderpage.mine.utils.SelectionBuilder;

import java.util.Arrays;

import static com.coderpage.utils.LogUtils.LOGV;

/**
 * @author abner-l. 2017-02-12
 * @since 0.1.0
 */

public class TallyProvider extends ContentProvider {
    private static final String TAG = LogUtils.makeLogTag(TallyProvider.class);

    private TallyDatabase mOpenHelper;
    private TallyUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new TallyDatabase(getContext());
        mUriMatcher = new TallyUriMatcher();
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        TallyUriEnum tallyUriEnum = mUriMatcher.matchUri(uri);
        return tallyUriEnum.contentType;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        TallyUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        long insertId = -1;
        if (matchingUriEnum.table != null) {
            insertId = db.insertOrThrow(matchingUriEnum.table, null, values);
            notifyChange(uri);
        }
        switch (matchingUriEnum) {
            case EXPENSE:
                return TallyContract.Expense.buildExpenseUri(String.valueOf(insertId));
            case CATEGORY:
                return TallyContract.Category.buildCategoryUri(String.valueOf(insertId));
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ")");
        if (uri == TallyContract.BASE_CONTENT_URI) {
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        TallyUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        LOGV(TAG, "uri=" + uri + " code=" + matchingUriEnum.code + " proj=" +
                Arrays.toString(projection) + " selection=" + selection + " args="
                + Arrays.toString(selectionArgs) + ")");

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (matchingUriEnum) {
            default:
                SelectionBuilder builder = buildExpandedSelection(uri, matchingUriEnum.code);

                boolean distinct = TallyContractHelper.isQueryDistinct(uri);
                Cursor cursor = builder.where(selection, selectionArgs)
                        .query(db, distinct, projection, sortOrder, null);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        TallyUriEnum matchingUriEnum = mUriMatcher.matchCode(match);
        if (matchingUriEnum == null) {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        switch (matchingUriEnum) {
            case EXPENSE:
                return builder.table(TallyDatabase.Tables.EXPENSE_JOIN_CATEGORY);
            case CATEGORY:
                return builder.table(TallyDatabase.Tables.CATEGORY);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        TallyUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        switch (matchingUriEnum) {
            case EXPENSE:
                return builder.table(matchingUriEnum.table);
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    private void deleteDatabase() {
        // TODO: wait for content provider operations to finish, then tear down
        mOpenHelper.close();
        Context context = getContext();
        TallyDatabase.deleteDatabase(context);
        mOpenHelper = new TallyDatabase(getContext());
    }
}

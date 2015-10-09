package com.doronzehavi.castawake.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.doronzehavi.castawake.LogUtils;
import com.doronzehavi.castawake.data.AlarmContract.AlarmEntry;
import com.doronzehavi.castawake.data.AlarmContract.AlarmInstance;

/**
 * Content provider for com.doronzehavi.castawake.CastAwake alarms
 */
public class AlarmsProvider extends ContentProvider {
    // Used for query matching
    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    private static final int INSTANCES = 3;
    private static final int INSTANCES_ID = 4;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AlarmContract.AUTHORITY, "alarms", ALARMS);
        sUriMatcher.addURI(AlarmContract.AUTHORITY, "alarms/#", ALARMS_ID);
        sUriMatcher.addURI(AlarmContract.AUTHORITY, "instances", INSTANCES);
        sUriMatcher.addURI(AlarmContract.AUTHORITY, "instances/#", INSTANCES_ID);
    }

    private AlarmsDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new AlarmsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case ALARMS:
                qb.setTables(AlarmEntry.TABLE_NAME);
                break;
            case ALARMS_ID:
                qb.setTables(AlarmEntry.TABLE_NAME);
                qb.appendWhere(AlarmEntry._ID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
            case INSTANCES:
                qb.setTables(AlarmInstance.TABLE_NAME);
                break;
            case INSTANCES_ID:
                qb.setTables(AlarmInstance.TABLE_NAME);
                qb.appendWhere(AlarmInstance._ID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);

        if (ret == null) {
            LogUtils.e("Alarms.query: failed");
        } else {
            // Register cursor to observe changes
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case ALARMS:
                return AlarmEntry.CONTENT_TYPE;
            case ALARMS_ID:
                return AlarmEntry.CONTENT_ITEM_TYPE;
            case INSTANCES:
                return AlarmInstance.CONTENT_TYPE;
            case INSTANCES_ID:
                return AlarmInstance.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case ALARMS:
                rowId = db.insert(AlarmEntry.TABLE_NAME, null, values);
                break;
            case INSTANCES:
                rowId = db.insert(AlarmInstance.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert from URI: " + uri);
        }
        Uri uriResult = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(uriResult, null);
        return uriResult;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        String primaryKey;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case ALARMS:
                count = db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ALARMS_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)){
                    selection = AlarmEntry._ID + "=" + primaryKey;
                } else {
                    selection = AlarmEntry._ID + "=" + primaryKey +
                            " AND (" + selection + ")";
                }
                count = db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INSTANCES:
                count = db.delete(AlarmInstance.TABLE_NAME, selection, selectionArgs);
                break;
            case INSTANCES_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = AlarmInstance._ID + "=" + primaryKey;
                } else {
                    selection = AlarmInstance._ID + "=" + primaryKey +
                            " AND (" + selection + ")";
                }
                count = db.delete(AlarmInstance.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        String alarmId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case ALARMS_ID:
                alarmId = uri.getLastPathSegment();
                count = db.update(AlarmEntry.TABLE_NAME, values,
                        AlarmEntry._ID + "=" + alarmId,
                        null);
                break;
            case INSTANCES_ID:
                alarmId = uri.getLastPathSegment();
                count = db.update(AlarmInstance.TABLE_NAME, values,
                        AlarmInstance._ID + "=" + alarmId,
                        null);
                break;
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + uri);
            }
        }
        LogUtils.v("*** notifyChange() id: " + alarmId + " uri " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

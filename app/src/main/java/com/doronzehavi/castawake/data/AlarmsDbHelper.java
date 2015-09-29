package com.doronzehavi.castawake.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.doronzehavi.castawake.LogUtils;
import com.doronzehavi.castawake.data.AlarmContract.AlarmEntry;
import com.doronzehavi.castawake.data.AlarmContract.AlarmInstance;


public class AlarmsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // This creates a default alarm at 8:30 for every Mon,Tue,Wed,Thu,Fri
    private static final String DEFAULT_ALARM_1 = "(8, 30, 31, 0, 0, '', NULL, 0);";

    // This creates a default alarm at 9:30 for every Sat,Sun
    private static final String DEFAULT_ALARM_2 = "(9, 00, 96, 0, 0, '', NULL, 0);";


    // Database and table names
    public static final String DATABASE_NAME = "alarms.db";

    public AlarmsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static void createAlarmsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                AlarmEntry.HOUR + " INTEGER NOT NULL, " +
                AlarmEntry.MINUTES + " INTEGER NOT NULL, " +
                AlarmEntry.DAYS_OF_WEEK + " INTEGER NOT NULL, " +
                AlarmEntry.ENABLED + " INTEGER NOT NULL, " +
                AlarmEntry.VIBRATE + " INTEGER NOT NULL, " +
                AlarmEntry.LABEL + " TEXT NOT NULL, " +
                AlarmEntry.RINGTONE + " TEXT, " +
                AlarmEntry.DELETE_AFTER_USE + " INTEGER NOT NULL DEFAULT 0);");
        LogUtils.i("Alarms Table created");
    }

    private static void createInstanceTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AlarmInstance.TABLE_NAME + " (" +
                AlarmInstance._ID + " INTEGER PRIMARY KEY," +
                AlarmInstance.YEAR + " INTEGER NOT NULL, " +
                AlarmInstance.MONTH + " INTEGER NOT NULL, " +
                AlarmInstance.DAY + " INTEGER NOT NULL, " +
                AlarmInstance.HOUR + " INTEGER NOT NULL, " +
                AlarmInstance.MINUTES + " INTEGER NOT NULL, " +
                AlarmInstance.VIBRATE + " INTEGER NOT NULL, " +
                AlarmInstance.LABEL + " TEXT NOT NULL, " +
                AlarmInstance.RINGTONE + " TEXT, " +
                AlarmInstance.ALARM_STATE + " INTEGER NOT NULL, " +
                AlarmInstance.ALARM_ID + " INTEGER REFERENCES " +
                AlarmEntry.TABLE_NAME + "(" + AlarmEntry._ID + ") " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");
        LogUtils.i("Instance table created");
    }
    
    private static void insertDefaultAlarms(SQLiteDatabase db) {
        // insert default alarms
        LogUtils.i("Inserting default alarms");
        String cs = ", "; //comma and space
        String insertMe = "INSERT INTO " + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry.HOUR + cs +
                AlarmEntry.MINUTES + cs +
                AlarmEntry.DAYS_OF_WEEK + cs +
                AlarmEntry.ENABLED + cs +
                AlarmEntry.VIBRATE + cs +
                AlarmEntry.LABEL + cs +
                AlarmEntry.RINGTONE + cs +
                AlarmEntry.DELETE_AFTER_USE + ") VALUES ";
        db.execSQL(insertMe + DEFAULT_ALARM_1);
        db.execSQL(insertMe + DEFAULT_ALARM_2);
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        createAlarmsTable(db);
        createInstanceTable(db);
        insertDefaultAlarms(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * No upgrading at this time...
         */
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmInstance.TABLE_NAME);
        onCreate(db);
    }
}

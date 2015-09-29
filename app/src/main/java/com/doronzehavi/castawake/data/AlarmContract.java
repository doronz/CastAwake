package com.doronzehavi.castawake.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.doronzehavi.castawake.Constants;

/**
 * Defines table and column names for the alarms database.
 * <p/>
 * The {@link AlarmEntry} table holds the user created alarms.
 * The {@link AlarmInstance} table holds the state of each alarm in the
 * AlarmsColumns table.
 */
public final class AlarmContract {
    public static final String AUTHORITY = Constants.AUTHORITY;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_ALARM = "alarms";
    public static final String PATH_INSTANCE = "instances";



    private AlarmContract() {
    }

    public static Uri buildAlarmUri() {
        return AlarmContract.BASE_CONTENT_URI.buildUpon().appendPath(AlarmContract.PATH_ALARM).build();
    }

    public interface AlarmSettingColumns extends BaseColumns {
        /**
         * This string is used to indicate no ringtone.
         */
        public static final Uri NO_RINGTONE_URI = Uri.EMPTY;

        /**
         * This string is used to indicate no ringtone.
         */
        public static final String NO_RINGTONE = NO_RINGTONE_URI.toString();

        /**
         * True if alarm should vibrate
         * <p>Type: BOOLEAN</p>
         */
        public static final String VIBRATE = "vibrate";

        /**
         * Alarm label.
         * <p/>
         * <p>Type: STRING</p>
         */
        public static final String LABEL = "label";

        /**
         * Audio alert to play when alarm triggers. Null entry
         * means use system default and entry that equal
         * Uri.EMPTY.toString() means no ringtone.
         * <p/>
         * <p>Type: STRING</p>
         */
        public static final String RINGTONE = "ringtone";
    }

    public interface AlarmEntry extends AlarmSettingColumns, BaseColumns {
        public static final int ID_INDEX = 0;
        public static final int HOUR_INDEX = 1;
        public static final int MINUTES_INDEX = 2;
        public static final int DAYS_OF_WEEK_INDEX = 3;
        public static final int ENABLED_INDEX = 4;
        public static final int VIBRATE_INDEX = 5;
        public static final int LABEL_INDEX = 6;
        public static final int RINGTONE_INDEX = 7;
        public static final int DELETE_AFTER_USE_INDEX = 8;

        public static final String TABLE_NAME = "alarms";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ALARM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ALARM;



        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/alarms");

        /**
         * Hour in 24-hour localtime 0 - 23.
         * <p>Type: INTEGER</p>
         */
        public static final String HOUR = "hour";

        /**
         * Minutes in localtime 0 - 59.
         * <p>Type: INTEGER</p>
         */
        public static final String MINUTES = "minutes";

        /**
         * Days of the week encoded as a bit set.
         * <p>Type: INTEGER</p>
         */
        public static final String DAYS_OF_WEEK = "daysofweek";

        /**
         * True if alarm is active.
         * <p>Type: BOOLEAN</p>
         */
        public static final String ENABLED = "enabled";

        /**
         * Determine if alarm is deleted after it has been used.
         * <p>Type: INTEGER</p>
         */
        public static final String DELETE_AFTER_USE = "delete_after_use";
    }

    /**
     * Constants for the Instance table, which contains the state of each alarm.
     */
    public interface AlarmInstance extends AlarmSettingColumns, BaseColumns {

        public static final String TABLE_NAME = "instances";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_INSTANCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_INSTANCE;

        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/instances");

        /**
         * Alarm state when to show no notification.
         * <p/>
         * Can transitions to:
         * LOW_NOTIFICATION_STATE
         */
        public static final int SILENT_STATE = 0;

        /**
         * Alarm state to show low priority alarm notification.
         * <p/>
         * Can transitions to:
         * HIDE_NOTIFICATION_STATE
         * HIGH_NOTIFICATION_STATE
         * DISMISSED_STATE
         */
        public static final int LOW_NOTIFICATION_STATE = 1;

        /**
         * Alarm state to hide low priority alarm notification.
         * <p/>
         * Can transitions to:
         * HIGH_NOTIFICATION_STATE
         */
        public static final int HIDE_NOTIFICATION_STATE = 2;

        /**
         * Alarm state to show high priority alarm notification.
         * <p/>
         * Can transitions to:
         * DISMISSED_STATE
         * FIRED_STATE
         */
        public static final int HIGH_NOTIFICATION_STATE = 3;

        /**
         * Alarm state when alarm is in snooze.
         * <p/>
         * Can transitions to:
         * DISMISSED_STATE
         * FIRED_STATE
         */
        public static final int SNOOZE_STATE = 4;

        /**
         * Alarm state when alarm is being fired.
         * <p/>
         * Can transitions to:
         * DISMISSED_STATE
         * SNOOZED_STATE
         * MISSED_STATE
         */
        public static final int FIRED_STATE = 5;

        /**
         * Alarm state when alarm has been missed.
         * <p/>
         * Can transitions to:
         * DISMISSED_STATE
         */
        public static final int MISSED_STATE = 6;

        /**
         * Alarm state when alarm is done.
         */
        public static final int DISMISSED_STATE = 7;

        /**
         * Alarm year.
         * <p/>
         * <p>Type: INTEGER</p>
         */
        public static final String YEAR = "year";

        /**
         * Alarm month in year.
         * <p/>
         * <p>Type: INTEGER</p>
         */
        public static final String MONTH = "month";

        /**
         * Alarm day in month.
         * <p/>
         * <p>Type: INTEGER</p>
         */
        public static final String DAY = "day";

        /**
         * Alarm hour in 24-hour localtime 0 - 23.
         * <p>Type: INTEGER</p>
         */
        public static final String HOUR = "hour";

        /**
         * Alarm minutes in localtime 0 - 59
         * <p>Type: INTEGER</p>
         */
        public static final String MINUTES = "minutes";

        /**
         * Foreign key to Alarms table
         * <p>Type: INTEGER (long)</p>
         */
        public static final String ALARM_ID = "alarm_id";

        /**
         * Alarm state
         * <p>Type: INTEGER</p>
         */
        public static final String ALARM_STATE = "alarm_state";
    }

}

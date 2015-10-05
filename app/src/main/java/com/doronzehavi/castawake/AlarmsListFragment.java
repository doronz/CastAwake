package com.doronzehavi.castawake;


import android.app.Fragment;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.doronzehavi.castawake.data.Alarm;
import com.doronzehavi.castawake.data.AlarmContract;
import com.doronzehavi.castawake.data.AlarmContract.AlarmEntry;
import com.doronzehavi.castawake.data.AlarmInstance;
import com.doronzehavi.castawake.data.AlarmStateManager;
import com.doronzehavi.castawake.data.AlarmsAdapter;

import java.util.Calendar;

public class AlarmsListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, TimePickerDialog.OnTimeSetListener {

    private static final int ALARMS_LOADER = 0;
    private AlarmsAdapter mAlarmAdapter;
    private FloatingActionButton mFab;

    public static Fragment newInstance() {
        return new AlarmsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d("App started and fragment created");
        mAlarmAdapter = new AlarmsAdapter(getActivity(), null, 0);
        View v = inflater.inflate(R.layout.fragment_alarms_list, container, false);
        ListView listView = (ListView) v.findViewById(R.id.alarms_list);
        listView.setAdapter(mAlarmAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                asyncDeleteAlarm(new Alarm(cursor));
                return true;
            }
        });

        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingAlarm();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ALARMS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri allAlarmEntries = AlarmContract.buildAlarmUri();
        /**
         * The default sort order for this table
         */
        String sortOrder =
                AlarmEntry.HOUR + ", " +
                AlarmEntry.MINUTES + " ASC" + ", " +
                AlarmEntry._ID + " DESC";

        return new CursorLoader(getActivity(),
                allAlarmEntries,
                null, null, null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAlarmAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmAdapter.swapCursor(null);
    }

    private void startCreatingAlarm(){
        AlarmUtils.showTimeEditDialog(this, null);
    }

    private void asyncAddAlarm(final Alarm alarm){
        final Context context = getActivity().getApplicationContext();
        final AsyncTask<Void, Void, AlarmInstance> addAlarmTask =
                new AsyncTask<Void, Void, AlarmInstance>() {
                    @Override
                    protected AlarmInstance doInBackground(Void... params) {
                        if (context != null && alarm != null) {
                            ContentResolver cr = context.getContentResolver();

                            // Add alarm to db
                            Alarm newAlarm = Alarm.addAlarm(cr, alarm);

                            // Create and add instance to db
                            if (newAlarm.enabled) {
                                return setupAlarmInstance(context, newAlarm);
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(AlarmInstance alarmInstance) {
                        if (alarmInstance != null) {
                            AlarmUtils.popAlarmSetToast(context, alarmInstance.getAlarmTime().getTimeInMillis());
                        }
                    }
                };
        addAlarmTask.execute();
    }
    private void asyncDeleteAlarm(final Alarm alarm) {
        final Context context = getActivity().getApplicationContext();
        final AsyncTask<Void, Void, Void> deleteTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parameters) {
                // Activity may be closed at this point , make sure data is still valid
                if (context != null && alarm != null) {
                    ContentResolver cr = context.getContentResolver();
                    Alarm.deleteAlarm(cr, alarm.id);
                }
                return null;
            }
        };
        deleteTask.execute();
    }

    private static AlarmInstance setupAlarmInstance(Context context, Alarm alarm) {
        ContentResolver cr = context.getContentResolver();
        AlarmInstance newInstance = alarm.createInstanceAfter(Calendar.getInstance());
        newInstance = AlarmInstance.addInstance(cr, newInstance);
        // Register instance to state manager
        AlarmStateManager.registerInstance(context, newInstance, true);
        return newInstance;
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Alarm a = new Alarm();
        a.alert = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
                RingtoneManager.TYPE_ALARM);
        if (a.alert == null) {
            a.alert = Uri.parse("content://settings/system/alarm_alert");
        }
        a.hour = hourOfDay;
        a.minutes = minute;
        a.enabled = true;
        asyncAddAlarm(a);
    }
}

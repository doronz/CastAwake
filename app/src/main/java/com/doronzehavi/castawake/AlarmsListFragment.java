package com.doronzehavi.castawake;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.doronzehavi.castawake.data.Alarm;
import com.doronzehavi.castawake.data.AlarmContract;
import com.doronzehavi.castawake.data.AlarmContract.AlarmEntry;
import com.doronzehavi.castawake.data.AlarmInstance;
import com.doronzehavi.castawake.data.AlarmsAdapter;

import java.util.Calendar;

public class AlarmsListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ALARMS_LOADER = 0;
    private AlarmsAdapter mAlarmAdapter;
    private FloatingActionButton mFab;

    public static Fragment newInstance() {
        return new AlarmsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAlarmAdapter = new AlarmsAdapter(getActivity(), null, 0);
        View v = inflater.inflate(R.layout.fragment_alarms_list, container, false);
        ListView listView = (ListView) v.findViewById(R.id.alarms_list);
        listView.setAdapter(mAlarmAdapter);
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncAddAlarm(new Alarm());
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

    private static AlarmInstance setupAlarmInstance(Context context, Alarm alarm) {
        ContentResolver cr = context.getContentResolver();
        AlarmInstance newInstance = alarm.createInstanceAfter(Calendar.getInstance());
        newInstance = AlarmInstance.addInstance(cr, newInstance);
        return newInstance;
    }
}

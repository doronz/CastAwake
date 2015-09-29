package com.doronzehavi.castawake.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.doronzehavi.castawake.R;
import com.doronzehavi.castawake.data.AlarmContract.AlarmEntry;


public class AlarmsAdapter extends CursorAdapter {

    public AlarmsAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.alarm_item, parent, false);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView time, dayOfWeek, deleteAfterUse;
        Switch alarmSwitch;
        time = (TextView) view.findViewById(R.id.time);
        dayOfWeek = (TextView) view.findViewById(R.id.day_of_week);
        deleteAfterUse = (TextView) view.findViewById(R.id.delete_after_use);
        alarmSwitch = (Switch) view.findViewById(R.id.alarm_switch);

        time.setText(cursor.getInt(AlarmEntry.HOUR_INDEX) + ":" + cursor.getInt(AlarmEntry.MINUTES_INDEX));

    }
}

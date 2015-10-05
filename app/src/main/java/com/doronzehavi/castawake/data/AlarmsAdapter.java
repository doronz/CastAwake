package com.doronzehavi.castawake.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.doronzehavi.castawake.AlarmUtils;
import com.doronzehavi.castawake.R;

import java.util.Calendar;

public class AlarmsAdapter extends CursorAdapter {

    public AlarmsAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.alarm_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setTag(vh); // Used to provide access to viewholder in bindView()
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        int hours = cursor.getInt(AlarmContract.AlarmEntry.HOUR_INDEX);
        int minutes = cursor.getInt(AlarmContract.AlarmEntry.MINUTES_INDEX);
        boolean enabled = cursor.getInt(AlarmContract.AlarmEntry.ENABLED_INDEX) == 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        vh.time.setText(AlarmUtils.getFormattedTime(context, cal));
        vh.alarmSwitch.setChecked(enabled);
    }


    // Viewholder pattern
    private static class ViewHolder {
        TextView time, dayOfWeek, deleteAfterUse;
        Switch alarmSwitch;
        public ViewHolder(View view) {
            time = (TextView) view.findViewById(R.id.time);
            dayOfWeek = (TextView) view.findViewById(R.id.day_of_week);
            deleteAfterUse = (TextView) view.findViewById(R.id.delete_after_use);
            alarmSwitch = (Switch) view.findViewById(R.id.alarm_switch);
        }
    }
}
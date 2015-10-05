package com.doronzehavi.castawake.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.doronzehavi.castawake.AlarmAlertWakeLock;
import com.doronzehavi.castawake.LogUtils;

/**
 * Manages starting and stopping of an alarm instance. Eventually should also
 * handle casting alarm to chromecast.
 */
public class AlarmService extends Service {
    // A public action send by AlarmService when the alarm has started.
    public static final String ALARM_ALERT_ACTION = "com.doronzehavi.castawake.ALARM_ALERT";

    // A public action sent by AlarmService when the alarm has stopped for any reason.
    public static final String ALARM_DONE_ACTION = "com.doronzehavi.castawake.ALARM_DONE";

    // Private action used to start an alarm with this service.
    public static final String START_ALARM_ACTION = "START_ALARM";

    // Private action used to stop an alarm with this service.
    public static final String STOP_ALARM_ACTION = "STOP_ALARM";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(LogUtils.LOGTAG, "AlarmService started.");
        if (START_ALARM_ACTION.equals(intent.getAction())) {
            LogUtils.d(LogUtils.LOGTAG, "Alarm started!");
        }


        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Utility method to help start alarm properly. If alarm is already firing, it
     * will mark it as missed and start the new one.
     *
     * @param context application context
     * @param instance to trigger alarm
     */
    public static void startAlarm(Context context, AlarmInstance instance) {
        Intent intent = AlarmInstance.createIntent(context, AlarmService.class, instance.mId);
        intent.setAction(START_ALARM_ACTION);

        // Maintain a cpu wake lock until the service can get it
        AlarmAlertWakeLock.acquireCpuWakeLock(context);
        context.startService(intent);
    }

    /**
     * Utility method to help stop an alarm properly. Nothing will happen, if alarm is not firing
     * or using a different instance.
     *
     * @param context application context
     * @param instance you are trying to stop
     */
    public static void stopAlarm(Context context, AlarmInstance instance) {
        Intent intent = AlarmInstance.createIntent(context, AlarmService.class, instance.mId);
        intent.setAction(STOP_ALARM_ACTION);

        // We don't need a wake lock here, since we are trying to kill an alarm
        context.startService(intent);
    }

    private void stopCurrentAlarm() {
        sendBroadcast(new Intent(ALARM_DONE_ACTION));
        AlarmAlertWakeLock.releaseCpuLock();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

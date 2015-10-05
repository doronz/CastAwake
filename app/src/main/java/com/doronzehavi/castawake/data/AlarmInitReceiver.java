/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doronzehavi.castawake.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager.WakeLock;

import com.doronzehavi.castawake.AlarmAlertWakeLock;
import com.doronzehavi.castawake.AsyncHandler;
import com.doronzehavi.castawake.LogUtils;

public class AlarmInitReceiver extends BroadcastReceiver {

    // A flag that indicates that switching the volume button default was done
    private static final String PREF_VOLUME_DEF_DONE = "vol_def_done";

    /**
     * Sets alarm on ACTION_BOOT_COMPLETED.  Resets alarm on
     * TIME_SET, TIMEZONE_CHANGED
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        LogUtils.v("AlarmInitReceiver " + action);

        // Allow us to use the broadcast receiver asynchronously on a background thread
        final PendingResult result = goAsync();

        // Keep CPU going while we do this.. screen can be off
        final WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
        wl.acquire();

        // We need to increment the global id out of the async task to prevent
        // race conditions
        AlarmStateManager.updateGlobalIntentId(context);
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {

                // Update all the alarm instances on boot and time change
                AlarmStateManager.fixAlarmInstances(context);

                result.finish();
                LogUtils.v("AlarmInitReceiver finished");
                wl.release();
            }
        });
    }

    private void switchVolumeButtonDefault(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();

/* TODO:
        editor.putString(SettingsActivity.KEY_VOLUME_BEHAVIOR,
            SettingsActivity.DEFAULT_VOLUME_BEHAVIOR);
*/

        // Make sure we do it only once
        editor.putBoolean(PREF_VOLUME_DEF_DONE, true);
        editor.apply();
    }
}

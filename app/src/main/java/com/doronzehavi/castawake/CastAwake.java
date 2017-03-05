package com.doronzehavi.castawake;

import android.app.Application;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;


public class CastAwake extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VideoCastManager.initialize(this, Constants.APP_ID, null, null)
                .enableFeatures(
                        VideoCastManager.FEATURE_DEBUGGING |
                                VideoCastManager.FEATURE_AUTO_RECONNECT |
                                VideoCastManager.FEATURE_WIFI_RECONNECT
                );


    }
}

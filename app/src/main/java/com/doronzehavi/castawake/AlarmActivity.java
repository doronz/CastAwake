package com.doronzehavi.castawake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;


public class AlarmActivity extends AppCompatActivity {
    private VideoCastManager mCastManager;
    private VideoCastManagerHelper mCastManagerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mCastManager = VideoCastManager.getInstance();
        mCastManagerHelper = new VideoCastManagerHelper(this, mCastManager);
        mCastManagerHelper.launchAppIfPossible(10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCastManager = VideoCastManager.getInstance();
        mCastManager.incrementUiCounter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCastManager.decrementUiCounter();
    }
}

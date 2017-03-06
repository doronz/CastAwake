package com.doronzehavi.castawake;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;

public class MainActivity extends AppCompatActivity {

    private final String ALARMS_LIST_FRAGMENT_TAG = "ALFTAG";
    private CastStateListener mCastStateListener;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private IntroductoryOverlay mIntroductoryOverlay;
    private MenuItem mediaRouteMenuItem;
    private SessionManager mSessionManager;
    private SessionManagerListenerImpl mSessionManagerListener = new SessionManagerListenerImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCastContext = CastContext.getSharedInstance(this);

        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AlarmsListFragment.newInstance(), ALARMS_LIST_FRAGMENT_TAG)
                    .commit();
        }
    }

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarted(Session session, String sessionId) {
            invalidateOptionsMenu();
        }
        @Override
        public void onSessionResumed(Session session, boolean wasSuspended) {
            invalidateOptionsMenu();
        }
        @Override
        public void onSessionEnded(Session session, int error) {
            finish();
        }
        @Override
        public void onSessionStarting(Session session) {

        }
        @Override
        public void onSessionStartFailed(Session session, int i) {
        }
        @Override
        public void onSessionEnding(Session session) {
        }
        @Override
        public void onSessionResumeFailed(Session session, int i) {
        }
        @Override
        public void onSessionSuspended(Session session, int i) {
        }
        @Override
        public void onSessionResuming(Session session, String s) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu_item:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                || super.dispatchKeyEvent(event);
    }


    @Override
    protected void onResume() {
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;

    }

    @Override
    public void onStop() {
        super.onStop();
    }



    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(CastSession castSession, int i) {

        }


        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {

        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {

        }

        @Override
        public void onSessionStarting(CastSession castSession) {

        }


        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {

        }

        @Override
        public void onSessionEnding(CastSession castSession) {

        }


        @Override
        public void onSessionResuming(CastSession castSession, String s) {

        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {

        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {

        }
    }
}



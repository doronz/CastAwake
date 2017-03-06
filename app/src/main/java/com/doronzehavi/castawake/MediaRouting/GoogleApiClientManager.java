package com.doronzehavi.castawake.MediaRouting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.media.MediaRouter;

import com.doronzehavi.castawake.Constants;
import com.doronzehavi.castawake.LogUtils;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;


public class GoogleApiClientManager {

    private final Context mContext;
    private ApiConnectionCallbacks mConnectionCallbacks;
    private ApiConnectionFailedListener mConnectionFailedListener;
    private Cast.Listener mCastListener;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private String mSessionId;
    private boolean mApplicationStarted;
    private boolean mWaitingForReconnect;
    private MediaRouter mMediaRouter;
    SharedPreferences mPrefs;

    public GoogleApiClientManager(Context context, MediaRouter router) {
        mContext = context;
        mMediaRouter = router;
        mPrefs = mContext.getSharedPreferences(Constants.ROUTE_PREF, Context.MODE_PRIVATE);

    }

    public void setSelectedDevice(MediaRouter.RouteInfo routeInfo) {
        if (routeInfo != null) saveSelectedDevice(routeInfo);
    }

    /**
     * Start the receiver app
     */
    public void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    LogUtils.d("application has stopped");
                    teardown(true);
                }

            };

            // Connect to Google Play services
            mConnectionCallbacks = new ApiConnectionCallbacks();
            mConnectionFailedListener = new ApiConnectionFailedListener();

            if (mSelectedDevice == null) {
                mSelectedDevice = loadSelectedDevice();
            }

            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            LogUtils.e("Failed launchReceiver", e);
        }
    }

    /**
     * Tear down the connection to the receiver
     */
    public void teardown(boolean selectDefaultRoute) {
        LogUtils.d("teardown");
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    Cast.CastApi.stopApplication(mApiClient, mSessionId);
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        if (selectDefaultRoute) {
            mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;
    }

    private void saveSelectedDevice(MediaRouter.RouteInfo routeInfo) {
        mSelectedDevice = CastDevice.getFromBundle(routeInfo.getExtras());


        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(routeInfo.getExtras());
        prefsEditor.putString(Constants.SELECTED_DEVICE_PREF, json);
        prefsEditor.commit();
    }

    public CastDevice loadSelectedDevice() {
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.SELECTED_DEVICE_PREF, "");
        CastDevice device = gson.fromJson(json, CastDevice.class);
        return device;
    }

    private class ApiConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {
        private boolean mWaitingForReconnect;

        @Override
        public void onConnected(Bundle connectionHint) {
            LogUtils.d("onConnected");

            if (mApiClient == null) {
                // We got disconnected while this runnable was pending
                // execution.
                return;
            }
            try {
                if (mWaitingForReconnect) {
                    mWaitingForReconnect = false;
                    // Check if the receiver app is still running
                    if ((connectionHint != null)
                            && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                        LogUtils.d("App  is no longer running");
                        teardown(true);
                    } else {
                        // Todo: Create/re-create custom message channel
                    }
                } else {
                    // Launch the receiver app
                    Cast.CastApi.launchApplication(mApiClient, Constants.APP_ID, false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(
                                                Cast.ApplicationConnectionResult result) {
                                            Status status = result.getStatus();
                                            LogUtils.d(
                                                    "ApplicationConnectionResultCallback.onResult:"
                                                            + status.getStatusCode());
                                            if (status.isSuccess()) {
                                                ApplicationMetadata applicationMetadata = result
                                                        .getApplicationMetadata();
                                                mSessionId = result.getSessionId();
                                                String applicationStatus = result
                                                        .getApplicationStatus();
                                                boolean wasLaunched = result.getWasLaunched();
                                                LogUtils.d("application name: "
                                                        + applicationMetadata.getName()
                                                        + ", status: " + applicationStatus
                                                        + ", sessionId: " + mSessionId
                                                        + ", wasLaunched: " + wasLaunched);
                                                mApplicationStarted = true;

                                            } else {
                                                LogUtils.e("application could not launch");
                                                teardown(true);
                                            }
                                        }
                                    });
                }
            } catch (Exception e) {
                LogUtils.e("Failed to launch application", e);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            LogUtils.d("onConnectionSuspended");
            mWaitingForReconnect = true;
        }
    }

    private class ApiConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            teardown(false);
        }
    }

}

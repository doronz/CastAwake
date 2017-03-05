package com.doronzehavi.castawake;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.media.MediaRouter;

import com.google.android.libraries.cast.companionlibrary.cast.CastMediaRouterCallback;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.utils.PreferenceAccessor;

import java.util.List;


/**
 * Helps with the logic of video casting
 */
public class VideoCastManagerHelper extends CastMediaRouterCallback {
    private AsyncTask<Void, Integer, Boolean> mConnectionTask;

    private VideoCastManager mVideoCastManager;
    private PreferenceAccessor mPreferenceAccessor;
    private MediaRouter mMediaRouter;
    private MediaRouter.RouteInfo mRoute;

    public VideoCastManagerHelper(Context context, VideoCastManager manager) {
        super(manager);
        mVideoCastManager = manager;
        mPreferenceAccessor = new PreferenceAccessor(context);
        mMediaRouter = MediaRouter.getInstance(context);
        mVideoCastManager.addVideoCastConsumer(new VideoCastConsumerImpl() {
            @Override
            public void onConnected() {
                super.onConnected();
                LogUtils.d("onConnected()");


            }
        });
    }

    public void launchAppIfPossible(final int timeoutInSeconds) {
        LogUtils.d(String.format("launchAppIfPossible(%d)", timeoutInSeconds));
        final String routeId = mPreferenceAccessor.getStringFromPreference(
                SettingsActivity.KEY_DEVICE_PREF);

        if (routeId == null)
            return;

        // cancel any prior reconnection task
        if (mConnectionTask != null && !mConnectionTask.isCancelled()) {
            mConnectionTask.cancel(true);
        }

        mConnectionTask = new AsyncTask<Void, Integer, Boolean>() {
            List<MediaRouter.RouteInfo> routes;
            MediaRouter.RouteInfo theRoute = null;
            @Override
            protected void onPreExecute() {
                // See if our routes have been updated
                routes = mMediaRouter.getRoutes();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                LogUtils.d("Attempting to launch app.");
                if (mVideoCastManager.isConnected()) {
                    return true;
                }
                try {
                    if (routes != null) {
                        for (MediaRouter.RouteInfo route : routes) {
                            if (route.getId().equals(routeId)) {
                                theRoute = route;
                                break;
                            }
                        }
                    }
                    if (theRoute != null) {
                        // Route has been found and we will connect to it now
                        return true;
                    } else {
                        LogUtils.e("Route is not yet available.");
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result || timeoutInSeconds <= 0) {
                    if (result == null || !result) {
                        LogUtils.e("Could not find route, giving up!");
                        mVideoCastManager.onDeviceSelected(null);
                    }
                    else {
                        LogUtils.d("Route is available, attempting to connect!");
                        connectToRoute(theRoute);
                    }
                }
                else {
                    launchAppIfPossible(timeoutInSeconds - 1);
                }
            }
        };
        mConnectionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void connectToRoute(MediaRouter.RouteInfo theRoute) {
        if (mVideoCastManager.isConnected()) {
            LogUtils.d("Already connected to route!");
            return;
        }
        if (theRoute != null) {
            LogUtils.d("Selecting route: " + theRoute.getName());
            mMediaRouter.selectRoute(theRoute);
        }
    }
}

package com.doronzehavi.castawake.MediaRouting;


import android.content.Context;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;

import com.doronzehavi.castawake.Constants;
import com.google.android.gms.cast.CastMediaControlIntent;

public class MediaRouterManager {


    private final MediaRouteSelector mSelector;
    private final MediaRouter mMediaRouter;
    private final MediaRouterCallback mCallback;
    private final Context mContext;
    private GoogleApiClientManager mApiManager;

    public MediaRouterManager(Context context) {
        mContext = context;
        mMediaRouter = MediaRouter.getInstance(mContext.getApplicationContext());

        // Create a route selector for the type of routes your app supports.
        mSelector = new MediaRouteSelector.Builder()
                // These are the framework-supported intents
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .addControlCategory(CastMediaControlIntent.categoryForCast(Constants.APP_ID))
                .build();

        mCallback = new MediaRouterCallback();

        mApiManager = new GoogleApiClientManager(mContext, mMediaRouter);
    }

    public void launchApp() {
        mApiManager.launchReceiver();
    }


    public MediaRouteSelector getSelector() {
        return mSelector;
    }

    public void addCallback() {
        mMediaRouter.addCallback(mSelector, mCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void removeCallback() {
        mMediaRouter.removeCallback(mCallback);
    }

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo route) {
            if (route.getPlaybackType() == RouteInfo.PLAYBACK_TYPE_LOCAL) {
                return;
            }

            mApiManager.setSelectedDevice(route);
            mApiManager.launchReceiver();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo route) {
            //mApiManager.teardown(false);
            //mApiManager.setSelectedDevice(null);
        }
    }



}

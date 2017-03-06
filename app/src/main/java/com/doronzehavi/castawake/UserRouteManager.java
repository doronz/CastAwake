package com.doronzehavi.castawake;


import android.support.v7.media.MediaRouter;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class UserRouteManager {

    private static UserRouteManager sInstance;

    public static UserRouteManager getInstance() {
        if (sInstance == null)
            sInstance = new UserRouteManager();

        return sInstance;
    }

    private UserRouteManager() {}

    /**
     * Saved user routes only. User has previously chosen this route.
     */
    private PriorityQueue<UserRoute> mSavedUserRoutes = new PriorityQueue<>(3, new UserRouteComparator());
    /**
     * Union of available routes and saved routes.
     */
    private PriorityQueue<UserRoute> mRoutes = new PriorityQueue<>(10, new UserRouteComparator());

    public PriorityQueue<UserRoute> updateRoutes(List<MediaRouter.RouteInfo> routes) {
        for (UserRoute route : mSavedUserRoutes) {
            mRoutes.add(route);
        }
        for (MediaRouter.RouteInfo route : routes) {
            if (getExisting(route.getId()) == null){ // Route doesn't already exist
                UserRoute newRoute = new UserRoute(route.getName(), route.getId());
                mRoutes.add(newRoute);
            }
        }
        return mRoutes;
    }

    private UserRoute getExisting(String id) {
        Iterator<UserRoute> it = mSavedUserRoutes.iterator();
        if (id != null) {
            while (it.hasNext()) {
                UserRoute curr = it.next();
                if (id.equals(curr.mRouteId)) {
                    return curr;
                }
            }
        }
        return null;
    }

    private void addRoute(String name, String id) {
        UserRoute newRoute = getExisting(id);
        if (newRoute == null)
            newRoute = new UserRoute(name, id);
        newRoute.markUsed();
        mSavedUserRoutes.add(newRoute);
    }

    public class UserRoute {
        private String mRouteName;

        public String getId() {
            return mRouteId;
        }

        public String getName() {
            return mRouteName;
        }

        private String mRouteId;
        private long mLastUsed;

        public UserRoute(String name, String id){
            mRouteName = name;
            mRouteId = id;
        }

        public UserRoute(MediaRouter.RouteInfo routeInfo) {
            mRouteName = routeInfo.getName();
            mRouteId = routeInfo.getId();
        }

        public void markUsed() {
            mLastUsed = System.currentTimeMillis();
        }
    }

    private class UserRouteComparator implements java.util.Comparator<UserRoute> {
        @Override
        public int compare(UserRoute lhs, UserRoute rhs) {
            if (lhs.mLastUsed > rhs.mLastUsed)
                return -1; // Order the most recent time first
            else if (lhs.mLastUsed < rhs.mLastUsed)
                return 1;
            return 0;
        }
    }
}

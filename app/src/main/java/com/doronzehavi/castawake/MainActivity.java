package com.doronzehavi.castawake;


import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.doronzehavi.castawake.MediaRouting.MediaRouterManager;

public class MainActivity extends AppCompatActivity {

    private final String ALARMS_LIST_FRAGMENT_TAG = "ALFTAG";
    private MediaRouterManager mMediaRouterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaRouterManager = new MediaRouterManager(this);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AlarmsListFragment.newInstance(), ALARMS_LIST_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Attach the MediaRouteSelector to the menu item
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(
                        mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouterManager.getSelector());

        return true;
    }



    @Override
    public void onStart() {
        // Add the callback on start to tell the media router what kinds of routes
        // your app works with so the framework can discover them.
        mMediaRouterManager.addCallback();
        super.onStart();
    }


    @Override
    public void onStop() {
        // Remove the selector on stop to tell the media router that it no longer
        // needs to discover routes for your app.
        mMediaRouterManager.removeCallback();
        super.onStop();
    }
}

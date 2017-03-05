/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.doronzehavi.castawake;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.libraries.cast.companionlibrary.utils.PreferenceAccessor;

import java.util.PriorityQueue;


/**
 * Settings for the Alarm Clock.
 *
 * TODO: Refactor this in the modern way
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    public static final String KEY_DEVICE_PREF = "device_pref";
    PreferenceAccessor mPrefAccess;
    MediaRouter mRouter;
    PriorityQueue<UserRouteManager.UserRoute> mRoutes;
    UserRouteManager mUserRouteManager;
    private ListPreference mDevicePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Implement with preference fragment..
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_bar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.settings);

        mPrefAccess = new PreferenceAccessor(this);
        mRouter = MediaRouter.getInstance(this);
        mUserRouteManager = UserRouteManager.getInstance();

        mDevicePref = (ListPreference)findPreference(KEY_DEVICE_PREF);
        mDevicePref.setOnPreferenceChangeListener(this);

        loadUserRoutes();


    }

    private void loadUserRoutes() {

        mRoutes = mUserRouteManager.updateRoutes(mRouter.getRoutes());

        CharSequence[] routeNames = new CharSequence[mRoutes.size()];
        CharSequence[] routeIds = new CharSequence[mRoutes.size()];
        int initialSize = mRoutes.size();
        for (int i = 0; i < initialSize; i++) {
            UserRouteManager.UserRoute curr = mRoutes.poll();
            routeNames[i] = curr.getName();
            routeIds[i] = curr.getId();
        }


        mDevicePref.setEntries(routeNames);
        mDevicePref.setEntryValues(routeIds);
        updateDevicePrefSummary();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    private void updateDevicePrefSummary() {
        mDevicePref.setSummary(mDevicePref.getEntry());
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        LogUtils.d("onPreferenceChange: " + pref.getKey() + " : " + newValue);
        if (KEY_DEVICE_PREF.equals(pref.getKey())) {
            updateDevicePrefSummary();

            LogUtils.d("KEY_DEVICE_PREF = " + newValue);
        }
        return true;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Exported activity but no headers we support.
        return false;
    }



}

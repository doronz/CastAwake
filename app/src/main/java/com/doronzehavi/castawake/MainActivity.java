package com.doronzehavi.castawake;


import android.app.Fragment;
import android.os.Bundle;

public class MainActivity extends SingleFragmentActivity {

    private final String ALARMS_LIST_FRAGMENT_TAG = "ALFTAG";


    @Override
    protected Fragment createFragment() {
        return AlarmsListFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, createFragment(), ALARMS_LIST_FRAGMENT_TAG)
                    .commit();
        }
    }
}

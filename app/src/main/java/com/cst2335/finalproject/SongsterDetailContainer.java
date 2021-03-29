package com.cst2335.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SongsterDetailContainer extends AppCompatActivity {
    /**
     * Method sets the layout, loads data to the fragment
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_fragment);

        Bundle bundle = getIntent().getExtras();

        SongsterFragment dFragment = new SongsterFragment();
        dFragment.setArguments(bundle); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentLocation, dFragment)
                .commit();
    }
}
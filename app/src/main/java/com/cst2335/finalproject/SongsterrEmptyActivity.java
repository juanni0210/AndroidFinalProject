package com.cst2335.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles inflating of search fragment for songs
 */

public class SongsterrEmptyActivity extends AppCompatActivity {

    /**
     * Create and display Search object fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songster_empty);

        Bundle dataToPass = getIntent().getExtras();

        SongsterrDetailFragment dFragment = new SongsterrDetailFragment();
        dFragment.setArguments(dataToPass);
        dFragment.setTablet(false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.songsterFragmentLocation, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }
}

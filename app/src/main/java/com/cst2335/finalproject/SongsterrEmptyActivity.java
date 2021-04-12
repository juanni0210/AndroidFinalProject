package com.cst2335.finalproject;

import android.os.Bundle;

/**
 * Handles inflating of search fragment for songs
 */
import androidx.appcompat.app.AppCompatActivity;

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
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.songsterFragment, dFragment)
                .commit();
    }
}

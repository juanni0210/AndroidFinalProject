package com.cst2335.finalproject;

import android.os.Bundle;

/**
 * Handles inflating of search fragment for songs
 */
import androidx.appcompat.app.AppCompatActivity;


/**
 * Display fragments layout when user uses phone.
 * @author Xueru Chen
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

        //get the data that was passed from Fragment
        Bundle dataToPass = getIntent().getExtras();

        //This is copied directly from FragmentExample.java lines 47-54
        SongsterrDetailFragment dFragment = new SongsterrDetailFragment();
        dFragment.setArguments(dataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.songsterFragment, dFragment)
                .commit();
    }
}

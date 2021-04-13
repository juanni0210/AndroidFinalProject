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
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
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

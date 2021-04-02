package com.cst2335.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles inflating of favorite fragment for songs
 */
public class SongsterrFavEmpty extends AppCompatActivity {

    /**
     * Create and display Favorite object fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songsterr_fav_empty);

        Bundle dataToPass = getIntent().getExtras();

        SongsterrFavDetailFragment dFragment = new SongsterrFavDetailFragment();
        dFragment.setArguments(dataToPass); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.songsterrFavFragmentLocation, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }
}
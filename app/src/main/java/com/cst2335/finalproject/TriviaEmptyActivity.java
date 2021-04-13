package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * An Activity will display fragments layout when user uses phone.
 * @author Juan Ni
 */
public class TriviaEmptyActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_empty);

        Bundle dataToTriviaFragment = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        TriviaDetailsFragment triviaDFragment = new TriviaDetailsFragment();
        triviaDFragment.setArguments(dataToTriviaFragment ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.triviaFragment, triviaDFragment)
                .commit();
    }
}
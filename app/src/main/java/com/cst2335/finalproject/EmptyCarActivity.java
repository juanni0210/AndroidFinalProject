/**
 * When the user end is mobile phone, when user goes to see
 * details of a car model, he will be directed to this page,
 * this is on a different thread from database
 * @author Sophie Sun
 * @since 1.0
 */

package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmptyCarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_car);
        Bundle dataToPass = getIntent().getExtras();
        CarDetailsFragment dFragment = new CarDetailsFragment(); //add a DetailFragment
        dFragment.setArguments( dataToPass ); //pass it a bundle for information
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
    }
}
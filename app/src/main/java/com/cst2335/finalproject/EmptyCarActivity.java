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
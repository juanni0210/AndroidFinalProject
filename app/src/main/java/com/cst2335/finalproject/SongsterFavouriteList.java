package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SongsterFavouriteList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_favourite_list);

        Button goBackBtn = findViewById(R.id.backButton);
        goBackBtn.setOnClickListener(btn->{

            Intent goBack = new Intent(SongsterFavouriteList.this, SongsterSearch.class);
            startActivity(goBack);

        });



    }
}
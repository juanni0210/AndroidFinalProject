package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Main Activity of the application. User can access to four projects.
 * @author Juan Ni, Sophie Sun, Feiqiong Deng, Xueru Chen
 */
public class MainActivity extends AppCompatActivity {
    private Button startTriviaBtn, startSongsterBtn, startCarDBBtn, startSoccerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTriviaBtn = findViewById(R.id.startTriviaBtn);
        startSongsterBtn = findViewById(R.id.startSongsterBtn);
        startCarDBBtn = findViewById(R.id.startCarDBBtn);
        startSoccerBtn = findViewById(R.id.startSoccerBtn);

        Intent goToTrivaLaunch = new Intent(MainActivity.this, TriviaGameLaunchActivity.class);
        startTriviaBtn.setOnClickListener(v -> startActivity(goToTrivaLaunch));

        Intent goToSonsterLaunch = new Intent(MainActivity.this, SongsterSearch.class);
        startSongsterBtn.setOnClickListener(v -> startActivity(goToSonsterLaunch));

        Intent goToCarDBLaunch = new Intent(MainActivity.this, CarDatabase.class);
        startCarDBBtn.setOnClickListener(v -> startActivity(goToCarDBLaunch));

        Intent goToSoccerLaunch = new Intent(MainActivity.this, RatingSoccerAPI.class);
        startSoccerBtn.setOnClickListener(v -> startActivity(goToSoccerLaunch));
    }
}

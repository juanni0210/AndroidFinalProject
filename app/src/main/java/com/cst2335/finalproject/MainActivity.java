package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;

/**
 * Main Activity of the application. User can access to four projects.
 * @author Juan Ni
 */
public class MainActivity extends AppCompatActivity {
    private Button startTriviaBtn, startSongsterBtn, startCarDBBtn, startSoccerBtn;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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

//        Intent goToSonsterLaunch = new Intent(MainActivity.this, SongsterSearch.class);
//        startSongsterBtn.setOnClickListener(v -> startActivity(goToSonsterLaunch));
//
//        Intent goToCarDBLaunch = new Intent(MainActivity.this, CarDatabase.class);
//        startCarDBBtn.setOnClickListener(v -> startActivity(goToCarDBLaunch));
//
//        Intent goToSoccerLaunch = new Intent(MainActivity.this, SoccerGames.class);
//        startSoccerBtn.setOnClickListener(v -> startActivity(goToSoccerLaunch));
    }
}
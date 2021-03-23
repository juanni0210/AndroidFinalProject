package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button startCarDBBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startCarDBBtn = findViewById(R.id.startCarDB);
        Intent goToCarDB = new Intent(MainActivity.this, CarDatabase.class);
        startCarDBBtn.setOnClickListener(v -> {
            startActivity(goToCarDB);
        });

    }
}
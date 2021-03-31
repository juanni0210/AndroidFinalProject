package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class RatingSoccerAPI extends AppCompatActivity {

    TextView rateCount, showRating;
    EditText review;
    Button submit;
    RatingBar ratingBar;
    float rateValue;
    String temp;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_soccer_a_p_i);

        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        float savedValue = prefs.getFloat("ReserveRating", 5);
        Intent goToSoccer = new Intent(RatingSoccerAPI.this, SoccerGames.class);


        rateCount = findViewById(R.id.rateCount);
        ratingBar = findViewById(R.id.ratingBar);
        review = findViewById(R.id.review);
        submit = findViewById(R.id.submitBtn);
        showRating = findViewById(R.id.showRating);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingBar.getRating();
                if(rateValue <= 1 && rateValue >0)
                    rateCount.setText("Bad " + rateValue + "/5");
                else if(rateValue <= 2 && rateValue > 1)
                    rateCount.setText("OK " + rateValue + "/5");
                else if(rateValue <= 2 && rateValue > 2)
                    rateCount.setText("Good " + rateValue + "/5");
                else if(rateValue <= 2 && rateValue > 3)
                    rateCount.setText("Very Good " + rateValue + "/5");
                else if(rateValue <= 2 && rateValue > 4)
                    rateCount.setText("Best " + rateValue + "/5");
            }
        });
        submit.setOnClickListener(click -> {
            saveSharedPrefs(ratingBar.getRating());
            startActivity(goToSoccer);
        });
    }

    private void saveSharedPrefs(float valueToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("ReserveRating", valueToSave);
        editor.commit();
    }
}
package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import hotchemi.android.rate.AppRate;

/**
 * This is the class to show the rating bar for the API.
 * A dialog box is shown to ask to rate the API.
 * The user is asked to rate the application using 5 stars and the rating result is saved by SharedPreferences.
 *  @author Feiqiong Deng
 *  @version version 1
 */
public class RatingSoccerAPI extends AppCompatActivity {

    TextView rateCount, showRating;
    EditText review;
    Button submit;
    RatingBar ratingBar;
    float rateValue;
    SharedPreferences prefs;
    SharedPreferences prefsComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_soccer_a_p_i);

        // Setting the alert dialog when starting the app.
        AlertDialog.Builder builder = new AlertDialog.Builder(RatingSoccerAPI.this);
        View view = LayoutInflater.from(RatingSoccerAPI.this).inflate(R.layout.soccer_dialog, null);

        TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
        ImageButton dialogBtn = view.findViewById(R.id.dialogBtn);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialogContent);

        dialogTitle.setText("Rating");
        dialogBtn.setImageResource(R.drawable.rating);
        dialogContent.setText("Could you rate the API?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goToPage = new Intent(RatingSoccerAPI.this, SoccerGames.class);
                startActivity(goToPage);
            }
        });
        builder.setView(view);
        builder.show();

        // SharedPreferences to save the rating value.
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        prefsComment = getSharedPreferences("Comments", Context.MODE_PRIVATE);
        Intent goToSoccer = new Intent(RatingSoccerAPI.this, SoccerGames.class);

        rateCount = findViewById(R.id.rateCount);
        ratingBar = findViewById(R.id.ratingBar);
        review = findViewById(R.id.review);
        submit = findViewById(R.id.submitBtn);
        showRating = findViewById(R.id.showRating);

        float savedValue = prefs.getFloat("ReserveRating", 5);
        ratingBar.setRating(savedValue);

        String savedComments = prefsComment.getString("ReserveComments", "");
        review.setText(savedComments);

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
            saveSharedPrefs(review.getText().toString());
            startActivity(goToSoccer);
        });
    }

    private void saveSharedPrefs(float valueToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("ReserveRating", valueToSave);
        editor.commit();
    }

    private void saveSharedPrefs(String commentsToSave) {
        SharedPreferences.Editor editor = prefsComment.edit();
        editor.putString("ReserveComments", commentsToSave);
        editor.commit();
    }

}
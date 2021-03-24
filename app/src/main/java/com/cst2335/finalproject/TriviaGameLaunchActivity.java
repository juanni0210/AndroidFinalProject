package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;
import com.plattysoft.leonids.ParticleSystem;

public class TriviaGameLaunchActivity extends AppCompatActivity {
    private String questionNum, difficultyType, questionType, gameURL;
    private EditText questionNumberInput;
    private Spinner difficultySpinner, questionTypeSpinner;
    private Switch timerSwitch;
    private RelativeLayout launchLayout;
    private Button gameStartBtn, backHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game_launch);

        questionNumberInput = findViewById(R.id.questionNumInput);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        questionTypeSpinner = findViewById(R.id.questionTypeSpinner);
        gameStartBtn = findViewById(R.id.startGameBtn);
        backHomeBtn = findViewById(R.id.backHomeBtn);


        //get value that user selected for difficulty of the questions
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficultyType = parent.getItemAtPosition(position).toString();
                difficultyType = difficultyType.replaceAll("\\s+", "").toLowerCase();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                difficultyType = "easy";
            }
        });

        //get value that user selected for question type, multiple choice or True/False
        questionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionType = parent.getItemAtPosition(position).toString();
                questionType = questionType.replaceAll("\\s+", "").toLowerCase();
                if(questionType.equals("true/false")) {
                    questionType = "boolean";
                }
                if(questionType.equals("multiplechoice")) {
                    questionType = "multiple";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {questionType = "anytype";}
        });

        launchLayout = findViewById(R.id.launch_layout);
        timerSwitch = findViewById(R.id.timerSwitch);
        timerSwitch.setChecked(false);
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                if (isChecked) {
                    showSnackbar(getResources().getString(R.string.switch_on_message), true);
                } else {
                    showSnackbar(getResources().getString(R.string.switch_off_message), false);
                }
            }
        });


        // click start button, go to another page to play the game
        Intent goToGameStart = new Intent(TriviaGameLaunchActivity.this, TriviaGameOngoingActivity.class);
        gameStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get value that user input for question numbers
                questionNum = questionNumberInput.getText().toString();
                if (questionType.equals("anytype")) {
                    gameURL = "https://opentdb.com/api.php?amount=" + questionNum.trim() + "&difficulty=" + difficultyType;
                } else {
                    gameURL = "https://opentdb.com/api.php?amount=" + questionNum.trim() + "&type=" + questionType + "&difficulty=" + difficultyType;
                }
                goToGameStart.putExtra("GENERATED_URL", gameURL);
                goToGameStart.putExtra("QUESTION_TYPE", questionType);
                goToGameStart.putExtra("TIMER_SWITCH", timerSwitch.isChecked());
                startActivity(goToGameStart);
            }
        });

        //go back to home page
        Intent goToHome = new Intent(TriviaGameLaunchActivity.this, MainActivity.class);
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToHome);
            }
        });


    }

    public void showSnackbar(String message, boolean isOn) {
        Snackbar snackbar = Snackbar.make(launchLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.undo_text), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timerSwitch.setChecked(!isOn);
                    }
                });
        snackbar.show();
    }



}


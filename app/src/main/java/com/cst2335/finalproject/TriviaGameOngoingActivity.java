package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TriviaGameOngoingActivity extends AppCompatActivity {
    private ArrayList<Question> questionList = new ArrayList<>();
    private Question currentQuestion;
    private String selectedAns = "";
    private int totalQuestionCount, answerCount, correctCount;
    private boolean isTimerChecked;
    private String generatedGameURL, questionType;
    private ImageView waitingImage;
    private TextView unAnsweredView, correctView, wrongView, questionTextView, timeTextView;
    private Button nextButton;
    private LinearLayout timerLayout;
    private Timer timer;
    private TimerTask timerTask;
    private Double time = 0.0;
    private boolean timerStarted = false;

    private ProgressBar gameProgressBar;
    private RadioGroup choiceRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game_ongoing);

        unAnsweredView = findViewById(R.id.unAnswered);
        unAnsweredView.setText("");
        correctView = findViewById(R.id.correctedAns);
        correctView.setText("");
        wrongView = findViewById(R.id.wrongAns);
        wrongView.setText("");

        timerLayout = findViewById(R.id.timerLayout);
        //timer layout won'be show at beginning until we know user switch timer on in the previous page
        timerLayout.setVisibility(View.GONE);
        timeTextView = findViewById(R.id.timeTextView);

        questionTextView = findViewById(R.id.questionText);
        questionTextView.setText("");
        nextButton = findViewById(R.id.nextBtn);
        nextButton.setVisibility(View.INVISIBLE);
        choiceRadioGroup = findViewById(R.id.choiceRatioGroup);
        //get selected answer from user
        choiceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton currentBtn = (RadioButton) group.findViewById(checkedId);
            selectedAns = currentBtn.getText().toString();
        });

        gameProgressBar = findViewById(R.id.gameProgressBar);
        gameProgressBar.setVisibility(View.VISIBLE);
        waitingImage = findViewById(R.id.waitingImg);

        Intent fromLaunchPage = getIntent();
        generatedGameURL = fromLaunchPage.getStringExtra("GENERATED_URL");
        questionType = fromLaunchPage.getStringExtra("QUESTION_TYPE");
        isTimerChecked = fromLaunchPage.getBooleanExtra("TIMER_SWITCH", false);
        Toast.makeText(TriviaGameOngoingActivity.this, generatedGameURL, Toast.LENGTH_SHORT).show();
        timer = new Timer();


        // click next button, it will show the next question by calling update method
        nextButton.setOnClickListener(v -> {
            //check whether user selected an answer or not, if not, create a toast to tell user
            //it user answered the question, then go to next question
            if (selectedAns == "") {
                Toast.makeText(TriviaGameOngoingActivity.this, getResources().getString(R.string.selectAnsNotice), Toast.LENGTH_LONG).show();
            } else {
                answerCount++;
                //call before update
                if (currentQuestion.getCorrectedAns().equals(selectedAns)) {
                    correctCount++;
                }
                updateQuestion();
                unAnsweredView.setText((totalQuestionCount - answerCount) + "/" + totalQuestionCount + " " + getResources().getString(R.string.unAnsweredText));
                correctView.setText(correctCount + " " + getResources().getString(R.string.correctCountText));
                wrongView.setText((answerCount - correctCount) + " " + getResources().getString(R.string.wrongCountText));
            }

        });

        GameFactory triviaRequest = new GameFactory();
        triviaRequest.execute(generatedGameURL);
    }

    public void updateQuestion() {
        selectedAns = "";
        currentQuestion = questionList.get(answerCount);
        int questionIndex = answerCount + 1;
        questionTextView.setText(questionIndex + ". " + currentQuestion.getQuestionText());
        choiceRadioGroup.removeAllViews();
        //loop the choices list and create radio button for each choice in the radio group
        for(int i=0; i < currentQuestion.getChoices().size(); i++){
            RadioButton choiceRadioBtn = new RadioButton(this);
            choiceRadioBtn.setText(currentQuestion.getChoices().get(i));
            choiceRadioBtn.setTextSize(18);
            choiceRadioGroup.addView(choiceRadioBtn);
        }

        if (answerCount == questionList.size()-1) {
            nextButton.setText("FINISH GAME");

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check whether the timer is stop if user checked the timer switch
                    if(isTimerChecked && timerStarted == true) {
                        timerStarted = false;
                        timerTask.cancel();
                    }
                    //when click finish game, the timer view is gone
                    timerLayout.setVisibility(View.GONE);
                    // check whether the last question you answered is right
                    if (currentQuestion.getCorrectedAns().equals(selectedAns)) {
                        correctCount++;
                    }
                    Intent goToGameResult = new Intent(TriviaGameOngoingActivity.this, TriviaGameResultActivity.class);
                    goToGameResult.putExtra("CORRECTED_ANSWER_COUNT", correctCount);
                    goToGameResult.putExtra("WRONG_ANSWER_COUNT", totalQuestionCount - correctCount);
                    goToGameResult.putExtra("DIFFICULTY_TYPE", currentQuestion.getDifficultyType());
                    goToGameResult.putExtra("QUESTION_TYPE", questionType);
                    goToGameResult.putExtra("TIME_SPENT", timeTextView.getText().toString());
                    startActivity(goToGameResult);


                }
            });
        }
    }

    class GameFactory extends AsyncTask< String, Integer, String> {
        @Override
        protected String doInBackground(String ... args)
        {
            try {

                URL gameURL = new URL(args[0]);
                //open the connection
                HttpURLConnection gameURLConnection = (HttpURLConnection) gameURL.openConnection();
                //wait for data:
                InputStream uvResponse = gameURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(uvResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                result = result.replaceAll("&#039;", "'");
                result = result.replaceAll("&quot;", "''");
                result = result.replaceAll("&amp;", "&");
                result = result.replaceAll("&eacute;", "É");


                JSONObject jObject = new JSONObject(result);
                JSONArray questionsArray = jObject.getJSONArray("results");

                if (questionsArray != null) {
                    for (int i = 0; i < questionsArray.length(); i++) {
                        Question question = new Question((JSONObject) questionsArray.get(i));
                        questionList.add(question);
                    }

                    //want to see the right answer of each question - test purpose
                    for (int i = 0; i < questionList.size(); i++) {
                        System.out.println((i+1) + ". " + questionList.get(i).getQuestionText() + "corrected: " + questionList.get(i).getCorrectedAns());
                    }

                    totalQuestionCount = questionList.size();
                    System.out.println(totalQuestionCount);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }


        @Override
        protected void onProgressUpdate(Integer ... values)
        {
            super.onProgressUpdate(values);
            gameProgressBar.setVisibility(View.VISIBLE);
            gameProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String fromDoInBackground)
        {
            //if user switch on timerSwitch, the timer Layout shows on the game page
            if(isTimerChecked) {
                timerLayout.setVisibility(View.VISIBLE);
            }
            // start timer
            if(!timerStarted) {
                timerStarted = true;

                //start timer
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time++;
                                timeTextView.setText(getTimerText());
                            }
                        });
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 0, 1000);
            }
            nextButton.setVisibility(View.VISIBLE);
            updateQuestion();
            //when all the questions are loaded, progress bar and waiting iamge will be gone
            gameProgressBar.setVisibility(View.GONE);
            waitingImage.setVisibility(View.GONE);
            unAnsweredView.setText((totalQuestionCount-answerCount) + "/" + totalQuestionCount + " " + getResources().getString(R.string.unAnsweredText));
            correctView.setText(correctCount + " " + getResources().getString(R.string.correctCountText));
            wrongView.setText((answerCount-correctCount) + " " + getResources().getString(R.string.wrongCountText));
        }
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = (rounded % 86400) / 3600;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

    }


}
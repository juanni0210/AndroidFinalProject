package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

/**
 * Trivia page where all the questions are loaded and user can start answer questions.
 * @author Juan Ni
 */
public class TriviaGameOngoingActivity extends AppCompatActivity {
    private ArrayList<Question> questionList = new ArrayList<>();
    private Question currentQuestion;
    private String selectedAns = "";
    private int totalQuestionCount, answerCount, correctCount;
    private boolean isTimerChecked;
    private String generatedGameURL;
    private QuestionType questionType;
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

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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
        questionType = (QuestionType)fromLaunchPage.getSerializableExtra("QUESTION_TYPE");
        isTimerChecked = fromLaunchPage.getBooleanExtra("TIMER_SWITCH", false);
        //just want to check the url from the previous page
        //Toast.makeText(TriviaGameOngoingActivity.this, generatedGameURL, Toast.LENGTH_SHORT).show();
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
                if (currentQuestion.getCorrectAns().equals(selectedAns)) {
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

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * @param menu Menu: The options menu in which you place your items.
     * @return boolean: return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * @param item MenuItem: The menu item that was selected. This value cannot be null.
     * @return boolean: Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.backHomeItem:
                startActivity(new Intent(TriviaGameOngoingActivity.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                startActivity(new Intent(TriviaGameOngoingActivity.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(TriviaGameOngoingActivity.this, SongsterSearch.class));
                break;
            case R.id.carDBItem:
                startActivity(new Intent(TriviaGameOngoingActivity.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                startActivity(new Intent(TriviaGameOngoingActivity.this, RatingSoccerAPI.class));
                break;
            case R.id.helpItem:
                AlertDialog.Builder triviaHelpDialog = new AlertDialog.Builder(this);
                triviaHelpDialog.setTitle(getResources().getString(R.string.triviaHelpTile))
                        //What is the message:
                        .setMessage(getResources().getString(R.string.triviaInstructions1) + "\n"
                                + getResources().getString(R.string.triviaInstructions2) + "\n"
                                + getResources().getString(R.string.triviaInstructions3) + "\n"
                                + getResources().getString(R.string.triviaInstructions4) + "\n"
                                + getResources().getString(R.string.triviaInstructions5))
                        //What the No button does:
                        .setNegativeButton(getResources().getString(R.string.closeHelpDialog), (click, arg) -> {
                        })
                        //Show the dialog
                        .create().show();
                break;
        }
        return true;
    }

    /**
     * Update the question after user answers one and pass data to next page when user answers all questions.
     */
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
                    if (currentQuestion.getCorrectAns().equals(selectedAns)) {
                        correctCount++;
                    }
                    Intent goToGameResult = new Intent(TriviaGameOngoingActivity.this, TriviaGameResultActivity.class);
                    //pass data to next page
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

    /**
     * Inner class GameFactory extends from AsyncTask abstract class to get conncection to API, getting data from it in another thread
     * and  integrating into UI.
     * @author Juan Ni
     */
    class GameFactory extends AsyncTask< String, Integer, String> {
        @Override
        protected String doInBackground(String ... args)
        {
            try {
                URL triviaGameURL = new URL(args[0]);
                //open the connection
                HttpURLConnection gameURLConnection = (HttpURLConnection) triviaGameURL.openConnection();
                //wait for data:
                InputStream triviaResponse = gameURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(triviaResponse, "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    builder.append(line + "\n");
                }
                String result = builder.toString();
                result = result.replaceAll("&#039;", "'");
                result = result.replaceAll("&quot;", "''");
                result = result.replaceAll("&amp;", "&");
                result = result.replaceAll("&eacute;", "É");
                result = result.replaceAll("&Eacute;", "É");
                result = result.replaceAll("&atilde;", "Ã");
                result = result.replaceAll("&Atilde;", "Ã");


                JSONObject jObject = new JSONObject(result);
                //get the JSONArray of "results" which contains each question as an JSONObject
                JSONArray questionsArray = jObject.getJSONArray("results");

                if (questionsArray != null) {
                    for (int i = 0; i < questionsArray.length(); i++) {
                        Question question = new Question((JSONObject) questionsArray.get(i));
                        questionList.add(question);
                    }

                    //want to see the right answer of each question - test purpose
                    for (int i = 0; i < questionList.size(); i++) {
                        System.out.println((i+1) + ". " + questionList.get(i).getQuestionText() + "corrected: " + questionList.get(i).getCorrectAns());
                    }

                    totalQuestionCount = questionList.size();
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
            //when all the questions are loaded, the next button shows
            nextButton.setVisibility(View.VISIBLE);
            //by calling the updateQuestion method here, the first question will be displayed
            updateQuestion();
            //when all the questions are loaded, progress bar and waiting image will be gone
            gameProgressBar.setVisibility(View.GONE);
            waitingImage.setVisibility(View.GONE);
            unAnsweredView.setText((totalQuestionCount-answerCount) + "/" + totalQuestionCount + " " + getResources().getString(R.string.unAnsweredText));
            correctView.setText(correctCount + " " + getResources().getString(R.string.correctCountText));
            wrongView.setText((answerCount-correctCount) + " " + getResources().getString(R.string.wrongCountText));
        }
    }

    /**
     * Returns text of time user spent on the game with format 00:00:00.
     * @return String value of formated text showing time 00:00:00
     */
    private String getTimerText() {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = (rounded % 86400) / 3600;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

}
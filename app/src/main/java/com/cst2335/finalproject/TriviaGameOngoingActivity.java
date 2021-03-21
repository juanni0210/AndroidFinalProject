package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class TriviaGameOngoingActivity extends AppCompatActivity {
    private ArrayList<Question> questionList = new ArrayList<>();
    private Question currentQuestion;
    private String selectedAns;
    private int totalQuestionCount;
    private int answerCount;
    private int correctCount;


    private String generatedGameURL, questionType;
    private ProgressBar gameProgressBar;
    private ChoiceListAdapter myChoiceAdapter;
    private ListView choiceListView;
    private RadioGroup choiceRadioGroup;

    private TextView unAnsweredView, correctView, wrongView, questionTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game_ongoing);

        unAnsweredView = findViewById(R.id.unAnswered);
        correctView = findViewById(R.id.correctedAns);
        wrongView = findViewById(R.id.wrongAns);


        questionTextView = findViewById(R.id.questionText);
        nextButton = findViewById(R.id.nextBtn);
        nextButton.setVisibility(View.INVISIBLE);
        choiceListView = findViewById(R.id.choiceListView);

        gameProgressBar = findViewById(R.id.gameProgressBar);
        gameProgressBar.setVisibility(View.VISIBLE);

        Intent fromLaunchPage = getIntent();
        generatedGameURL = fromLaunchPage.getStringExtra("GENERATED_URL");
        questionType = fromLaunchPage.getStringExtra("QUESTION_TYPE");
        Toast.makeText(TriviaGameOngoingActivity.this, generatedGameURL, Toast.LENGTH_SHORT).show();


        // click next button, it will show the next question by calling update method
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCount++;
                //call before update
                if(currentQuestion.getCorrectedAns().equals(selectedAns)) {
                    correctCount++;
                }
                updateQuestion();
                unAnsweredView.setText((totalQuestionCount-answerCount) + "/" + totalQuestionCount + " " + getResources().getString(R.string.unAnsweredText));
                correctView.setText(correctCount + " " + getResources().getString(R.string.correctCountText));
                wrongView.setText((answerCount-correctCount) + " " + getResources().getString(R.string.wrongCountText));
            }
        });


        GameFactory triviaRequest = new GameFactory();
        triviaRequest.execute(generatedGameURL);
    }

    public void updateQuestion() {
        myChoiceAdapter = new ChoiceListAdapter();
        currentQuestion = questionList.get(answerCount);
        int questionIndex = answerCount + 1;
        questionTextView.setText(questionIndex + ". " + currentQuestion.getQuestionText());
        choiceListView.setAdapter(myChoiceAdapter);
        myChoiceAdapter.notifyDataSetChanged();
        if (answerCount == questionList.size()-1) {
            nextButton.setText("FINISH GAME");
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check whether the last question you answered is right
                    if(currentQuestion.getCorrectedAns().equals(selectedAns)) {
                        correctCount++;
                    }
                    Intent goToGameResult = new Intent(TriviaGameOngoingActivity.this, GameResultActivity.class);
                    goToGameResult.putExtra("CORRECTED_ANSWER_COUNT", correctCount);
                    goToGameResult.putExtra("WRONG_ANSWER_COUNT", totalQuestionCount-correctCount);
                    goToGameResult.putExtra("DIFFICULTY_TYPE", currentQuestion.getDifficultyType());
                    goToGameResult.putExtra("QUESTION_TYPE", questionType);
                    startActivity(goToGameResult);
                }
            });
        }


    }


    private class ChoiceListAdapter extends BaseAdapter {

        public int getCount() { return currentQuestion.getChoices().size(); }

        public String getItem(int position) { return currentQuestion.getChoices().get(position); }

        //return the object's database id
        public long getItemId(int position) { return (long)position; }

        public View getView(int position, View old, ViewGroup parent)
        {

            LayoutInflater inflater = getLayoutInflater();

            String choice = getItem(position);

            View newView = inflater.inflate(R.layout.choices_row, parent, false);

            Button choiceButton = newView.findViewById(R.id.choiceBtn);
            choiceButton.setText(choice);
            choiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedAns = choiceButton.getText().toString();
                }
            });

          //return it to be put in the table
            return newView;
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
            nextButton.setVisibility(View.VISIBLE);
            updateQuestion();
            gameProgressBar.setVisibility(View.GONE);
            unAnsweredView.setText((totalQuestionCount-answerCount) + "/" + totalQuestionCount + " " + getResources().getString(R.string.unAnsweredText));
            correctView.setText(correctCount + " " + getResources().getString(R.string.correctCountText));
            wrongView.setText((answerCount-correctCount) + " " + getResources().getString(R.string.wrongCountText));
        }
    }


}
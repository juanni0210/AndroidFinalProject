package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class GameResultActivity extends AppCompatActivity {
    private Button startAgainBtn, saveScoreBtn, saveNameBtn, cancelSaveBtn;
    private int totalQuestionCount, correctAnswerCount, wrongAnswerCount, gameScore;
    private TextView congratsTextView, scoreView, resultDetailsView, scoreResult;
    private String difficultyType, questionType;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText playerNameInput;

    SharedPreferences prefs;
    public static final String PLAYER_NAME_TAG = "player_name";
    SQLiteDatabase triviaDB;
    private ScoreListAdapter scoreAdapter;
    private ArrayList<ScoreRecord> scoreRecordsList = new ArrayList<>();
    private boolean isTablet;
    public static final String PLAYER_NAME = "PLAYER NAME";
    public static final String PLAYER_SCORE = "PLAYER SCORE";
    public static final String QUESTION_AMOUNT = "QUESTION AMOUNT";
    public static final String DIFFICULTY_TYPE = "DIFFICULTY TYPE";
    public static final String QUESTION_TYPE = "QUESTION TYPE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        congratsTextView = findViewById(R.id.congratText);
        scoreView = findViewById(R.id.scoreText);
        resultDetailsView = findViewById(R.id.resultDetils);
        saveScoreBtn = findViewById(R.id.saveScoreBtn);

        isTablet = findViewById(R.id.triviaFragment) != null;

        Intent fromGamePage = getIntent();
        correctAnswerCount = fromGamePage.getIntExtra("CORRECTED_ANSWER_COUNT", 0);
        wrongAnswerCount = fromGamePage.getIntExtra("WRONG_ANSWER_COUNT", 0);
        difficultyType = fromGamePage.getStringExtra("DIFFICULTY_TYPE");
        questionType = fromGamePage.getStringExtra("QUESTION_TYPE");
        totalQuestionCount = correctAnswerCount + wrongAnswerCount;

        prefs = getSharedPreferences("PlayerNames", Context.MODE_PRIVATE);


        double rightRatio = correctAnswerCount/(double)totalQuestionCount * 100;

        if(rightRatio == 100) {
            congratsTextView.setText(getResources().getString(R.string.masterpiece));
        } else if (rightRatio  < 100 && rightRatio >= 90 ) {
            congratsTextView.setText(getResources().getString(R.string.amazing));
        } else if (rightRatio < 90 && rightRatio >= 80 ) {
            congratsTextView.setText(getResources().getString(R.string.great));
        } else if (rightRatio < 80 && rightRatio >= 60 ) {
            congratsTextView.setText(getResources().getString(R.string.good));
        } else {
            congratsTextView.setText(getResources().getString(R.string.keepGoing));
        }

        if (difficultyType.equals("easy")) {
            gameScore = correctAnswerCount;
        } else if (difficultyType.equals("medium")) {
            gameScore = correctAnswerCount * 2;
        } else if (difficultyType.equals("hard")) {
            gameScore = correctAnswerCount * 3;
        }

        scoreView.setText(getResources().getString(R.string.scoreTitle) + " " + gameScore);
        resultDetailsView.setText(" YOU ANSWERED " + correctAnswerCount + " questions right!");


        ListView scoreListView = findViewById(R.id.scoreListView);

        //get any previously saved Message objects
        loadScoreRecordFromDatabase();

        //create an adapter object and send it to the listView
        scoreAdapter = new ScoreListAdapter();
        scoreListView.setAdapter(scoreAdapter);

        scoreListView.setOnItemLongClickListener( (parent, view, position, id) -> {
            ScoreRecord selectedRecord = scoreRecordsList.get(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.deleteNotice))
                    //What is the message:
                    .setMessage(getResources().getString(R.string.selectRow) + " " + (position+1)  + " ." + "\n"
                            + getResources().getString(R.string.showPlayerName) + " " + selectedRecord.getName() + "." + "\n"
                            + getResources().getString(R.string.showScore) + " " + selectedRecord.getScore() + "." + "\n"
                            + getResources().getString(R.string.showQuestionAmount) + " " + selectedRecord.getQuestionAmount() + " " + getResources().getString(R.string.questionText) + ".")
                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        String idTag = Long.toString(id);
                        if (isTablet) {
                            if (getSupportFragmentManager().findFragmentByTag(idTag) != null) {
                                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(idTag)).commit();
                            }
                        }
                        deleteScoreRecord(selectedRecord);
                        scoreRecordsList.remove(position);
                        scoreAdapter.notifyDataSetChanged();

                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })
                    //Show the dialog
                    .create().show();
            return true;
        });


        scoreListView.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToTriviaFragment = new Bundle();
            dataToTriviaFragment.putString(PLAYER_NAME, scoreRecordsList.get(position).getName() );
            dataToTriviaFragment.putInt(PLAYER_SCORE, scoreRecordsList.get(position).getScore());
            dataToTriviaFragment.putInt(QUESTION_AMOUNT, scoreRecordsList.get(position).getQuestionAmount());
            dataToTriviaFragment.putString(DIFFICULTY_TYPE, scoreRecordsList.get(position).getDifficultyType());
            dataToTriviaFragment.putString(QUESTION_TYPE, scoreRecordsList.get(position).getQuestionType());

            if(isTablet)
            {
                TriviaDetailsFragment triviaDFragment = new TriviaDetailsFragment(); //add a DetailFragment
                triviaDFragment.setArguments(dataToTriviaFragment); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.triviaFragment, triviaDFragment, Long.toString(id)) //Add the fragment in FrameLayout,with ID as a tag
                        .addToBackStack(null)  //go back to the previous one lecture
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent triviaNextActivity = new Intent(GameResultActivity.this, TriviaEmptyActivity.class);
                triviaNextActivity.putExtras(dataToTriviaFragment); //send data to next activity
                startActivity(triviaNextActivity); //make the transition
            }
        });



        saveScoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewScoreRecordDialog();
            }
        });


        startAgainBtn = findViewById(R.id.startAgainBtn);
        startAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToGameLaunch = new Intent(GameResultActivity.this, TriviaGameLaunchActivity.class);
                startActivity(goToGameLaunch);
            }
        });
    }

    protected void deleteScoreRecord(ScoreRecord record)
    {
        triviaDB.delete(TiviaOpener.TABLE_NAME, TiviaOpener.COL_ID + "= ?", new String[] {Long.toString(record.getId())});

    }

    public void createNewScoreRecordDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popUpWindowView = getLayoutInflater().inflate(R.layout.save_score_window, null);
        playerNameInput = popUpWindowView.findViewById(R.id.inputPlayerName);
        scoreResult = popUpWindowView.findViewById(R.id.yourScore);
        saveNameBtn = popUpWindowView.findViewById(R.id.saveNameBtn);
        cancelSaveBtn = popUpWindowView.findViewById(R.id.cancelSaveBtn);

        dialogBuilder.setView(popUpWindowView);
        dialog = dialogBuilder.create();
        dialog.show();

        String savedPlayerNames = prefs.getString(PLAYER_NAME_TAG, "");
        playerNameInput.setText(savedPlayerNames);
        scoreResult.setText(getResources().getString(R.string.scoreTitle) + " " + gameScore);

        saveNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlayerName();
                createScoreRecordAndAddToDatabase();
                dialog.dismiss();
            }
        });

        cancelSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void savePlayerName(){
        SharedPreferences prefs = getSharedPreferences("PlayerNames", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PLAYER_NAME_TAG, playerNameInput.getText().toString());
        editor.commit();
    }

    private void createScoreRecordAndAddToDatabase() {
        String inputName = playerNameInput.getText().toString();
        //add to the database and get the new ID
        ContentValues newRowValues = new ContentValues();

        //Now provide a value for every database column defined in MyOpener.java:

        newRowValues.put(TiviaOpener.COL_NAME, inputName);
        newRowValues.put(TiviaOpener.COL_SCORE, gameScore);
        newRowValues.put(TiviaOpener.COL_AMOUNT, totalQuestionCount);
        newRowValues.put(TiviaOpener.COL_DIFFICULTY, difficultyType);
        newRowValues.put(TiviaOpener.COL_TYPE, questionType);

        //Now insert in the database:
        long newId = triviaDB.insert(TiviaOpener.TABLE_NAME, null, newRowValues);

        ScoreRecord record = new ScoreRecord(inputName, gameScore, difficultyType, questionType, totalQuestionCount, newId);

        scoreRecordsList.add(record);
        Collections.sort(scoreRecordsList, (ScoreRecord a, ScoreRecord b) -> {
            return b.getScore() - a.getScore();
        });
        scoreAdapter.notifyDataSetChanged();
    }

    private void loadScoreRecordFromDatabase() {
        //get a database connection:
        TiviaOpener triviaDBOpener = new TiviaOpener(this);
        //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer
        triviaDB = triviaDBOpener.getWritableDatabase();

        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String[] columns = {TiviaOpener.COL_ID, TiviaOpener.COL_NAME, TiviaOpener.COL_SCORE, TiviaOpener.COL_AMOUNT, TiviaOpener.COL_DIFFICULTY, TiviaOpener.COL_TYPE};
        //query all the results from the database:
        Cursor results = triviaDB.query(false, TiviaOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int nameColumnIndex = results.getColumnIndex(TiviaOpener.COL_NAME);
        int scoreColumnIndex = results.getColumnIndex(TiviaOpener.COL_SCORE);
        int amountColumnIndex = results.getColumnIndex(TiviaOpener.COL_AMOUNT);
        int difficultyColumnIndex = results.getColumnIndex(TiviaOpener.COL_DIFFICULTY);
        int typeColumnIndex = results.getColumnIndex(TiviaOpener.COL_TYPE);
        int idColIndex = results.getColumnIndex(TiviaOpener.COL_ID);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String name = results.getString(nameColumnIndex);
            int score = results.getInt(scoreColumnIndex);
            int amount = results.getInt(amountColumnIndex);
            String difficulty = results.getString(difficultyColumnIndex);
            String type = results.getString(typeColumnIndex);
            long id = results.getLong(idColIndex);

            //add the new Message to the array list:
            scoreRecordsList.add(new ScoreRecord(name, score, difficulty, type, amount, id));
        }
        Collections.sort(scoreRecordsList, (ScoreRecord a, ScoreRecord b) -> {
            return b.getScore() - a.getScore();
        });
    }


    private class ScoreListAdapter extends BaseAdapter {
        public int getCount() { return scoreRecordsList.size(); }

        public ScoreRecord getItem(int position) { return scoreRecordsList.get(position); }

        //return the object's database id
        public long getItemId(int position) { return getItem(position).getId(); }

        public View getView(int position, View old, ViewGroup parent)
        {
            View newView;
            LayoutInflater inflater = getLayoutInflater();

            ScoreRecord record = getItem(position);

            newView = inflater.inflate(R.layout.score_row, parent, false);

            TextView messageTextView = newView.findViewById(R.id.scoreRecord);
            messageTextView.setText((position + 1) + ". " + record.getName() + "        " + record.getScore());

            //return it to be put in the table
            return newView;
        }
    }
}
package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Trivia game result page shows result details and will ask user whether to save the score or not, if they save
 * the score it will be saved to the database as well, and user can see all the saved score records displayed
 * once they save it.
 * @author Juan Ni
 */
public class TriviaGameResultActivity extends AppCompatActivity {
    private Button startAgainBtn, saveScoreBtn, saveNameBtn, cancelSaveBtn;
    private int totalQuestionCount, correctAnswerCount, wrongAnswerCount, gameScore;
    private TextView congratsTextView, scoreView, resultDetailsView, scoreResult;
    private String difficultyType, timeSpent;
    private QuestionType questionType;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText playerNameInput;

    SharedPreferences triviaPlayerPrefs;
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


    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game_result);

        congratsTextView = findViewById(R.id.congratText);
        scoreView = findViewById(R.id.scoreText);
        resultDetailsView = findViewById(R.id.resultDetils);
        saveScoreBtn = findViewById(R.id.saveScoreBtn);
        isTablet = findViewById(R.id.triviaFragment) != null;

        //listen for view measured event, then emit particles
        ViewTreeObserver viewTreeObserver = scoreView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    scoreView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    new ParticleSystem(TriviaGameResultActivity.this, 80, R.drawable.circle_yellow, 2000)
                            .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                            .setAcceleration(0.0005f, 90)
                            .setFadeOut(1000)
                            .oneShot(scoreView, 80);

                    new ParticleSystem(TriviaGameResultActivity.this, 80, R.drawable.circle_pink, 2000)
                            .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                            .setAcceleration(0.0005f, 90)
                            .setFadeOut(1000)
                            .oneShot(scoreView, 80);

                    new ParticleSystem(TriviaGameResultActivity.this, 80, R.drawable.circle_blue, 2000)
                            .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                            .setAcceleration(0.0005f, 90)
                            .setFadeOut(1000)
                            .oneShot(scoreView, 80);
                }
            });
        }

        //get data from previous page
        Intent fromGamePage = getIntent();
        correctAnswerCount = fromGamePage.getIntExtra("CORRECTED_ANSWER_COUNT", 0);
        wrongAnswerCount = fromGamePage.getIntExtra("WRONG_ANSWER_COUNT", 0);
        difficultyType = fromGamePage.getStringExtra("DIFFICULTY_TYPE");
        questionType = (QuestionType) fromGamePage.getSerializableExtra("QUESTION_TYPE");
        timeSpent = fromGamePage.getStringExtra("TIME_SPENT");
        totalQuestionCount = correctAnswerCount + wrongAnswerCount;

        triviaPlayerPrefs = getSharedPreferences("TriviaPlayerNames", Context.MODE_PRIVATE);


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
        resultDetailsView.setText("Time spent: " + timeSpent + "\n"
                                 + "You answered " + correctAnswerCount + " out of " + totalQuestionCount + " right!");

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
                    .setMessage(getResources().getString(R.string.triviaSelectRow) + " " + (position+1)  + " ." + "\n"
                            + getResources().getString(R.string.showPlayerName) + " " + selectedRecord.getName() + "." + "\n"
                            + getResources().getString(R.string.showScore) + " " + selectedRecord.getScore() + "." + "\n"
                            + getResources().getString(R.string.showQuestionAmount) + " " + selectedRecord.getQuestionAmount() + " " + getResources().getString(R.string.questionText) + ".")
                    //what the Yes button does:
                    .setPositiveButton(getResources().getString(R.string.yesName), (click, arg) -> {
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
                    .setNegativeButton(getResources().getString(R.string.noName), (click, arg) -> { })
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
                Intent triviaNextActivity = new Intent(TriviaGameResultActivity.this, TriviaEmptyActivity.class);
                triviaNextActivity.putExtras(dataToTriviaFragment); //send data to next activity
                startActivity(triviaNextActivity); //make the transition
            }
        });

        //when click save button, createNewScoreRecordDialog method will be called, a window pops up to ask user whether to save it or not
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
                Intent goToGameLaunch = new Intent(TriviaGameResultActivity.this, TriviaGameLaunchActivity.class);
                startActivity(goToGameLaunch);
            }
        });

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * @param menu Menu: The options menu in which you place your items.
     * @return boolean: Return true for the menu to be displayed; if you return false it will not be shown.
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
                startActivity(new Intent(TriviaGameResultActivity.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                startActivity(new Intent(TriviaGameResultActivity.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                Toast.makeText(this, "Go to songster page", Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
                Toast.makeText(this, "Go to car database page", Toast.LENGTH_LONG).show();
                break;
            case R.id.soccerItem:
                Toast.makeText(this, "Go to soccer game page", Toast.LENGTH_LONG).show();
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
     * Deletes the score record from database.
     * @param record ScoreRecord type called record
     */
    protected void deleteScoreRecord(ScoreRecord record)
    {
        triviaDB.delete(TiviaDatabaseOpener.TABLE_NAME, TiviaDatabaseOpener.COL_ID + "= ?", new String[] {Long.toString(record.getId())});
    }

    /**
     * Pops up a window asking user to save the score record or not.
     */
    public void createNewScoreRecordDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popUpWindowView = getLayoutInflater().inflate(R.layout.trivia_save_score_window, null);
        playerNameInput = popUpWindowView.findViewById(R.id.inputPlayerName);
        scoreResult = popUpWindowView.findViewById(R.id.yourScore);
        saveNameBtn = popUpWindowView.findViewById(R.id.saveNameBtn);
        cancelSaveBtn = popUpWindowView.findViewById(R.id.cancelSaveBtn);

        dialogBuilder.setView(popUpWindowView);
        dialog = dialogBuilder.create();
        dialog.show();

        String savedPlayerNames = triviaPlayerPrefs.getString(PLAYER_NAME_TAG, "");
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

    /**
     * Saves player name input by users to the sharedPreferences.
     */
    private void savePlayerName(){
        SharedPreferences triviaPlayerPrefs = getSharedPreferences("TriviaPlayerNames", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = triviaPlayerPrefs.edit();
        editor.putString(PLAYER_NAME_TAG, playerNameInput.getText().toString());
        editor.commit();
    }

    /**
     * Pop us a window asking user to save the score record or not.
     */
    private void createScoreRecordAndAddToDatabase() {
        String inputName = playerNameInput.getText().toString();
        //add to the database and get the new ID
        ContentValues newRowValues = new ContentValues();

        String questionTypeString = QuestionType.toDisplayText(questionType, getResources());
        //Now provide a value for every database column defined in TriviaDatabaseOpener.java:
        newRowValues.put(TiviaDatabaseOpener.COL_NAME, inputName);
        newRowValues.put(TiviaDatabaseOpener.COL_SCORE, gameScore);
        newRowValues.put(TiviaDatabaseOpener.COL_AMOUNT, totalQuestionCount);
        newRowValues.put(TiviaDatabaseOpener.COL_DIFFICULTY, difficultyType);
        newRowValues.put(TiviaDatabaseOpener.COL_TYPE, questionTypeString);

        //Now insert in the database:
        long newId = triviaDB.insert(TiviaDatabaseOpener.TABLE_NAME, null, newRowValues);

        ScoreRecord record = new ScoreRecord(inputName, gameScore, difficultyType, questionTypeString, totalQuestionCount, newId);

        scoreRecordsList.add(record);
        // sort the socre in order to show the score in the list view from highest to the lowest
        Collections.sort(scoreRecordsList, (ScoreRecord a, ScoreRecord b) -> {
            return b.getScore() - a.getScore();
        });
        scoreAdapter.notifyDataSetChanged();
    }

    /**
     * Loads score records from database and displays on the list view.
     */
    private void loadScoreRecordFromDatabase() {
        //get a database connection:
        TiviaDatabaseOpener triviaDBOpener = new TiviaDatabaseOpener(this);
        //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer
        triviaDB = triviaDBOpener.getWritableDatabase();

        // We want to get all of the columns. Look at TriviaDatabaseOpener.java for the definitions:
        String[] columns = {TiviaDatabaseOpener.COL_ID, TiviaDatabaseOpener.COL_NAME, TiviaDatabaseOpener.COL_SCORE, TiviaDatabaseOpener.COL_AMOUNT, TiviaDatabaseOpener.COL_DIFFICULTY, TiviaDatabaseOpener.COL_TYPE};
        //query all the results from the database:
        Cursor results = triviaDB.query(false, TiviaDatabaseOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int nameColumnIndex = results.getColumnIndex(TiviaDatabaseOpener.COL_NAME);
        int scoreColumnIndex = results.getColumnIndex(TiviaDatabaseOpener.COL_SCORE);
        int amountColumnIndex = results.getColumnIndex(TiviaDatabaseOpener.COL_AMOUNT);
        int difficultyColumnIndex = results.getColumnIndex(TiviaDatabaseOpener.COL_DIFFICULTY);
        int typeColumnIndex = results.getColumnIndex(TiviaDatabaseOpener.COL_TYPE);
        int idColIndex = results.getColumnIndex(TiviaDatabaseOpener.COL_ID);

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


    /**
     * Inner class ScoreListAdapter extends from BaseAdapter, a customized Adapter used in ListView
     * @author Juan Ni
     */
    private class ScoreListAdapter extends BaseAdapter {
        /**
         * Returns count of items.
         * @return int value of the count of the items represented by this Adapter.
         */
        public int getCount() { return scoreRecordsList.size(); }

        /**
         * Returns ScoreRecord type data at the specified position.
         * @param position int - Position of the item whose data we want within the adapter's data set.
         * @return ScoreRecord type object at specified position.
         */
        public ScoreRecord getItem(int position) { return scoreRecordsList.get(position); }

        /**
         * Returns database id of ScoreRecord object at the specified position in the ListView.
         * @param position int value of position
         * @return long value of database id
         */
        public long getItemId(int position) { return getItem(position).getId(); }

        /**
         * Gets a View that displays the scores at the specified position in the ListView.
         * @param position  int - The position of the item within the adapter's data set of the item whose view we want.
         * @param old View - The old view to reuse, if possible.
         * @param parent ViewGroup - The parent that this view will eventually be attached to.
         * @return A View corresponding to the data at the specified position.
         */
        public View getView(int position, View old, ViewGroup parent)
        {
            View newView;
            LayoutInflater inflater = getLayoutInflater();

            ScoreRecord record = getItem(position);

            newView = inflater.inflate(R.layout.trivia_score_row, parent, false);

            TextView messageTextView = newView.findViewById(R.id.scoreRecord);
            messageTextView.setText((position + 1) + ". " + record.getName() + "        " + record.getScore());

            //return it to be put in the table
            return newView;
        }
    }

}
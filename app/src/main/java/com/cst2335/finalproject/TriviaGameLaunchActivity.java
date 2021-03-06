package com.cst2335.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * Launch page of trivia game where user can select question number, type, difficulty type and other options.
 * @author Juan Ni
 */
public class TriviaGameLaunchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DifficultyType difficultyType = DifficultyType.Easy;
    private QuestionType questionType = QuestionType.Any;
    private EditText questionNumberInput;
    private Spinner difficultySpinner, questionTypeSpinner;
    private Switch timerSwitch;
    private RelativeLayout launchLayout;
    private Button gameStartBtn, backHomeBtn;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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
                String difficultyTypeString = parent.getItemAtPosition(position).toString();
                difficultyType = DifficultyType.toEnum(difficultyTypeString, getResources());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                difficultyType = DifficultyType.Easy;
            }
        });

        //get value that user selected for question type, multiple choice or True/False
        questionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String questionTypeString = parent.getItemAtPosition(position).toString();
                questionType = QuestionType.toEnum(questionTypeString, getResources());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                questionType = QuestionType.Any;
            }
        });

        launchLayout = findViewById(R.id.launch_layout);
        timerSwitch = findViewById(R.id.timerSwitch);
        timerSwitch.setChecked(false);
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                if (isChecked) {
                    showSnackbar(getResources().getString(R.string.trivia_switch_on_message), true);
                } else {
                    showSnackbar(getResources().getString(R.string.trivia_switch_off_message), false);
                }
            }
        });

        // click start button, go to another page to play the game
        Intent goToGameStart = new Intent(TriviaGameLaunchActivity.this, TriviaGameOngoingActivity.class);
        gameStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get value that user input for question numbers
                String questionNum = questionNumberInput.getText().toString();
                String gameURL;
                if (questionType == QuestionType.Any) {
                    gameURL = "https://opentdb.com/api.php?amount=" + questionNum.trim() + "&difficulty=" + DifficultyType.toUrlString(difficultyType);
                } else {
                    gameURL = "https://opentdb.com/api.php?amount=" + questionNum.trim() + "&type=" + QuestionType.toUrlString(questionType) + "&difficulty=" + DifficultyType.toUrlString(difficultyType);
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

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.navigationOpen, R.string.navigationClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * Question class is a blueprint of questions of trivia game.
     * @param message String value of message showing in the snackBar
     * @param isOn boolean value of whether the snackBar is checked
     */
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
                startActivity(new Intent(TriviaGameLaunchActivity.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, SongsterSearch.class));
                break;
            case R.id.carDBItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, RatingSoccerAPI.class));
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
     * Called when an item in the navigation menu is selected.
     * @param item MenuItem: The selected item.
     * @return boolean: Return true to display the item as the selected item.
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            case R.id.backHomeItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, SongsterSearch.class));
                break;
            case R.id.carDBItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                startActivity(new Intent(TriviaGameLaunchActivity.this, RatingSoccerAPI.class));
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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }


}


package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SongsterSearch extends AppCompatActivity {
    /**
     * tool bar for other activities and help
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        toolbar = findViewById(R.id.songsterrToolbar);
        setSupportActionBar(toolbar);

    }

    /**
     * Inflates menu bar
     * @param menu menu to be inflated
     * @return if menu has been inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Handles actions of selected menu items
     * @param item which item has been selected
     * @return if actions have been successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.backHomeItem:
                startActivity(new Intent(SongsterSearch.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                //startActivity(new Intent(TriviaGameLaunchActivity.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SongsterSearch.this, SongsterSearch.class));
                Toast.makeText(this, "Go to songster page", Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
                Toast.makeText(this, "Go to car database page", Toast.LENGTH_LONG).show();
                break;
            case R.id.soccerItem:
                Toast.makeText(this, "Go to soccer game page", Toast.LENGTH_LONG).show();
                break;
            case R.id.helpItem:
                AlertDialog.Builder songsterHelpDialog = new AlertDialog.Builder(this);
                songsterHelpDialog.setTitle(getResources().getString(R.string.songsterHelpTile))
                        //What is the message:
                        .setMessage(getResources().getString(R.string.songsterInstructions1) + "\n"
                                + getResources().getString(R.string.songsterInstructions2) + "\n"
                                + getResources().getString(R.string.songsterInstructions3) + "\n"
                                + getResources().getString(R.string.songsterInstructions4) + "\n"
                                + getResources().getString(R.string.songsterInstructions5))
                        //What the No button does:
                        .setNegativeButton(getResources().getString(R.string.closeHelpDialog), (click, arg) -> {
                        })
                        //Show the dialog
                        .create().show();
                break;
        }
        return true;

    }
}

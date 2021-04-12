package com.cst2335.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SongsterSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
    /**
     * tool bar for other activities and help
     */
    private Toolbar toolbar;
    /**
     * ArrayList holding the search results
     */
    private ArrayList<SongsterrObject> search;
    /**
     * Progress bar to show progress while searching
     */
    private ProgressBar progressBar;
    /**
     * ImageButton that starts the search function
     */
    private ImageButton searchButton;
    /**
     * ImageButton that starts the favorite activity
     */
    private ImageButton goToFavButton;
    /**
     * Adapter to handle list view for displaying search results
     */
    private BaseAdapter myAdapter;
    /**
     * Database helper to handle adding to favorites
     */
    private SQLiteDatabase db;

    /**
     * String for item position text
     */
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_SONG_TITLE = "TITLE";
    public static final String ITEM_SONG_ID = "S_ID";
    public static final String ITEM_ARTIST_NAME = "NAME";
    public static final String ITEM_ARTIST_ID = "A_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        toolbar = findViewById(R.id.songsterrToolbar);
        setSupportActionBar(toolbar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigationOpen, R.string.navigationClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Database
        SongsterrDatabaseHelper dbOpener = new SongsterrDatabaseHelper(this);
        db = dbOpener.getWritableDatabase();

        //sharedPreference to show last search content
        SharedPreferences prefs = getSharedPreferences("songsterPrefs", Context.MODE_PRIVATE);
        String strSearch = prefs.getString("searchText", "");


        search = new ArrayList<>();
        ListView theList = findViewById(R.id.songsterrListSearch);
        myAdapter = new MyListAdapter();
        theList.setAdapter(myAdapter);

        progressBar = findViewById(R.id.songsterrProgress);

        EditText editSongsterr = findViewById(R.id.songsterrEdit);
        editSongsterr.setText(strSearch);

        searchButton = findViewById(R.id.songsterrButtonSearch);
        searchButton.setOnClickListener( clk ->
        {
            SharedPreferences.Editor edit = prefs.edit();

            edit.putString("searchText", editSongsterr.getText().toString());
            edit.commit();

            search = new ArrayList<>();

            SongsterrQuery query = new SongsterrQuery(editSongsterr.getText().toString());
            query.execute();

            //pop up a snackbar to show input searching content
            Snackbar.make(findViewById(R.id.songsterMain), R.string.searching+ editSongsterr.getText().toString(), Snackbar.LENGTH_LONG).show();
        });

        theList.setOnItemClickListener( (list, item, position, id) -> {
            SongsterrObject selectedItem = search.get(position);
            String getSongTitle = selectedItem.getSongName();
            String getSongID = selectedItem.getSongID();
            String getArtistName = selectedItem.getArtistName();
            String getArtistID = selectedItem.getArtistID();

            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SONG_TITLE,getSongTitle);
            dataToPass.putString(ITEM_SONG_ID,getSongID);
            dataToPass.putString(ITEM_ARTIST_NAME, getArtistName);
            dataToPass.putString(ITEM_ARTIST_ID,getArtistID);

            boolean isTablet = findViewById(R.id.songsterFragmentLocation) != null;

            if(isTablet)
            {
                SongsterrDetailFragment dFragment = new SongsterrDetailFragment();
                dFragment.setArguments( dataToPass );
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.songsterFragmentLocation, dFragment)
                        .commit();
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SongsterSearch.this, SongsterrEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition

            }
        });

        goToFavButton = findViewById(R.id.goToFavBtn);
        Intent goToFav = new Intent(SongsterSearch.this, SongsterrFavoritesList.class);
        Toast.makeText(this,R.string.goToFav, Toast.LENGTH_LONG).show();
        startActivity(goToFav);

    }

    /**
     * Adapter to help display listview
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         *size getter
         * @return size of listview
         */
        public int getCount() {
            return search.size();  }

        /**
         *
         * @param position position of the requested object
         * @return object at position
         */
        public SongsterrObject getItem(int position) {
            return search.get(position);  }

        /**
         *
         * @param p position of object
         * @return id of the requested object
         */
        public long getItemId(int p) {
            return p; }

        /**
         * inflates and returns view for display in listview
         *
         * @param p position of object
         * @param recycled old view to be recycled
         * @param parent
         * @return inflated vied for listview
         */
        public View getView(int p, View recycled, ViewGroup parent)
        {

            View thisRow = getLayoutInflater().inflate(R.layout.songsterr_listview_layout, null);

            TextView songName = thisRow.findViewById(R.id.songNameList );
            songName.setText(getItem(p).getSongName() );

            return thisRow;
        }
    }

    private class SongsterrQuery extends AsyncTask<String, Integer, String> {
        /**
         * Song name of the search result
         */
        private String songName;
        /**
         * Song ID of the search result
         */
        private String songId;
        /**
         * Artist name of the search result
         */
        private String artistName;
        /**
         * ArtistID of the search result
         */
        private String artistID;

        /**
         * text input from user
         */
        private String searchText;


        public SongsterrQuery(String searchText){
            this.searchText=searchText;
        }

        /**
         * Async task to to pull information from url and use information to create SongsterObjects
         * and adding those Objects to the arraylist of search results
         * @param strings
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {
            String ret = null;
            String queryURL = "http://www.songsterr.com/a/ra/songs.json?pattern="+searchText;
            SongsterrObject tempObj;
            int progress = 0;

            try {
                publishProgress(progress);

                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();

                progress+=50;
                publishProgress(progress);

                JSONArray songsterArray = new JSONArray(result);
                for(int i = 0; i < songsterArray.length(); i++){
                    progress+=5;
                    publishProgress(progress);
                    JSONObject songsterObj = songsterArray.getJSONObject(i);

                    songName = songsterObj.getString("title");
                    songId = songsterObj.getString("id");

                    String artist = songsterObj.getString("artist");
                    JSONObject artistObj = new JSONObject(artist);
                    artistID = artistObj.getString("id");

                    Boolean isUseThePrefix = artistObj.getBoolean("useThePrefix");

                    if(isUseThePrefix){
                        artistName = artistObj.getString("name");
                    }else{
                        artistName = artistObj.getString("nameWithoutThePrefix");
                    }

                    tempObj = new SongsterrObject(songName, songId, artistName, artistID);
                    search.add(tempObj);

                }

                publishProgress(100);

            }
            catch(MalformedURLException mfe){ ret = "Malformed URL exception"; }
            catch(IOException ioe)          { ret = "IO Exception. Is the Wifi connected?";}
            catch(JSONException e)          { ret = "Broken JSON";
                Log.e("Broken Json", e.getMessage()); }

            return ret;
        }

        /**
         * Updates the progress bar
         * @param values percentage progress to be displayed by progress bar
         */
        @Override
        protected void onProgressUpdate(Integer ... values) {
            super.onProgressUpdate(values);
            searchButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);

        }

        /**
         * Once asynctask is complete hide progress bar and notify adapter that data has been changed
         * @param sentFromDoInBackground
         */
        @Override
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);
            searchButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            myAdapter.notifyDataSetChanged();
        }
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

    // Needed for the OnNavigationItemSelected interface:
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }




}

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

/**
 * Launch page of songster search where user can input song title or artist name to search
 * @author Xueru Chen
 */
public class SongsterSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
    /**
     * toolbar for other activities and help
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
     * String for songster information details
     */
    public static final String ITEM_SONG_TITLE = "TITLE";
    public static final String ITEM_SONG_ID = "S_ID";
    public static final String ITEM_ARTIST_NAME = "NAME";
    public static final String ITEM_ARTIST_ID = "A_ID";

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        /**
         * Get toolbar from layout and set Toolbar
         */
        toolbar = findViewById(R.id.songsterrToolbar);
        setSupportActionBar(toolbar);

        /**
         * Get navigation drawer from layout and set NavigationDrawer
         */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigationOpen, R.string.navigationClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Set and open database
         */
        SongsterrDatabaseHelper dbOpener = new SongsterrDatabaseHelper(this);
        db = dbOpener.getWritableDatabase();

        /**
         * sharedPreference to show last search content
         */
        SharedPreferences prefs = getSharedPreferences("songsterPrefs", Context.MODE_PRIVATE);
        String strSearch = prefs.getString("searchText", "");

        /**
         * Get listview form layout and set Listview and adapter
         */
        search = new ArrayList<>();
        ListView theList = findViewById(R.id.songsterrListSearch);
        myAdapter = new MyListAdapter();
        theList.setAdapter(myAdapter);

        /**
         * Get progress bar from layout
         */
        progressBar = findViewById(R.id.songsterrProgress);

        /**
         * Get edit text from layout and set input editext
         */
        EditText editSongsterr = findViewById(R.id.songsterrEdit);
        editSongsterr.setText(strSearch);

        search = new ArrayList<>();

        /**
         * Get search button from layout and set click search button
         * click search button and search url
         */
        searchButton = findViewById(R.id.songsterrButtonSearch);
        searchButton.setOnClickListener(clk ->
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("searchText", editSongsterr.getText().toString());
            edit.commit();

            SongsterrQuery query = new SongsterrQuery(editSongsterr.getText().toString());
            query.execute();

            //pop up a snackbar to show input searching content
            Snackbar.make(findViewById(R.id.songsterMain), getString(R.string.search) + editSongsterr.getText().toString(), Snackbar.LENGTH_LONG).show();
        });

        theList.setOnItemClickListener((list, item, position, id) -> {
            /**
             * Get selected items details in variable separately
             */
            SongsterrObject selectedItem = search.get(position);
            String getSongTitle = selectedItem.getSongName();
            String getSongID = selectedItem.getSongID();
            String getArtistName = selectedItem.getArtistName();
            String getArtistID = selectedItem.getArtistID();

            /**
             * Pass the songster details
             */
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SONG_TITLE, getSongTitle);
            dataToPass.putString(ITEM_SONG_ID, getSongID);
            dataToPass.putString(ITEM_ARTIST_NAME, getArtistName);
            dataToPass.putString(ITEM_ARTIST_ID, getArtistID);

            /**
             * Check if it is a tablet
             */
            boolean isTablet = findViewById(R.id.songsterFragmentLocation) != null;
            if (isTablet) {
                SongsterrDetailFragment dFragment = new SongsterrDetailFragment();
                dFragment.setArguments(dataToPass);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.songsterFragmentLocation, dFragment)
                        .commit();
            } else{ //isPhone
                Intent nextActivity = new Intent(SongsterSearch.this, SongsterrEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        /**
         * Click Favorite button to SongsterrFavoritesList activity, show a alertdialog
         */
        goToFavButton = findViewById(R.id.goToFavBtn);
        goToFavButton.setOnClickListener(clk -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SongsterSearch.this);
            builder.setTitle(R.string.alertGotoFav);
            builder.setIcon(R.drawable.exclamation_mark);
            builder.setPositiveButton(R.string.yes, (e, arg) -> {
                Intent goToFav = new Intent(SongsterSearch.this, SongsterrFavoritesList.class);
                startActivity(goToFav);
                Toast.makeText(this, R.string.goToFav, Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton(R.string.no, (e, arg) -> { });
            builder.create().show();
        });
    }

    /**
     * Adapter to help display details in listview
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         * size getter
         * @return size of listview
         */
        public int getCount() {
            return search.size();  }

        /**
         * Get positon
         * @param position position of the requested object
         * @return object at position
         */
        public SongsterrObject getItem(int position) {
            return search.get(position);  }

        /**
         *Get id
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

    /**
     * This is the AsyncTask to connect to the link and get data from the API.
     */
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

        /**
         *
         * @param searchText
         */
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
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.backHomeItem:
                startActivity(new Intent(SongsterSearch.this, MainActivity.class));
                Toast.makeText(this, R.string.goToMain, Toast.LENGTH_LONG).show();
                break;
            case R.id.triviaItem:
                //startActivity(new Intent(SongsterSearch.this, TriviaGameLaunchActivity.class));
                Toast.makeText(this, R.string.goToTrivia, Toast.LENGTH_LONG).show();
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SongsterSearch.this, SongsterSearch.class));
                Toast.makeText(this, R.string.goToSongster, Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
//                startActivity(new Intent(SongsterSearch.this, CarDatabase.class));
                Toast.makeText(this, R.string.goToCarDatabase, Toast.LENGTH_LONG).show();
                break;
            case R.id.soccerItem:
//                startActivity(new Intent(SongsterSearch.this, SoccerGames.class));
                Toast.makeText(this, R.string.goToSoccerGame, Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(SongsterSearch.this, MainActivity.class));
                Toast.makeText(this, R.string.goToMain, Toast.LENGTH_LONG).show();
                break;
            case R.id.triviaItem:
                //startActivity(new Intent(SongsterSearch.this, TriviaGameLaunchActivity.class));
                Toast.makeText(this, R.string.goToTrivia, Toast.LENGTH_LONG).show();
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SongsterSearch.this, SongsterSearch.class));
                Toast.makeText(this, R.string.goToSongster, Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
//                startActivity(new Intent(SongsterSearch.this, CarDatabase.class));
                Toast.makeText(this, R.string.goToCarDatabase, Toast.LENGTH_LONG).show();
                break;
            case R.id.soccerItem:
//                startActivity(new Intent(SongsterSearch.this, SoccerGames.class));
                Toast.makeText(this, R.string.goToSoccerGame, Toast.LENGTH_LONG).show();
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

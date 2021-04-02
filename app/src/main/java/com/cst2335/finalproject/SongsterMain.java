package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class SongsterMain extends AppCompatActivity {

    /**
     * ArrayList holding the search results
     */
    private ArrayList<SongsterrObject> search;
    /**
     * Progress bar to show progress while searching
     */
    private ProgressBar progressBar;
    /**
     * tool bar for favorite, help, home commands and snackbar
     */
    private Toolbar toolbar;
    /**
     * button that starts the search function
     */
    private Button searchButton;
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
    /**
     * int to show being used for empty activity
     */
    public static final int EMPTY_ACTIVITY = 345;

    Button goToFav;

    /**
     * Main onCreate of songster search app
     * Creates db helper, toolbar, shared preferences, search arraylist, listview
     * Handles button presses on search button and list view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_main);

        SongsterrDatabaseHelper dbOpener = new SongsterrDatabaseHelper(this);
        db = dbOpener.getWritableDatabase();

//        toolbar = findViewById(R.id.songsterrToolbar);
//        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("songsterPrefs", Context.MODE_PRIVATE);
        String strSearch = prefs.getString("searchText", "");

        search = new ArrayList<>();

        ListView theList = findViewById(R.id.songsterrListSearch);
        theList.setAdapter( myAdapter = new MyListAdapter() );

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

           // Snackbar.make(findViewById(R.id.songsterMain),"Searching: "+ editSongsterr.getText().toString(), Snackbar.LENGTH_LONG);

        });

        theList.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(SongsterrDatabaseHelper.COL_SONGNAME, search.get(position).getSongName());
            dataToPass.putString(SongsterrDatabaseHelper.COL_SONGID, search.get(position).getSongID());
            dataToPass.putString(SongsterrDatabaseHelper.COL_ARTISTNAME, search.get(position).getArtistName());
            dataToPass.putString(SongsterrDatabaseHelper.COL_ARTISTID, search.get(position).getArtistID());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(SongsterrDatabaseHelper.COL_ID, position);

            boolean isTablet = findViewById(R.id.songsterFragmentLocation) != null;

            if(isTablet)
            {
                SongsterrDetailFragment dFragment = new SongsterrDetailFragment();
                dFragment.setArguments( dataToPass );
                dFragment.setTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.songsterFragmentLocation, dFragment)
                        .addToBackStack("AnyName")
                        .commit();
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SongsterMain.this, SongsterrEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition

            }
        });

//        goToFav =  findViewById(R.id.goFav);
//        goToFav.setOnClickListener(clk->{
//            Intent favPage = new Intent(SongsterMain.this, SongsterrFavoritesList.class);
//            startActivity(favPage);
//        });


    }

    /**
     * Method to check if the results were successful from the empty activity.
     * If proper codes are given take the id from the data and add to favorites
     *
     * @param requestCode int to check if the result came from the empty activity
     * @param resultCode int to check if the activity finished successfully
     * @param data data from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getIntExtra(SongsterrDatabaseHelper.COL_ID, 0);
                addToFavorite((int)id);
            }
        }
    }

    /**
     * Method that take an id of the item and then adds that item to the favorites database
     * @param id id of the item being added to favorites
     */
    public void addToFavorite(int id){
        SongsterrObject addObj = search.get(id);

        ContentValues newRowValues = new ContentValues();

        newRowValues.put(SongsterrDatabaseHelper.COL_SONGNAME, addObj.getSongName());
        newRowValues.put(SongsterrDatabaseHelper.COL_SONGID, addObj.getSongID());
        newRowValues.put(SongsterrDatabaseHelper.COL_ARTISTNAME, addObj.getArtistName());
        newRowValues.put(SongsterrDatabaseHelper.COL_ARTISTID, addObj.getArtistID());

        long newId = db.insert(SongsterrDatabaseHelper.TABLE_NAME, null, newRowValues);
        if(newId > 0 ){
            Toast.makeText(this, "Added to Favorite", Toast.LENGTH_LONG).show();
        }

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
            songName.setText("Song name: " + getItem(p).getSongName() );

            TextView artistName = thisRow.findViewById(R.id.artistNameList);
            artistName.setText("Artist Name: " + getItem(p).getArtistName() );

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

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.songsterr_menu, menu);

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

            case R.id.help:
                View middle = getLayoutInflater().inflate(R.layout.songsterr_dialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setView(middle);

                builder.create().show();
                break;

            case R.id.goFav:
                Intent favPage = new Intent(SongsterMain.this, SongsterrFavoritesList.class);
                startActivity(favPage);
                break;

            case R.id.goHome:
                Intent homePage = new Intent(SongsterMain.this, MainActivity.class);
                Snackbar.make(toolbar, "Go to Home Page?", Snackbar.LENGTH_LONG)
                        .setAction("Confirm", e -> startActivity(homePage)).show();

                break;

        }
        return true;
    }


}
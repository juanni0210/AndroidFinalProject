
package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SongsterSearch extends AppCompatActivity {

    /**
     * Button used for search
     */
    private Button searchButton;
    /**
     * Button used for favourite page
     */
    private Button favouritesButton;
    /**
     * edit text for user input search content
     */
    private EditText searchEditText;
    /**
     * Progress bar to show process
     */
    private ProgressBar mProgressBar;
    /**
     * toolbar at the top
     */
    private Toolbar mainToolbar;
    /**
     * Listview to show the search result
     */
    private ListView songsterView;
    /**
     * Array list to all songster object
     */
    private ArrayList<SongsterObject> songsterList = new ArrayList<>();
    /**
     * set a adapter
     */
    private SongsterAdapter songsterAdapter = new SongsterAdapter();
    /**
     * sharedpre to store last time search result
     */
    //private SharedPreferences sharedPref;

    private String url = "http://www.songsterr.com/a/ra/songs.xml?pattern=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        /**
         * set each element to id in layout
         */
        searchButton = findViewById(R.id.searchButton);
        favouritesButton = findViewById(R.id.goToFavourites);
        searchEditText = findViewById(R.id.search_editText);
        mProgressBar = findViewById(R.id.progress_bar);
        mainToolbar = findViewById(R.id.main_toolbar_songster);
        songsterView = findViewById(R.id.songsterListView);

        songsterView.setAdapter(songsterAdapter);
        //sharedPref = getSharedPreferences("Songster", MODE_PRIVATE);

//        /**
//         * set tool bar
//         */
//        mProgressBar.setVisibility(View.VISIBLE);
//        setSupportActionBar(mainToolbar);

        Button goBackBtn = findViewById(R.id.backButton);
        goBackBtn.setOnClickListener(btn->{

            Intent goBack = new Intent(SongsterSearch.this, MainActivity.class);
            startActivity(goBack);

        });

        /**
         * CLick on Search button and show the search result and a snackbar
         */
        searchButton.setOnClickListener(clk->{
            /**
             * show snackbar with text from searchbar
             */
            Snackbar.make(findViewById(R.id.search_page),
                    ("Searching: "+searchEditText.getText().toString()), Snackbar.LENGTH_LONG)
                    .show();
            /**
             *
             */
            SongsterQuery search = new SongsterQuery();
            search.execute(url+searchEditText.getText().toString());
        });

        /**
         * click songster listview to show details information of songs
         */
        songsterView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, SongsterDetails.class);
            SongsterObject songsterObject = songsterAdapter.getItem(position);
            //intent.putExtra("SongID", songsterObject.getSongID());
            intent.putExtra("Song", songsterObject.getSongName());
            //intent.putExtra("ArtistID", songsterObject.getArtistID());
            intent.putExtra("Artist", songsterObject.getArtistName());
//            intent.putExtra("useThePrefix", songsterObject.isUseThePrefix());
//            intent.putExtra("chordsPresent", songsterObject.isChordsPresent());
//            intent.putExtra("TabType", songsterObject.getTabType());
            startActivity(intent);
        });

        /**
         * long click listView to show a alter dialog to ask if what add to favourite list
         */
        songsterView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            SongsterObject songsterObject = (SongsterObject)  parent.getAdapter().getItem(position);
            /**
             * Alert dialog to let user choose if they want to add to favourite list
             */
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(getString(R.string.add_to_favourite))
                    .setPositiveButton(R.string.yes, (DialogInterface dialog, int which) -> {
//                        songsterObject.add(SongsterDatabaseOpenHelper.TABLE_NAME, SongsterDatabaseOpenHelper.COL_ID + "=" + songsterObject.getId(), null);
//                        songsterObject.add(position);
                        songsterAdapter.notifyDataSetChanged();
                        Toast.makeText(SongsterSearch.this, R.string.add, Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return songsterView.isLongClickable();
        });

        /**
         * Press Button go to favourite list.
         */
        favouritesButton.setOnClickListener(clk-> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(getString(R.string.go_to_favourite))
                    .setPositiveButton(R.string.yes, (DialogInterface dialog, int which) -> {
                        Intent intent = new Intent(SongsterSearch.this, SongsterFavouriteList.class);
                        startActivity(intent);
                        Toast.makeText(SongsterSearch.this, R.string.welcom_to_favourite, Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

        });

//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("search", searchEditText.getText().toString());
//        editor.apply();

    }

    private class SongsterQuery extends AsyncTask<String, Integer, String>{
        long songID;
        String songName;
        long artistID;
        String artistName;
        boolean useThePrefix;
        boolean chordsPresent;
        String tabType;

        @Override
        protected String doInBackground(String... args) {

            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jObject = new JSONObject(result);
                JSONArray modelJsonArray = jObject.getJSONArray("Results");
                for(int i = 0; i< modelJsonArray.length(); i++) {
                    JSONObject songsterObj = (JSONObject) modelJsonArray.get(i);
                    //songID = songsterObj.getLong("songID");
                    songName = songsterObj.getString("songName");
                    //artistID = songsterObj.getLong("artistID");
                    artistName = songsterObj.getString("artistName");
//                    useThePrefix = songsterObj.getBoolean("useThePrefix");
//                    chordsPresent = songsterObj.getBoolean("chordsPresent");
//                    tabType = songsterObj.getString("tabType");



                    SongsterObject songsterObject = new SongsterObject(songName, artistName);
                    songsterList.add(songsterObject);
                }

                for(int i = 0; i < songsterList.size(); i++) {
                    System.out.println("test" + songsterList.get(i). getSongName());
                    System.out.println("test" + songsterList.get(i). getArtistName());
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return "Done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(values[0]);
        }

    }

    protected class SongsterAdapter extends BaseAdapter {
        /**
         * returns the number of items
         * @return
         */
        @Override
        public int getCount() {
            return songsterList.size();
        }

        /**
         * returns an item from a specific position
         * @param position
         * @return
         */
        @Override
        public SongsterObject getItem(int position) {
            return songsterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * return a story title to be shown at row position
         * @param position
         * @param old
         * @param parent
         * @return
         */
        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = SongsterSearch.this.getLayoutInflater();
            View newView = inflater.inflate(R.layout.activity_songster_search_content, null);
            SongsterObject songsterObject = getItem(position);

            TextView searchResultSong = newView.findViewById(R.id.searchResultSong);
            TextView searchResultArtist = newView.findViewById(R.id.searchResultArtist);
            assert songsterObject != null;
            searchResultSong.setText(songsterObject.getSongName());
            searchResultArtist.setText(songsterObject.getArtistName());

            return newView;
        }
    }
}
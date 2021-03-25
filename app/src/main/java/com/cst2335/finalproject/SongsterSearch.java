
package com.cst2335.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SongsterSearch extends AppCompatActivity {

    private Button searchButton;
    private Button favouritesButton;
    private EditText searchEditText;
    private ProgressBar mProgressBar;
    private Toolbar mainToolbar;

    private ListView songsterView;
    private ArrayList<SongsterObject> songsterList = new ArrayList<>();
    private SongsterAdapter songsterAdapter = new SongsterAdapter();
    private SharedPreferences sharedPref;

    private String url = "http://www.songsterr.com/a/ra/songs.xml?pattern=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        /**
         * set each element
         */
        searchButton = findViewById(R.id.searchButton);
        favouritesButton = findViewById(R.id.goToFavourites);
        searchEditText = findViewById(R.id.search_editText);
        mProgressBar = findViewById(R.id.progress_bar);
        mainToolbar = findViewById(R.id.main_toolbar_songster);

        songsterView.setAdapter(songsterAdapter);
        sharedPref = getSharedPreferences("Songster", MODE_PRIVATE);
        mProgressBar.setVisibility(View.VISIBLE);


        /**
         * Search button
         */
        searchButton.setOnClickListener(clk->{
            /**
             * show snackbar with text from searchbar
             */
            Snackbar.make(findViewById(R.id.search_page),
                    ("Searching: "+searchEditText.getText().toString()), Snackbar.LENGTH_LONG)
                    .show();
            new SongsterQuery().execute();

        });

        songsterView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, SongsterDetails.class);
            SongsterObject songsterObject = songsterAdapter.getItem(position);

            intent.putExtra("Song", songsterObject.getSongName());
            intent.putExtra("Artist", songsterObject.getArtistName());
            intent.putExtra("useThePrefix", songsterObject.isUseThePrefix());
            intent.putExtra("chordsPresent", songsterObject.isChordsPresent());
            startActivity(intent);
        });

        songsterView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            SongsterObject songsterObject = (SongsterObject)  parent.getAdapter().getItem(position);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(getString(R.string.add_to_favourite))
                    .setPositiveButton(R.string.yes, (DialogInterface dialog, int which) -> {
                        songsterAdapter.notifyDataSetChanged();
                        Toast.makeText(SongsterSearch.this, R.string.add, Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

            return songsterView.isLongClickable();
        });

        //to go to favourite list.
        favouritesButton.setOnClickListener(clk-> {
                Intent intent = new Intent(SongsterSearch.this, SongsterFavouriteList.class);
                startActivity(intent);
        });

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("search", searchEditText.getText().toString());
        editor.apply();

    }

    private class SongsterQuery extends AsyncTask<String, Integer, String>{
        String songID;
        String songName;
        String artistID;
        String  artistName;
        String useThePrefix;
        String chordsPresent;

        @Override
        protected String doInBackground(String... strings) {

            publishProgress(0);
            try {
                String urlString = "http://www.songsterr.com/a/ra/songs.xml?pattern=" + searchEditText.getText().toString();
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream inStream = conn.getInputStream();

                conn.setRequestMethod("GET");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(inStream, "UTF-8");  //inStream comes from line 46

                while(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if(xpp.getEventType() == XmlPullParser.START_TAG){
                        String tagName = xpp.getName();

                        if(tagName.equals("Song")) {
                            songID = xpp.getAttributeValue(null,"id");
                            publishProgress(10);
                        }else if(tagName.equals("title")){
                            xpp.next();
                            songName = xpp.getName();
                            publishProgress(25);
                        }else if(tagName.equals("artist ")){
                            artistID = xpp.getAttributeValue(null,"id");
                            publishProgress(45);
                        }else if(tagName.equals("name ")){
                            xpp.next();
                            artistName = xpp.getName();
                            publishProgress(60);
                        }else if(tagName.equals("useThePrefix ")){
                            xpp.next();
                            useThePrefix = xpp.getName();
                            publishProgress(75);
                        }else if(tagName.equals("chordsPresent ")){
                            xpp.next();
                            chordsPresent = xpp.getName();
                            publishProgress(90);
                        }
                    }
                    xpp.next();
                }

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            return "finished";
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
         *
         * @return
         */
        @Override
        public int getCount() {
            return songsterList.size();
        }

        /**
         * returns an item from a specific position
         *
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
         *
         * @param position
         * @param old
         * @param parent
         * @return
         */
        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = SongsterSearch.this.getLayoutInflater();
            View newView = inflater.inflate(R.layout.songster_search_content, null);
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
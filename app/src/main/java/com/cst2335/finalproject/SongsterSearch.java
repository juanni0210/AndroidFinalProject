
package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SongsterSearch extends AppCompatActivity {

    /**
     * name of current activity
     */
    public static final String ACTIVITY_NAME = "SONGSTER_SEARCH_PAGE";
    /**
     * list of Songster
     */
    ArrayList<SongsterObject> songsterObject = new ArrayList<>();
    /**
     * Field from the screen accepts input from a user
     */
    private EditText searchText;
    /**
     * List with songster items from the screen
     */
    private ListView songsterList;
    /**
     *
     */
    private Button searchButton;
    /**
     * shared preferences instance
     */
    private SharedPreferences sharedPref;
    /**
     * toolbar instance
     */
    private Toolbar main_menu;
    /**
     * progress bar instance
     */
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        searchButton = findViewById(R.id.searchButton);
        searchText = findViewById(R.id.search_editText);
        progressBar = findViewById(R.id.progress_bar);
        main_menu = findViewById(R.id.main_toolbar_songster);
        songsterList = findViewById(R.id.songsterListView);

        progressBar.setVisibility(View.INVISIBLE);
        sharedPref = getSharedPreferences("SongsterPref", MODE_PRIVATE);



        /**
         * CLick on Search button and show the search result and a snackbar
         */
        searchButton.setOnClickListener(v ->
        {
            String url = "http://www.songsterr.com/a/ra/songs.json?pattern="+searchText.getText().toString();
            SongsterQuery downloadSongster = new SongsterQuery();
            downloadSongster.execute(url);


            searchText.onEditorAction(EditorInfo.IME_ACTION_DONE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("searchText", searchText.getText().toString());

            editor.apply();
        });

        /**
         * click songster listview to show details information of songs
         */
        songsterList.setOnItemClickListener(( parent,  view,  position,  id) ->{
            SongsterObject chosenOne = songsterObject.get(position);
            Bundle dataToPass = new Bundle();
            dataToPass.putSerializable("SongsterObject", chosenOne);

            if (isTablet) {
                SongsterFragment dFragment = new SongsterFragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not

                getSupportFragmentManager()

                        .beginTransaction()

                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout

                        .commit(); //actually load the fragment.
            } else //isPhone
            {
                Intent nextActivity = new Intent(SongsterSearch.this, SongsterDetailContainer.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        String searchTextValue = sharedPref.getString("searchText", "");
        searchText.setText(searchTextValue);

    }

    private class SongsterQuery extends AsyncTask<String, Integer, String>{
        /**
         * dialog to show a progression of downloading to the user
         */
        //private ProgressDialog p;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }
        /**
         * Methods connects to the server, retrieves the data about car charging stations
         * @param urls link to the server
         * @return data about car charging stations
         */
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                URL link = new URL(urls[0]);
                urlConnection = (HttpURLConnection) link.openConnection();
                publishProgress(25);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert urlConnection != null;
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                publishProgress(50);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                publishProgress(70);
                Log.e("connect", result.toString());
                return result.toString();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        /**
         * Method gets songster and displays them on the screen as a list of items
         * @param result data about songster retrieved by doInBackground method()
         */
        protected void onPostExecute(String result) {
            Log.e("onPostExe",result);
            songsterObject.clear(); // remove old results
            try {
                JSONArray songsterArray = new JSONArray(result);
                SongsterObject songsterObject = new SongsterObject();

                for(int i = 0; i < songsterArray.length(); i++){

                    JSONObject songsterObj = (JSONObject) songsterArray.get(i);
                    songsterObject.setSongID(songsterObj.getLong("id"));
                    songsterObject.setSongName(songsterObj.getString("title"));
                    //songsterObject.setChordsPresent(songsterObj.getBoolean("chordsPresent"));
                    Log.e("song", String.valueOf(songsterObj.getLong("id")));
                    Log.e("songName", songsterObj.getString("title"));

                    String artist = songsterObj.getString("artist");
                    JSONObject artistObj = new JSONObject(artist);
                    songsterObject.setArtistID(artistObj.getLong("id"));

                    Log.e("artist", String.valueOf(artistObj.getLong("id")));

                    Boolean isUseThePrefix = artistObj.getBoolean("useThePrefix");

                    if(isUseThePrefix){
                        songsterObject.setArtistName(artistObj.getString("name"));
                        Log.e("artistName",artistObj.getString("name"));
                    }else{
                        songsterObject.setArtistName(artistObj.getString("nameWithoutThePrefix"));
                        Log.e("artistNameWithout",artistObj.getString("nameWithoutThePrefix"));
                    }

//                    JSONArray typeObj = new JSONArray("tabTypes");
//                    songsterObject.setTabType(typeObj.getString(0));



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SongsterAdapter adapter = new SongsterAdapter(getApplicationContext(), songsterObject);
            songsterList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
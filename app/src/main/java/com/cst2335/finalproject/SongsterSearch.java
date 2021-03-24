package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SongsterSearch extends AppCompatActivity {

    private Button searchButton;
    private Button favouritesButton;
    private EditText searchEditText;
    private ProgressBar mProgressBar;
    private ListView songsterView;
    private Toolbar main_menu;
    private SharedPreferences sharedPref;

    private String url = "http://www.songsterr.com/a/ra/songs.xml?pattern=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_search);

        searchButton = findViewById(R.id.searchButton);
        favouritesButton = findViewById(R.id.goToFavourites);
        searchEditText = findViewById(R.id.search_editText);
        mProgressBar = findViewById(R.id.progress_bar);
        main_menu = findViewById(R.id.main_menu_songster);

        mProgressBar.setVisibility(View.VISIBLE);

        //Search button
        searchButton.setOnClickListener(clk->{
            /*
              show snackbar with text from searchbar
             */
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("search", searchEditText.getText().toString());
            editor.apply();

            /**
             *
             */
            Snackbar.make(findViewById(R.id.search_page),
                    ("Searching: "+searchEditText.getText().toString()), Snackbar.LENGTH_LONG)
                    .show();

            new SongsterQuery().execute(url+searchEditText.getText().toString());

        });

        //to go to favourite list.
        favouritesButton.setOnClickListener(clk-> {
                Intent intent = new Intent(SongsterSearch.this, SongsterFavouriteList.class);
                startActivity(intent);
        });
    }

    private class SongsterQuery extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String urlString="http://www.songsterr.com/a/ra/songs.xml?pattern="+searchEditText.getText().toString();

            try {
                URL url= new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream inStream = conn.getInputStream();

                conn.setRequestMethod("GET");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( inStream  , "UTF-8");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (XmlPullParserException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setVisibility(View.VISIBLE);
            //Log.i("AsyncTaskExample", "update:" + values[0]);
            //messageBox.setText("At step:" + values[0]);
            mProgressBar.setProgress(values[0]);
        }

    }
}
package com.cst2335.finalproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Fragment for display information about selected Search chose from list view
 * Handles buttons actions in fragment
 * @author Xueru Chen
 */
public class SongsterrDetailFragment extends Fragment {
    /**
     * Data of the item being displayed in fragment
     */
    private Bundle dataFromActivity;
    /**
     * Song name
     */
    private String songName;
    /**
     * Song ID
     */
    private String songID;
    /**
     * Artist Name
     */
    private String artistName;
    /**
     * Artist ID
     */
    private String artistID;
    /**
     * ImageButton click back to songster main
     */
    private ImageButton goToSongsterMain;
    /**
     * ArrayList holding the search results
     */
    public static ArrayList<SongsterrObject> search = new ArrayList<>();


    public SongsterrDetailFragment(){

    }

    /**
     * Displays information about song name and artist in the database
     * Handles the buttons press of the backbutton the gobutton and the add to favorties button
     * @param inflater inflater used ti inflate the fragment
     * @param container container to hold fragment
     * @param savedInstanceState
     * @return inflated fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.songsterr_search_detail, container, false);
        songName = dataFromActivity.getString(SongsterSearch.ITEM_SONG_TITLE);
        songID = dataFromActivity.getString(SongsterSearch.ITEM_SONG_ID);
        artistName = dataFromActivity.getString(SongsterSearch.ITEM_ARTIST_NAME);
        artistID = dataFromActivity.getString(SongsterSearch.ITEM_ARTIST_ID);

        TextView textSongName = result.findViewById(R.id.detailSongName);
        textSongName.setText(getString(R.string.songTitle) + songName);

        TextView textSongID = result.findViewById(R.id.detailSongID);
        textSongID.setText(getString(R.string.songID) + songID);

        TextView textArtistName = result.findViewById(R.id.detailArtistName);
        textArtistName.setText(getString(R.string.artistName) + artistName);

        TextView textArtistID = result.findViewById(R.id.detailArtistID);
        textArtistID.setText(getString(R.string.artistID) + artistID);

        /**
         * click on songID to launch a browser to the song’s guitar music page
         */
        textSongID.setOnClickListener(v -> {
            String url = "http://www.songsterr.com/a/wa/song?id="+songID ;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(url) );
            startActivity(i);
        });


        /**
         * click on artistID to launch a web browser to the artist’s list of songs
         */
        textArtistID.setOnClickListener(v -> {
            String url = "http://www.songsterr.com/a/wa/artist?id="+artistID ;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(url) );
            startActivity(i);
        });

        /**
         * Click ImageButton to Songster Main
         */
        goToSongsterMain = result.findViewById(R.id.songsterrMain);
        goToSongsterMain.setOnClickListener(clk->{
            Intent goToSongterMain = new Intent(container.getContext(), SongsterSearch.class);
            startActivity(goToSongterMain);
        });

        /**
         * Connect to database
         */
        SongsterrDatabaseHelper dbOpener = new SongsterrDatabaseHelper(container.getContext());
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        /**
         * Add to database
         */
        ImageButton addFavButton = result.findViewById(R.id.songsterrDetailFavoriteButton);
        addFavButton.setOnClickListener( clk -> {
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(SongsterrDatabaseHelper.COL_SONGNAME, songName);
            newRowValues.put(SongsterrDatabaseHelper.COL_SONGID, songID);
            newRowValues.put(SongsterrDatabaseHelper.COL_ARTISTNAME, artistName);
            newRowValues.put(SongsterrDatabaseHelper.COL_ARTISTID, artistID);
            long newId = db.insert(SongsterrDatabaseHelper.TABLE_NAME, null, newRowValues);

            SongsterrObject oneItem = new SongsterrObject(songName, songID, artistName, artistID, newId);
            search.add(oneItem);

            Toast.makeText(container.getContext(), R.string.add_to_favourite,Toast.LENGTH_SHORT).show();

        });

        /**
         * Click ImageButton to
         */
        Button favoriteBtn = result.findViewById(R.id.songsterrDetailGoButton);
        favoriteBtn.setOnClickListener(click -> {
            Intent goToSaved = new Intent(container.getContext(), SongsterrFavoritesList.class);
            startActivity(goToSaved);
            Toast.makeText(container.getContext(), R.string.goToFav, Toast.LENGTH_LONG).show();
        });


        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        AppCompatActivity parentActivity = (AppCompatActivity) context;
    }
}

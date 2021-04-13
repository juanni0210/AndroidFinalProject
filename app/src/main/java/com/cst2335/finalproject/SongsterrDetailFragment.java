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
 * SongsterrDetailFragment is a piece of an application's user interface or behavior that can be placed in an Activity.
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
     * Called to have the fragment instantiate its user interface view.
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return View: the View for the fragment's UI, or null.
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

        //Get text view from layout and set text
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
         * Click button to add to database
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
         * Click ImageButton to go to favorites list page
         */
        Button favoriteBtn = result.findViewById(R.id.songsterrDetailGoButton);
        favoriteBtn.setOnClickListener(click -> {
            Intent goToSaved = new Intent(container.getContext(), SongsterrFavoritesList.class);
            startActivity(goToSaved);
            Toast.makeText(container.getContext(), R.string.goToFav, Toast.LENGTH_LONG).show();
        });
        return result;
    }

    /**
     * Called when a fragment is first attached to its context.
     * @param context Context.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        AppCompatActivity parentActivity = (AppCompatActivity) context;
    }
}

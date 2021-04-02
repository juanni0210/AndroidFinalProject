package com.cst2335.finalproject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Fragment for display information about selected Search chose from list view
 * Handles buttons actions in fragment
 */
public class SongsterrDetailFragment extends Fragment {
    /**
     * Is the object a tablet
     */
    private boolean isTablet;
    /**
     * Data of the item being displayed in fragment
     */
    private Bundle dataFromActivity;
    /**
     * id of item
     */
    private int id;
    /**
     * position of item
     */
    private int pos;
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
     * Sets boolean containing if the device is a tablet or not
     * @param tablet Is the device a tablet
     */
    public void setTablet(boolean tablet) { isTablet = tablet; }

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
        id = dataFromActivity.getInt("POSITION");
        songName = dataFromActivity.getString(SongsterrDatabaseHelper.COL_SONGNAME);
        songID = dataFromActivity.getString(SongsterrDatabaseHelper.COL_SONGID);
        artistName = dataFromActivity.getString(SongsterrDatabaseHelper.COL_ARTISTID);
        artistID = dataFromActivity.getString(SongsterrDatabaseHelper.COL_ARTISTID);

        View result =  inflater.inflate(R.layout.songsterr_search_detail, container, false);

        TextView textTitle = (TextView)result.findViewById(R.id.detailSongName);
        textTitle.setText("Song Name: " + songName);

        TextView textPhone = (TextView)result.findViewById(R.id.detailSongID);
        textPhone.setText("Song ID:" + songID);

        TextView textLat = (TextView)result.findViewById(R.id.detailArtistName);
        textLat.setText("Artist Name:" + artistName);

        TextView textLon = (TextView)result.findViewById(R.id.detailArtistID);
        textLon.setText("Artist :" + artistID);



        Button addFavButton = (Button)result.findViewById(R.id.songsterrDetailFavoriteButton);
        addFavButton.setOnClickListener( clk -> {

            if(isTablet) {
                SongsterMain parent = (SongsterMain) getActivity();
                parent.addToFavorite(id);

            }
            //for Phone:
            else             {
                SongsterrEmptyActivity parent = (SongsterrEmptyActivity) getActivity();
                Intent backToFragmentExample = new Intent();
                backToFragmentExample.putExtra(SongsterrDatabaseHelper.COL_ID, id );

                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                parent.finish();
            }
        });

//        Button navigateButton = (Button)result.findViewById(R.id.songsterrDetailGoButton);
//        navigateButton.setOnClickListener( clk -> {
//
//            String strUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " (" + title + ")";
//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
//            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//            startActivity(intent);
//        });


        return result;
    }
}

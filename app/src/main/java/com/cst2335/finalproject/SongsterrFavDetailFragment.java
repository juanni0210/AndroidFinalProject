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
 * Fragment for display information about selected Favorite chose from list view
 * Handles buttons actions in fragment
 */
public class SongsterrFavDetailFragment extends Fragment {
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
     * Song Name
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
     * Handles the buttons press of the backbutton the gobutton and the remove from favorties button
     * @param inflater inflater used ti inflate the fragment
     * @param container container to hold fragment
     * @param savedInstanceState
     * @return inflated fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        pos = dataFromActivity.getInt(SongsterMain.ITEM_POSITION);
        id = (int)dataFromActivity.getLong(SongsterrDatabaseHelper.COL_ID);
        songName = dataFromActivity.getString(SongsterrDatabaseHelper.COL_SONGNAME);
        songID = dataFromActivity.getString(SongsterrDatabaseHelper.COL_SONGID);
        artistName = dataFromActivity.getString(SongsterrDatabaseHelper.COL_ARTISTNAME);
        artistID = dataFromActivity.getString(SongsterrDatabaseHelper.COL_ARTISTID);

        View result =  inflater.inflate(R.layout.songsterr_fav_fragment, container, false);

        TextView idTitle = (TextView)result.findViewById(R.id.songsterrFavDetailId);
        idTitle.setText("ID: " + id);

        TextView textSongName = (TextView)result.findViewById(R.id.songsterrFavDetailSongName);
        textSongName.setText("Song Name: " + songName);

        TextView textSongID = (TextView)result.findViewById(R.id.SongsterrFavDetailSongID);
        textSongID.setText("Song ID:" + songID);

        TextView textArtistName = (TextView)result.findViewById(R.id.songsterrFavDetailArtistName);
        textArtistName.setText("Artist Name:" + artistName);

        TextView textArtistID = (TextView)result.findViewById(R.id.songsterrFavDetailArtistID);
        textArtistID.setText("Artist ID:" + artistID);

//        ImageButton closeButton = (ImageButton)result.findViewById(R.id.carFavFragBackButton);
//        closeButton.setOnClickListener( clk -> {
//
//
//            if(isTablet) {
//                CarFavoritesList parent = (CarFavoritesList)getActivity();
//
//                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
//            }
//            //for Phone:
//            else //You are only looking at the details, you need to go back to the previous list page
//            {
//                CarFavEmpty parent = (CarFavEmpty) getActivity();
//                Intent backToFragmentExample = new Intent();
//
//
//                parent.setResult(Activity.RESULT_CANCELED, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
//                parent.finish(); //go back
//            }
//        });

        Button removeFavButton = (Button)result.findViewById(R.id.songsterrDetailFavoriteButton);
        removeFavButton.setOnClickListener( clk -> {


            if(isTablet) {
                SongsterrFavoritesList parent = (SongsterrFavoritesList) getActivity();
                parent.removeFromFavorite(id, pos);

                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                SongsterrFavEmpty parent = (SongsterrFavEmpty) getActivity();
                Intent backToFragmentExample = new Intent();
                backToFragmentExample.putExtra(SongsterrDatabaseHelper.COL_ID, id );
                backToFragmentExample.putExtra(SongsterMain.ITEM_POSITION, pos );

                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });

//        Button navigateButton = (Button)result.findViewById(R.id.carFavDetailGoButton);
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

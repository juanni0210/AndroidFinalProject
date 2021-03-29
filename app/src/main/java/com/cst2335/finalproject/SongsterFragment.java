package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;

/**
 * Class shows car charging station's details, loads station on a google map
 */
public class SongsterFragment extends Fragment {
    private boolean isTablet;
    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }
    /**
     * Method loads station's details on the screen, loads station on a google map, adds station to the list of favourite stations
     * @param savedInstanceState reference to a Bundle object that is passed into the onCreate method
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        assert bundle != null;
        SongsterObject songsterObject = (SongsterObject) bundle.getSerializable("SongsterObject");
        View result = inflater.inflate(R.layout.activity_songster_detail, container, false);

        TextView songName = result.findViewById(R.id.songName);
        String checkSongName = songsterObject.getSongName();
        songName.setText(checkSongName);

        TextView artistName = result.findViewById(R.id.artistName);
        String checkArtistName = songsterObject.getArtistName();
        artistName.setText(checkArtistName);

        TextView isUseThePrefix = result.findViewById(R.id.isUseThePrefix);
        Boolean checkIsUseThePrefix = songsterObject.isUseThePrefix();
        isUseThePrefix.setText(Boolean.toString(checkIsUseThePrefix));

        TextView isChorsdPresent = result.findViewById(R.id.isChorsdPresent);
        Boolean checkIsChorsdPresent = songsterObject.isChordsPresent();
        isChorsdPresent.setText(Boolean.toString(checkIsChorsdPresent));

//        TextView tabType = result.findViewById(R.id.tabType);
//        String checkTabType = songsterObject.getTabType();
//        tabType.setText(checkTabType);


        SongsterDatabaseOpenHelper dbOpener = new SongsterDatabaseOpenHelper(this.getActivity());
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        Button addBtn = result.findViewById(R.id.AddBtn);
        addBtn.setOnClickListener(click -> {
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(SongsterDatabaseOpenHelper.COL_SongID, songsterObject.getSongID());
            newRowValues.put(SongsterDatabaseOpenHelper.COL_SongName, checkSongName);
            newRowValues.put(SongsterDatabaseOpenHelper.COL_ArtistID, songsterObject.getArtistID());
            newRowValues.put(SongsterDatabaseOpenHelper.COL_ArtistName, checkArtistName);
            newRowValues.put(SongsterDatabaseOpenHelper.COL_UseThePrefix, checkIsUseThePrefix);
            newRowValues.put(SongsterDatabaseOpenHelper.COL_ChordsPresent, checkIsChorsdPresent);
            //newRowValues.put(SongsterDatabaseOpenHelper.COL_TabType, checkTabType);


            long newId = db.insert(SongsterDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            AlertDialog dialog = builder.setMessage("Add to the favourite list?")
                    .setPositiveButton("OK", (d,w) ->{
                        //snack bar
                    })
                    .setNegativeButton("No",(d,w) ->{})
                    .create();
            dialog.show();
        });

        Button seeListBtn = result.findViewById(R.id.listOfFavouritesButton);
        seeListBtn.setOnClickListener(clk -> {
            Intent nextPage = new Intent(this.getContext(), SongsterFavouriteList.class);
            startActivity(nextPage);
        });

        return result;
    }
}
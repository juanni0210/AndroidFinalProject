package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SongsterFavouriteList extends AppCompatActivity {

    ArrayList<SongsterObject> favSongster = new ArrayList<SongsterObject>();
    /**
     * position of a station in a list which user clicks
     */
    private int positionClicked;
    /**
     * adapter for the ListView
     */
    SongsterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_favourite_list);

        Button goBackBtn = findViewById(R.id.backButton);
        goBackBtn.setOnClickListener(btn -> {
            Intent goBack = new Intent(SongsterFavouriteList.this, SongsterSearch.class);
            startActivity(goBack);
        });

        ListView listOfFavourites = findViewById(R.id.listOfFavourites);

        SongsterDatabaseOpenHelper dbOpener = new SongsterDatabaseOpenHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        String[] columns = {SongsterDatabaseOpenHelper.COL_ID,
                SongsterDatabaseOpenHelper.COL_SongID, SongsterDatabaseOpenHelper.COL_SongName,
                SongsterDatabaseOpenHelper.COL_ArtistID, SongsterDatabaseOpenHelper.COL_ArtistName
        };
        Cursor results = db.query(false, SongsterDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(SongsterDatabaseOpenHelper.COL_ID);
        int songIDColIndex = results.getColumnIndex(SongsterDatabaseOpenHelper.COL_SongID);
        int songNameColIndex = results.getColumnIndex(SongsterDatabaseOpenHelper.COL_SongName);
        int artistIDColIndex = results.getColumnIndex(SongsterDatabaseOpenHelper.COL_ArtistID);
        int artistNameColIndex = results.getColumnIndex(SongsterDatabaseOpenHelper.COL_ArtistName);
//        int chordsPresentColIndex = results.getColumnIndex(SongsterDatabaseOpenHelper.COL_ChordsPresent);

        while (results.moveToNext()) {
            long id = results.getLong(idColIndex);
            long songID = results.getLong(songIDColIndex);
            String songName = results.getString(songNameColIndex);
            long artistID = results.getLong(artistIDColIndex);
            String artistName = results.getString(artistNameColIndex);
            //long chordsPresent = results.getLong(chordsPresentColIndex);

            favSongster.add(new SongsterObject(id, songID, songName, artistID, artistName));
            adapter = new SongsterAdapter(getApplicationContext(), favSongster);
            listOfFavourites.setAdapter(adapter);
        }

        listOfFavourites.setOnItemClickListener((parent, view, position, id) -> {
            positionClicked = position;
            SongsterObject songsterToDelete = favSongster.get(positionClicked);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle("Alert!")
                    .setMessage("Do you want to delete?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            favSongster.remove(positionClicked);
                            adapter.notifyDataSetChanged();
                            int numDeleted = db.delete(SongsterDatabaseOpenHelper.TABLE_NAME,
                                    SongsterDatabaseOpenHelper.COL_ID + "=?", new String[]{Long.toString(songsterToDelete.getId())});
                            //Log.i("StationView", "Deleted " + numDeleted + " rows");
                            Snackbar.make(findViewById(R.id.savedFragment), "Station was successfully deleted", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", (d, w) -> {  /* nothing */})
                    .create();
            dialog.show();

            Toast.makeText(getApplicationContext(), "You picked a station to delete", Toast.LENGTH_SHORT).show();
        });

    }
}
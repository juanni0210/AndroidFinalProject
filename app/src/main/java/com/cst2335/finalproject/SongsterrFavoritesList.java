package com.cst2335.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Handles displaying and removing items from favorites database
 * can start navigating to favorites items
 */
public class SongsterrFavoritesList extends AppCompatActivity {

    /**
     * ArrayList containing all favorite Song name
     */
    private ArrayList<SongsterrObject> favorites = new ArrayList<>();;
    /**
     * db helper
     */
    private SQLiteDatabase db;
    /**
     * Adapter to help display list view
     */
    private BaseAdapter myAdapter;

    /**
     * Populates and display favorites in list view
     * Handles selection of a favorite from listview or back button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.songsterr_favorites);

        //Set up
        TextView songTitle =  findViewById(R.id.detailSongName);
        TextView songID = findViewById(R.id.detailSongID);
        TextView artistName =  findViewById(R.id.detailArtistName);
        TextView artistID = findViewById(R.id.detailArtistID);
        ImageButton deleteBtn = findViewById(R.id.songsterrDetailFavoriteButton);

        ListView theFavList = findViewById(R.id.songsterrFavList);
        theFavList.setAdapter( myAdapter = new SongsterrFavListAdapter() );

        theFavList.setOnItemClickListener( (list, item, position, id) -> {
            SongsterrObject selectedItem = favorites.get(position);

            String getSongTitle = selectedItem.getSongName();
            String getSongID = selectedItem.getSongID();
            String getArtistName = selectedItem.getArtistName();
            String getArtistID = selectedItem.getArtistID();

            songTitle.setText(getSongTitle);
            songID.setText(getSongID);
            artistName.setText(getArtistName);
            artistID.setText(getArtistID);

            deleteBtn.setOnClickListener(clk->{
                AlertDialog.Builder builder = new AlertDialog.Builder(SongsterrFavoritesList.this);
                View view = LayoutInflater.from(SongsterrFavoritesList.this).inflate(R.layout.songsterr_alert, null);

                TextView alertTitle = (TextView) view.findViewById(R.id.alertTitle);
                ImageButton alertImg = view.findViewById(R.id.alertImg);

                alertTitle.setText(R.string.alertDeleteTitle);
                alertImg.setImageResource(R.drawable.exclamation_mark);

                builder.setPositiveButton(R.string.yes, (e, arg) -> {
                    deleteItem(selectedItem);
                    favorites.remove(position);
                    myAdapter.notifyDataSetChanged();
                    songTitle.setText("");
                    songID.setText("");
                    artistName.setText("");
                    artistID.setText("");

                    Snackbar.make(findViewById(R.id.favLayout), R.string.deleteSnackbar, Snackbar.LENGTH_SHORT).show();
                });

                builder.setNegativeButton(R.string.no, (e, arg) -> { });
                builder.create();
                builder.show();
            });

        });

        songID.setOnClickListener(v -> {
            String url = "http://www.songsterr.com/a/wa/song?id="+songID ;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(url) );
            startActivity(i);
        });

        artistID.setOnClickListener(v -> {
            String url = "http://www.songsterr.com/a/wa/artist?id="+artistID ;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(url) );
            startActivity(i);
        });

        loadDataFromDatabase();

    }

    protected void deleteItem(SongsterrObject object)
    {
        db.delete(SongsterrDatabaseHelper.TABLE_NAME, SongsterrDatabaseHelper.COL_ID + "= ?", new String[] {Long.toString(object.getId())});
    }


    private void loadDataFromDatabase(){
        favorites.clear();
        SongsterrDatabaseHelper dbOpener = new SongsterrDatabaseHelper(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {SongsterrDatabaseHelper.COL_ID, SongsterrDatabaseHelper.COL_SONGNAME, SongsterrDatabaseHelper.COL_SONGID, SongsterrDatabaseHelper.COL_ARTISTNAME, SongsterrDatabaseHelper.COL_ARTISTID};
        Cursor results = db.query(false, SongsterrDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //col index
        int songNameColumnIndex = results.getColumnIndex(SongsterrDatabaseHelper.COL_SONGNAME);
        int songIDColIndex = results.getColumnIndex(SongsterrDatabaseHelper.COL_SONGID);
        int artistNameColumnIndex = results.getColumnIndex(SongsterrDatabaseHelper.COL_ARTISTNAME);
        int artistIDColIndex = results.getColumnIndex(SongsterrDatabaseHelper.COL_ARTISTID);
        int idColIndex = results.getColumnIndex(SongsterrDatabaseHelper.COL_ID);

        while (results.moveToNext()) {

            String songName = results.getString(songNameColumnIndex);
            String songID = results.getString(songIDColIndex);
            String artistName = results.getString(artistNameColumnIndex);
            String artistID = results.getString(artistIDColIndex);
            long id = results.getLong(idColIndex);

            favorites.add(new SongsterrObject(songName, songID, artistName, artistID, id));
            myAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Adapter to help display listview
     */
    private class SongsterrFavListAdapter extends BaseAdapter {

        /**
         *size getter
         * @return size of listview
         */
        public int getCount() {
            return favorites.size();  }

        /**
         *
         * @param position position of the requested object
         * @return object at position
         */
        public SongsterrObject getItem(int position) {
            return favorites.get(position);  }

        /**
         *
         * @param p position of object
         * @return id of the requested object
         */
        public long getItemId(int p) {
            return p; }

        /**
         * inflates and returns view for display in listview
         *
         * @param p position of object
         * @param recycled old view to be recycled
         * @param parent
         * @return inflated vied for listview
         */
        public View getView(int p, View recycled, ViewGroup parent)
        {

            View thisRow = getLayoutInflater().inflate(R.layout.songsterr_listview_layout, null);

            TextView latText = thisRow.findViewById(R.id.songNameList );
            latText.setText(getItem(p).getSongName() );

            return thisRow;
        }
    }
}
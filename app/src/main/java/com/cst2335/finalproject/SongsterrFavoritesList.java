package com.cst2335.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songsterr_favorites);

        /**
         * Set up element
         */
        TextView songTitle =  findViewById(R.id.detailSongName);
        TextView songID = findViewById(R.id.detailSongID);
        TextView artistName =  findViewById(R.id.detailArtistName);
        TextView artistID = findViewById(R.id.detailArtistID);
        ImageButton deleteBtn = findViewById(R.id.songsterrDetailFavoriteButton);
        Toolbar toolbar = findViewById(R.id.songsterrToolbar);

        /**
         * Set toolbar
         */
        setSupportActionBar(toolbar);

        /**
         * set listview and adapter
         */
        ListView theFavList = findViewById(R.id.songsterrFavList);
        theFavList.setAdapter( myAdapter = new SongsterrFavListAdapter() );

        /**
         * click listview show songster
         */

        theFavList.setOnItemClickListener( (list, item, position, id) -> {
            SongsterrObject selectedItem = favorites.get(position);

            String getSongTitle = selectedItem.getSongName();
            String getSongID = selectedItem.getSongID();
            String getArtistName = selectedItem.getArtistName();
            String getArtistID = selectedItem.getArtistID();

            songTitle.setText(getString(R.string.songTitle)+getSongTitle);
            songID.setText(getString(R.string.songID)+getSongID);
            artistName.setText(getString(R.string.artistName)+getArtistName);
            artistID.setText(getString(R.string.artistID)+getArtistID);

            deleteBtn.setOnClickListener(clk->{
                AlertDialog.Builder builder = new AlertDialog.Builder(SongsterrFavoritesList.this);
                builder.setTitle(R.string.alertDeleteTitle);
                builder.setIcon(R.drawable.exclamation_mark);
                builder.setPositiveButton(R.string.yes, (e, arg) -> {
                    deleteItem(selectedItem);
                    favorites.remove(position);
                    myAdapter.notifyDataSetChanged();

                    /**
                     * clean the result
                     */
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

            /**
             * click on songID to launch a browser to the song’s guitar music page
             */
            songID.setOnClickListener(v -> {
                String url = "http://www.songsterr.com/a/wa/song?id="+getSongID ;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse(url) );
                startActivity(i);
            });

            /**
             * click on artistID to launch a web browser to the artist’s list of songs
             */
            artistID.setOnClickListener(v -> {
                String url = "http://www.songsterr.com/a/wa/artist?id="+getArtistID ;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse(url) );
                startActivity(i);
            });

        });

        loadDataFromDatabase();
    }

    /**
     * Function to del
     * ete item from databse
     * @param object
     */
    protected void deleteItem(SongsterrObject object) {
        db.delete(SongsterrDatabaseHelper.TABLE_NAME, SongsterrDatabaseHelper.COL_ID + "= ?", new String[] {Long.toString(object.getId())});
    }

    /**
     * function to help load data from database
     */
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

    /**
     * Inflates menu bar
     * @param menu menu to be inflated
     * @return if menu has been inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Handles actions of selected menu items
     * @param item which item has been selected
     * @return if actions have been successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.backHomeItem:
                startActivity(new Intent(SongsterrFavoritesList.this, MainActivity.class));
                Toast.makeText(this, R.string.goToMain, Toast.LENGTH_LONG).show();
                break;
            case R.id.triviaItem:
//                startActivity(new Intent(SongsterrFavoritesList.this, TriviaGameLaunchActivity.class));
                Toast.makeText(this, R.string.goToTrivia, Toast.LENGTH_LONG).show();
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SongsterrFavoritesList.this, SongsterSearch.class));
                Toast.makeText(this, R.string.goToSongster, Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
//                startActivity(new Intent(SongsterrFavoritesList.this, CarDatabase.class));
                Toast.makeText(this, R.string.goToCarDatabase, Toast.LENGTH_LONG).show();
                break;
            case R.id.soccerItem:
//                startActivity(new Intent(SongsterrFavoritesList.this, SoccerGames.class));
                Toast.makeText(this, R.string.goToSoccerGame, Toast.LENGTH_LONG).show();
                break;
            case R.id.helpItem:
                AlertDialog.Builder songsterHelpDialog = new AlertDialog.Builder(this);
                songsterHelpDialog.setTitle(getResources().getString(R.string.songsterHelpTile))
                        //What is the message:
                        .setMessage(getResources().getString(R.string.songsterInstructions1) + "\n"
                                + getResources().getString(R.string.songsterInstructions2) + "\n"
                                + getResources().getString(R.string.songsterInstructions3) + "\n"
                                + getResources().getString(R.string.songsterInstructions4) + "\n"
                                + getResources().getString(R.string.songsterInstructions5))
                        //What the No button does:
                        .setNegativeButton(getResources().getString(R.string.closeHelpDialog), (click, arg) -> {
                        })
                        //Show the dialog
                        .create().show();
                break;
        }
        return true;
    }
}
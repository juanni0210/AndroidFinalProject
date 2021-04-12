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

        //Set up
        TextView songTitle =  findViewById(R.id.detailSongName);
        TextView songID = findViewById(R.id.detailSongID);
        TextView artistName =  findViewById(R.id.detailArtistName);
        TextView artistID = findViewById(R.id.detailArtistID);
        ImageButton deleteBtn = findViewById(R.id.songsterrDetailFavoriteButton);
        Toolbar toolbar = findViewById(R.id.songsterrToolbar);
        setSupportActionBar(toolbar);

        ListView theFavList = findViewById(R.id.songsterrFavList);

        theFavList.setAdapter( myAdapter = new SongsterrFavListAdapter() );
        theFavList.setOnItemClickListener( (list, item, position, id) -> {
            SongsterrObject selectedItem = favorites.get(position);

            String getSongTitle = selectedItem.getSongName();
            String getSongID = selectedItem.getSongID();
            String getArtistName = selectedItem.getArtistName();
            String getArtistID = selectedItem.getArtistID();

            songTitle.setText(R.string.songTitle + getSongTitle);
            songID.setText(R.string.songID + getSongID);
            artistName.setText(R.string.artistName + getArtistName);
            artistID.setText(R.string.artistID + getArtistID);

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
                break;
            case R.id.triviaItem:
                //startActivity(new Intent(TriviaGameLaunchActivity.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SongsterrFavoritesList.this, SongsterSearch.class));
                Toast.makeText(this, "Go to songster page", Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
                Toast.makeText(this, "Go to car database page", Toast.LENGTH_LONG).show();
                break;
            case R.id.soccerItem:
                Toast.makeText(this, "Go to soccer game page", Toast.LENGTH_LONG).show();
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
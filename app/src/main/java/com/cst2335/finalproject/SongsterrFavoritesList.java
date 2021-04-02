package com.cst2335.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Handles displaying and removing items from favorites database
 * can start navigating to favorites items
 */
public class SongsterrFavoritesList extends AppCompatActivity {

    /**
     * ArrayList containing all favorite Song name
     */
    private ArrayList<SongsterrObject> favorites;
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

        favorites = new ArrayList<>();

        SongsterrDatabaseHelper dbOpener = new SongsterrDatabaseHelper(this);
        db = dbOpener.getWritableDatabase();

        ListView theList = findViewById(R.id.songsterrFavList);
        theList.setAdapter( myAdapter = new SongsterrFavListAdapter() );

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
        }
        myAdapter.notifyDataSetChanged();

//        ImageButton backButton = findViewById(R.id.carFavBackButton);
//        backButton.setOnClickListener( clk ->
//        {
//            Intent backPage = new Intent(CarFavoritesList.this, ElectricCarFinder.class);
//            startActivity(backPage);
//
//        });

        theList.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(SongsterrDatabaseHelper.COL_SONGNAME, favorites.get(position).getSongName());
            dataToPass.putString(SongsterrDatabaseHelper.COL_SONGID, favorites.get(position).getSongID());
            dataToPass.putString(SongsterrDatabaseHelper.COL_ARTISTNAME, favorites.get(position).getArtistName());
            dataToPass.putString(SongsterrDatabaseHelper.COL_ARTISTID, favorites.get(position).getArtistID());
            dataToPass.putInt(SongsterMain.ITEM_POSITION, position);
            dataToPass.putLong(SongsterrDatabaseHelper.COL_ID, id);

            boolean isTablet = findViewById(R.id.songsterrFavFragmentLocation) != null;

            if(isTablet)
            {
                SongsterrFavDetailFragment dFragment = new SongsterrFavDetailFragment();
                dFragment.setArguments( dataToPass );
                dFragment.setTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.songsterrFavFragmentLocation, dFragment)
                        .addToBackStack("AnyName")
                        .commit();
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SongsterrFavoritesList.this, SongsterrFavEmpty.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, SongsterMain.EMPTY_ACTIVITY); //make the transition

            }
        });
    }

    /**
     * Method to check if the results were successful from the empty activity.
     * If proper codes are given take the id from the data and remove from favorites
     *
     * @param requestCode int to check if the result came from the empty activity
     * @param resultCode int to check if the activity finished successfully
     * @param data data from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SongsterMain.EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getIntExtra(SongsterrDatabaseHelper.COL_ID, 0);
                int pos = data.getIntExtra(SongsterMain.ITEM_POSITION, 0);
                removeFromFavorite((int)id, pos);
            }
        }
    }

    /**
     * Method that take an id of the item and then removes that item from the favorites database
     * @param id id of the item being removed from favorites
     */
    public void removeFromFavorite(int id, int pos) {
        db.delete(SongsterrDatabaseHelper.TABLE_NAME, SongsterrDatabaseHelper.COL_ID + "=" + id, null);
        favorites.remove(pos);
        Toast.makeText(this, "Removed From Favorite", Toast.LENGTH_LONG).show();
        myAdapter.notifyDataSetChanged();

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
            latText.setText("Song Name: " + getItem(p).getSongName() );

            TextView longText = thisRow.findViewById(R.id.artistNameList );
            longText.setText("Artist Name: " + getItem(p).getArtistName() );


            return thisRow;
        }
    }
}

package com.cst2335.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongsterAdapter extends BaseAdapter {
    /**
     * list to hold car charging stations objects
     */
    private ArrayList<SongsterObject> songsterList;
    /**
     * context of the current state of an application
     */
    private Context mContext;
    /**
     * marker of a full or partial information of a car charging station displayed in an item view (needed for getView() method)
     */
    private Boolean fullInfo;

    /**
     * Constructor for an adapter
     * @param context current state of an application
     * @param songsterList list of songster objects
     * @param fullInfo marker of a full details for an item view displayed on screen
     */
    public SongsterAdapter(Context context, ArrayList<SongsterObject> songsterList, Boolean fullInfo) {
        super();
        this.mContext = context;
        this.songsterList = songsterList;
        this.fullInfo = fullInfo;
    }

    /**
     * Methods counts how many car charging stations are in a list
     * @return number of stations in a list
     */
    @Override
    public int getCount() {
        return songsterList.size();
    }

    /**
     * Methods gets a car charging station from a list
     * @param i position of a station in a list
     * @return ChargingStationObject car charging station
     */
    @Override
    public SongsterObject getItem(int i) {
        return songsterList.get(i);
    }

    /**
     * Methods gets a station's id from a database
     * @param i position of a station in a list
     * @return id of a station
     */
    @Override
    public long getItemId(int i) {
        return (getItem(i)).getId();
    }

    /**
     * Method returns a view for a car charging station
     * @param i position of a station in a list
     * @param view recycled view
     * @param viewGroup view that can contain other views
     * @return view of a car charging station (row to the ListView)
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View newView = inflater.inflate(R.layout.activity_songster_search_content, viewGroup, false);

        SongsterObject row = getItem(i);
        TextView searchResultSong = newView.findViewById(R.id.searchResultSong);
        TextView searchResultArtist = newView.findViewById(R.id.searchResultArtist);


        if(fullInfo) {
            searchResultSong.setText(row.getSongName());
            searchResultArtist.setText(row.getArtistName());

        }
        else{
            searchResultArtist.setText("no result");
            searchResultSong.setText("no result");
        }
        return newView;
    }
}

package com.cst2335.finalproject;

import java.io.Serializable;

public class SongsterObject implements Serializable {
    private long id;// database id
    private long songID;
    private String songName;
    private long artistID;
    private String  artistName;
    private boolean useThePrefix;
    private boolean chordsPresent;
    //private String tabType;


    public SongsterObject() {
    }

    public SongsterObject(long id, long songID, String songName, long artistID,
                          String artistName, boolean useThePrefix, boolean chordsPresent) {
        this.id = id;
        this.songID = songID;
        this.songName = songName;
        this.artistID = artistID;
        this.artistName = artistName;
        this.useThePrefix = useThePrefix;
        this.chordsPresent = chordsPresent;
        //this.tabType = tabType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSongID() {
        return songID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public long getArtistID() {
        return artistID;
    }

    public void setArtistID(long artistID) {
        this.artistID = artistID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public boolean isUseThePrefix() {
        return useThePrefix;
    }

    public void setUseThePrefix(boolean useThePrefix) {
        this.useThePrefix = useThePrefix;
    }

    public boolean isChordsPresent() {
        return chordsPresent;
    }

    public void setChordsPresent(boolean chordsPresent) {
        this.chordsPresent = chordsPresent;
    }

//    public String getTabType() {
//        return tabType;
//    }
//
//    public void setTabType(String tabType) {
//        this.tabType = tabType;
//    }
}

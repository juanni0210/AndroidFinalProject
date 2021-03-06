package com.cst2335.finalproject;

/**
 * Class to Set up Songster object
 * @author Xueru Chen
 */
public class SongsterrObject {
    /**
     * Name of song
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
     * id of object
     */
    private long id;

    /**
     * Constructor for songster details
     * @param songName song title
     * @param songID song id
     * @param artistName artist name
     * @param artistID artist id
     */
    public SongsterrObject(String songName, String songID, String artistName, String artistID){
        this.songName = songName;
        this.songID = songID;
        this.artistName = artistName;
        this.artistID = artistID;
    }

    /**
     * Constructor for songster details and id
     * @param songName song title
     * @param songID song id
     * @param artistName artist name
     * @param artistID artist id
     * @param id songster id
     */
    public SongsterrObject(String songName, String songID, String artistName, String artistID, long id){
        this.songName = songName;
        this.songID = songID;
        this.artistName = artistName;
        this.artistID = artistID;
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    /**
     * id getter id
     * @return
     */
    public long getId(){ return id; }
}

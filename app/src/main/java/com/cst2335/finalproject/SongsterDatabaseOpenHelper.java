package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongsterDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int version = 1;
    public static final String TABLE_NAME = "SongsterFavourite";
    public static final String COL_ID = "_id";
    public static final String COL_SongName = "SongName";
    public static final String COL_SongID= "SongID";
    public static final String COL_ArtistName = "ArtistName";
    public static final String COL_ArtistID = "ArtistID";
    public static final String COL_UseThePrefix = "UseThePrefix";
    public static final String COL_ChordsPresent = "ChordsPresent";
    public static final String COL_TabType = "TabType";

    private static final String DATABASE_NAME = "SongsterDatabase.db";

    public SongsterDatabaseOpenHelper(Context context) {
        super(context,TABLE_NAME,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_SongID + ", " + COL_SongName + ", "
                + COL_ArtistID + ", " + COL_ArtistName + ", "
                + COL_UseThePrefix + ", " + COL_ChordsPresent + ", " + COL_TabType + ") ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}


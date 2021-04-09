package com.cst2335.finalproject;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class to handle tables in database
 * Contains names of columns, table, databse name
 * Contains database version number
 */
public class SongsterrDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Database name
     */
    public static final String DATABASE_NAME = "SongsterrFavDatabase";
    /**
     * Database version
     */
    public static final int VERSION_NUM = 1;
    /**
     * Table name
     */
    public static final String TABLE_NAME = "Favorites";
    /**
     * Column name
     */
    public static final String COL_ID = "_id";
    /**
     * Column name
     */
    public static final String COL_SONGNAME = "SongName";
    /**
     * Column name
     */
    public static final String COL_SONGID = "songID";
    /**
     * Column name
     */
    public static final String COL_ARTISTNAME = "artistName";
    /**
     * Column name
     */
    public static final String COL_ARTISTID = "artistID";

    /**
     * Constructor for database helper
     * @param ctx
     */
    public SongsterrDatabaseHelper(Activity ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    /**
     * Creates table in db
     * @param db db helper
     */
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_SONGNAME + " TEXT, "
                + COL_SONGID + " TEXT, "
                + COL_ARTISTNAME + " TEXT, "
                + COL_ARTISTID + " TEXT)");
    }

    /**
     * Drop old table and create new table on version upgrade
     * @param db db helper
     * @param oldVersion old version number
     * @param newVersion new version number
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    /**
     * Drop old table and create new table on version downgrade
     * @param db db helper
     * @param oldVersion old version number
     * @param newVersion new version number
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }
}
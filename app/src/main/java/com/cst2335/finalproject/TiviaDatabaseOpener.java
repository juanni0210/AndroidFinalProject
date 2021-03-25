package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class extends from SQLiteOpenHelper class to manage trivia game database creation and version management.
 * @author Juan Ni
 */
public class TiviaDatabaseOpener extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "LeaderBoard";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "RecordTable";
    public final static String COL_NAME = "NAME";
    public final static String COL_SCORE = "SCORE";
    public final static String COL_AMOUNT= "QUESTION_AMOUNT";
    public final static String COL_DIFFICULTY = "DIFFICULTY";
    public final static String COL_TYPE = "QUESTION_TYPE";
    public final static String COL_ID = "ID";

    //constructor
    //Context ctx – the Activity where the database is being opened.
    public TiviaDatabaseOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT,"
                + COL_SCORE + " INTEGER,"
                + COL_AMOUNT + " INTEGER,"
                + COL_DIFFICULTY + " TEXT,"
                + COL_TYPE  + " TEXT);");  // add or remove columns
    }


    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }


}

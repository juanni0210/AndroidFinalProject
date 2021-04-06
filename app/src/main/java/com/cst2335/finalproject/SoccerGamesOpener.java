package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoccerGamesOpener extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "SoccerGamesDB";
    protected final static int VERSION_NUM = 3;
    public final static String TABLE_NAME = "ITEM";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_DATE = "DATE";
    public final static String COL_IMAGE = "IMAGE";
    public final static String COL_LINK = "LINK";
    public final static String COL_DESCRIPTION = "DESCRIPTION";
    public final static String COL_ID = "_id";

    public SoccerGamesOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_DATE + " text,"
                + COL_IMAGE  + " text,"
                + COL_LINK + " text,"
                + COL_DESCRIPTION + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

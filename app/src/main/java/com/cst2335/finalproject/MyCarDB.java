/**
 * This is the database page of the CarDatabase in this app
 * @author Sophie Sun
 * @since 1.0
 */

package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyCarDB extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "MyCarDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "Car";
    public final static String COL_MAKE = "MAKE";
    public final static String COL_MODEL = "MODEL";
    public final static String COL_MAKEID = "MAKEID";
    public final static String COL_MODELID = "MODELID";
    public final static String COL_ID = "_id";

    public MyCarDB(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MAKE + " text,"
                + COL_MODEL + " text,"
                + COL_MAKEID + " text,"
                + COL_MODELID + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

}

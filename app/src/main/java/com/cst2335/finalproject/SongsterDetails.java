package com.cst2335.finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SongsterDetails extends AppCompatActivity {

    private SQLiteDatabase db;
    private long dbId;
    private SongsterDatabaseOpenHelper dbOpener;
    private Toolbar detailToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_detail);

        detailToolbar = findViewById(R.id.detail_toolbar_songster);
        setSupportActionBar(detailToolbar);


        //get a database
        dbOpener = new SongsterDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();


    }

}

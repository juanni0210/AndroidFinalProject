package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Favorites extends AppCompatActivity {
    MyListAdapter myAdapter= new MyListAdapter();

    SQLiteDatabase db;
    ArrayList<Model> elements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        loadDataFromDatabase();

        Button previousButton = findViewById(R.id.previousPageButton);
        Button deleteBtn = findViewById(R.id.deleteButton);
        TextView makeName = findViewById(R.id.makeName);
        TextView modelName = findViewById(R.id.modelName);

        previousButton.setOnClickListener(click ->
                finish()
        );

        deleteBtn.setOnClickListener(v -> {
            ContentValues cValues = new ContentValues();
            cValues.put(MyCarDB.COL_MAKE, makeName.getText().toString());
            cValues.put(MyCarDB.COL_MODEL, modelName.getText().toString());
            long id = db.insert(MyCarDB.TABLE_NAME, null, cValues);
            Model model = new Model(makeName.getText().toString(), modelName.getText().toString(), id);
            elements.add(model);
            myAdapter.notifyDataSetChanged();
        });
    }
    private void loadDataFromDatabase()
    {
        //get a database connection:
        MyCarDB carDB = new MyCarDB(this);
        db = carDB.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {MyCarDB.COL_ID, MyCarDB.COL_MAKE, MyCarDB.COL_MODEL};
        //query all the results from the database:
        Cursor results = db.query(false, MyCarDB.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int makeColumnIndex = results.getColumnIndex(MyCarDB.COL_MAKE);
        int modelColumnIndex = results.getColumnIndex(MyCarDB.COL_MODEL);
        int idColIndex = results.getColumnIndex(MyCarDB.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String make  = results.getString(makeColumnIndex);
            String model = results.getString(modelColumnIndex);
            long id = results.getLong(idColIndex);
            //elements.add(new Model(makeName.getText().toString(), modelName.getText().toString(), id));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
    }

    public class MyListAdapter extends BaseAdapter {
        public int getCount() {
            return elements.size();
        }

        public Model getItem(int position) {
            return elements.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if (newView == null) {
                newView = inflater.inflate(R.layout.row_layout, parent, false);

            }
            //set what the text should be for this row:
            TextView makeName = newView.findViewById(R.id.makeName);
            TextView modelName = newView.findViewById(R.id.modelName);
            makeName.setText(getItem(position).toString());
            modelName.setText(getItem(position).toString());

            //return it to be put in the table
            return newView;
        }
    }
}

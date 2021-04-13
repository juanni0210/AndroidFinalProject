/**
 * This is the favorite page which loads and displays user's favorites
 * @author Sophie Sun
 * @since 1.0
 */

package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
        ListView favoriteListView = findViewById(R.id.saved_model_list);
        favoriteListView.setAdapter(myAdapter);

        loadDataFromDatabase();
        //myAdapter.notifyDataSetChanged();

        Button previousButton = findViewById(R.id.previousPageButton);
        //Button deleteBtn = findViewById(R.id.deleteButton);
        TextView makeName = findViewById(R.id.makeName);
        TextView modelName = findViewById(R.id.modelName);


        previousButton.setOnClickListener(click ->
                finish()
        );

        favoriteListView.setOnItemClickListener((p, b, pos, id) ->{

            Model currentModel = elements.get(pos);
            Bundle dataToPass = new Bundle();
            dataToPass.putString(CarDatabase.MAKE_SELECTED, currentModel.getMake() );
            dataToPass.putString(CarDatabase.MODEL_SELECTED, currentModel.getModel());
            boolean isTablet = findViewById(R.id.fragmentLocation) != null;

            if(isTablet)
            {
                CarDetailsFragment dFragment = new CarDetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment, Long.toString(id)) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(Favorites.this, EmptyCarActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }

            Button goToShop = findViewById(R.id.shop);
            goToShop.setOnClickListener(v -> {
                String urlShop = "https://www.autotrader.ca/cars/?mdl=" + currentModel.getModel() + "&make=" + currentModel.getMake() + "&loc=K2G1V8" ;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse(urlShop) );
                startActivity(i);
            });
        });

    }


    /**
     * @param
     */
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
            elements.add(new Model(make, model, id));
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
            makeName.setText(getItem(position).getMake());
            modelName.setText(getItem(position).getModel());

            //return it to be put in the table
            return newView;
        }
    }
}

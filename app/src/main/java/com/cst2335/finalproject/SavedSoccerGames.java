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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.cst2335.finalproject.SoccerGames.ITEM_DATE;
import static com.cst2335.finalproject.SoccerGames.ITEM_DESCRIPTION;
import static com.cst2335.finalproject.SoccerGames.ITEM_IMAGE;
import static com.cst2335.finalproject.SoccerGames.ITEM_TITLE;
import static com.cst2335.finalproject.SoccerGames.ITEM_URL;

public class SavedSoccerGames extends AppCompatActivity {

    public static ArrayList<Item> savedItems = new ArrayList<>();
    public static MyOwnAdapter mySavedAdapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_soccer_games);

//        loadDataFromDatabase(); //get any previously saved Contact objects

        //Get the fields from the screen:
        TextView saveDate = (TextView) findViewById(R.id.savedDate);
        TextView saveDescription = (TextView) findViewById(R.id.savedDescription);
        TextView saveLink = (TextView) findViewById(R.id.savedLink);
        Button readSavedBtn = (Button)findViewById(R.id.readSavedBtn);
        Button removeBtn = (Button)findViewById(R.id.removeBtn);
        ListView theSavedList = (ListView)findViewById(R.id.theSavedListView);

        mySavedAdapter = new MyOwnAdapter();
        theSavedList.setAdapter(mySavedAdapter);

        theSavedList.setOnItemClickListener((p, b, pos, id) -> {
            Item selectedItem = savedItems.get(pos);
            String getDate = selectedItem.getDate();
            String getImage = selectedItem.getImage();
            String getDescription = selectedItem.getDescription();
            String getLink = selectedItem.getUrl();

            saveDate.setText(getDate);
            saveDescription.setText(getDescription);
            saveLink.setText(getLink);
            // intend to load image
//            System.out.println(selectedItem.getImage());
//            loadImage(selectedItem.getImage());
//
//            Bundle linkToPass = new Bundle();
//            linkToPass.putString("news", getLink);
//            goToNews.putExtras(linkToPass);

//            readBtn.setOnClickListener(click -> {
//                startActivity(goToNews);
//            });

//            imgView.setImageBitmap(selectedItem.getItemImage());

        });
    }
//
    private void loadDataFromDatabase()
    {
        SoccerGamesOpener savedbOpener = new SoccerGamesOpener(SavedSoccerGames.this);
        db = savedbOpener.getWritableDatabase();

        String [] columns = {SoccerGamesOpener.COL_ID, SoccerGamesOpener.COL_TITLE, SoccerGamesOpener.COL_DATE, SoccerGamesOpener.COL_IMAGE, SoccerGamesOpener.COL_LINK, SoccerGamesOpener.COL_DESCRIPTION};
        Cursor results = db.query(false, SoccerGamesOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int titleColumnIndex = results.getColumnIndex(SoccerGamesOpener.COL_TITLE);
        int dateColumnIndex = results.getColumnIndex(SoccerGamesOpener.COL_DATE);
        int imageColIndex = results.getColumnIndex(SoccerGamesOpener.COL_IMAGE);
        int linkColIndex = results.getColumnIndex(SoccerGamesOpener.COL_LINK);
        int desColIndex = results.getColumnIndex(SoccerGamesOpener.COL_DESCRIPTION);
        int idColIndex = results.getColumnIndex(SoccerGamesOpener.COL_ID);

        while(results.moveToNext())
        {
            String title = results.getString(titleColumnIndex);
            String date = results.getString(dateColumnIndex);
            String image = results.getString(imageColIndex);
            String link = results.getString(linkColIndex);
            String description = results.getString(desColIndex);
            long id = results.getLong(idColIndex);

            savedItems.add(new Item(title, date, image, link, description,id));
//            mySavedAdapter.notifyDataSetChanged();
        }
    }

    public class MyOwnAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return savedItems.size();
        }

        public Item getItem(int position){
            return savedItems.get(position);
        }

        public View getView(int position, View old, ViewGroup parent)
        {
             LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View  newView = inflater.inflate(R.layout.row_item_layout, parent, false);
            Item item = getItem(position);
            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.itemGoesHere);
            tView.setText(item.getTitle());

//            tView.setText(getItem(position).toString());
            //return it to be put in the table
            return newView;

        }

        //last week we returned (long) position. Now we return the object's database id that we get from line 71
        public long getItemId(int position)
        {
            return getItem(position).getId();
        }
    }
}
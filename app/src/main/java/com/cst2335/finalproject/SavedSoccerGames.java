package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This is the SavedSoccerGames class to show the saved items from the database.
 * @author Feiqiong Deng
 * @version version 2
 */
public class SavedSoccerGames extends AppCompatActivity {

    public static ArrayList<Item> savedItems = new ArrayList<>();
    public static MyOwnAdapter mySavedAdapter;
    SQLiteDatabase db;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_soccer_games);

        //Get the fields from the screen:
        TextView saveTitle = (TextView) findViewById(R.id.savedTitle);
        TextView saveDate = (TextView) findViewById(R.id.savedDate);
        TextView saveDescription = (TextView) findViewById(R.id.savedDescription);
        TextView saveLink = (TextView) findViewById(R.id.savedLink);
        ImageButton readSavedBtn = findViewById(R.id.readSavedBtn);

        ImageButton removeBtn = findViewById(R.id.removeBtn);
        ListView theSavedList = (ListView)findViewById(R.id.theSavedListView);
        imgView = (ImageView)findViewById(R.id.savedImage);
        imgView.setImageResource(R.drawable.soccerdefault);

        mySavedAdapter = new MyOwnAdapter();
        theSavedList.setAdapter(mySavedAdapter);

        // click the selected item to show details of the item.
        theSavedList.setOnItemClickListener((p, b, pos, id) -> {
            Item selectedItem = savedItems.get(pos);
            String getTitle = selectedItem.getTitle();
            String getDate = selectedItem.getDate();
            String getImage = selectedItem.getImage();
            String getDescription = selectedItem.getDescription();
            String getLink = selectedItem.getUrl();

            ImageRequest imageReq=new ImageRequest();
            imageReq.execute(getImage);

            saveTitle.setText(getTitle);
            saveDate.setText(getDate);
            saveDescription.setText(getDescription);
            saveLink.setText(getLink);

            readSavedBtn.setOnClickListener(click -> {
                Intent intent=new Intent();
                intent.setData(Uri.parse(getLink));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            });

            // When clicking the remove button, the item is removed from the database.
            removeBtn.setOnClickListener(click -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(SavedSoccerGames.this);
                View view = LayoutInflater.from(SavedSoccerGames.this).inflate(R.layout.soccer_dialog, null);

                TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
                ImageButton dialogBtn = view.findViewById(R.id.dialogBtn);
                TextView dialogContent = (TextView) view.findViewById(R.id.dialogContent);

                dialogTitle.setText("Delete this news?");
                dialogBtn.setImageResource(R.drawable.remove);
                dialogContent.setText("The selected news is: " + pos + "\n" + "The database id id: " + id);

                builder.setPositiveButton("Yes", (e, arg) -> {
                    deleteItem(selectedItem);
                    savedItems.remove(pos);
                    mySavedAdapter.notifyDataSetChanged();
                    saveTitle.setText("");
                    saveDate.setText("");
                    saveDescription.setText("");
                    saveLink.setText("");
                    imgView.setImageBitmap(null);

                    Snackbar snackbar= Snackbar.make(findViewById(R.id.savedLayout), "The selected news is already deleted.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                });

                builder.setNegativeButton("No", (e, arg) -> { });
                builder.setView(view);
                builder.show();
            });

        });

        loadDataFromDatabase(); //get any previously saved Contact objects
    }

    protected void deleteItem(Item m)
    {
        db.delete(SoccerGamesOpener.TABLE_NAME, SoccerGamesOpener.COL_ID + "= ?", new String[] {Long.toString(m.getId())});
    }

    private class ImageRequest extends AsyncTask<String,Integer,String> {
        Bitmap itemImage;
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url2 = new URL(strings[0]);
                HttpURLConnection imgConnection = (HttpURLConnection) url2.openConnection();
                imgConnection.connect();
                int responseCode = imgConnection.getResponseCode();
                if (responseCode == 200) {
                    itemImage = BitmapFactory.decodeStream(imgConnection.getInputStream());
                }
            } catch (IOException e) {
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imgView.setImageBitmap(itemImage);
        }
    }

    private void loadDataFromDatabase()
    {
        savedItems.clear();
        SoccerGamesOpener savedbOpener = new SoccerGamesOpener(this);
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
            mySavedAdapter.notifyDataSetChanged();
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
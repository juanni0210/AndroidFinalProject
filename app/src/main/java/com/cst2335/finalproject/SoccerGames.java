package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.cst2335.finalproject.SavedSoccerGames.mySavedAdapter;
import static com.cst2335.finalproject.SavedSoccerGames.savedItems;

/**
 * This is the SoccerGames class to show the GUI and control the API behavior.
 * @author Feiqiong Deng
 * @version version 1
 */
public class SoccerGames extends AppCompatActivity {

//    private TextView mTextView;
    private MyListAdapter myAdapter;
    private ArrayList<Item> elements = new ArrayList<>();
    private ArrayList<Bitmap> bit = new ArrayList<>();
    SQLiteDatabase db;
    ImageView imgView;
    URL imageUrl;

    private ProgressBar progressBar;

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_TITLE = "TITLE";
    public static final String ITEM_IMAGE = "IMAGE";
    public static final String ITEM_DATE = "DATE";
    public static final String ITEM_DESCRIPTION = "DESCRIPTION";
    public static final String ITEM_URL = "URL";
    private DetailsFragment detailsFragment;

    /**
     * This is to initialize the activity and setting the http request.
     * @param savedInstanceState Passed to superclass onCreate method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_games);

        ListView myList = (ListView) findViewById(R.id.theListView);
        myList.setAdapter(myAdapter = new MyListAdapter());
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        imgView = findViewById(R.id.image);

        MyHTTPRequest req = new MyHTTPRequest();
        req.execute("https://www.goal.com/en/feeds/news");

        Button favoriteBtn = findViewById(R.id.favoriteBtn);
        Button saveBtn = findViewById(R.id.saveBtn);
        Button readBtn = findViewById(R.id.readBtn);
        RelativeLayout layout = findViewById(R.id.newsLayout);
        Intent goToNews = new Intent(SoccerGames.this, SoccerNewsPage.class);
        Intent goToSaved = new Intent(SoccerGames.this, SavedSoccerGames.class);

        SoccerGamesOpener dbOpener = new SoccerGamesOpener(this);
        db = dbOpener.getWritableDatabase();

        /*
        This is to create a toast when clicking the favorite button.
         */
        favoriteBtn.setOnClickListener( click -> {
//            Toast.makeText(SoccerGames.this, "Favorite news will be coming soon.",Toast.LENGTH_SHORT).show();
                    startActivity(goToSaved);
                }
        );

        /*
        This is to create a snack bar when clicking the save button.
         */
//        saveBtn.setOnClickListener( v -> {
//            Snackbar snackbar= Snackbar.make(layout, "Will be saved in the database.", Snackbar.LENGTH_SHORT);
//            snackbar.show();
//        });

         /*
        This is to create an  alert dialog when opening the API.
         */
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Rating");
        alertDialog.setMessage("Could you rate the API");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        /*
        when user click the item on the list view, details of the item will be sent to the fragment.
         */
        myList.setOnItemClickListener((p, b, pos, id) -> {
            Item selectedItem = elements.get(pos);
            String getTitle = selectedItem.getTitle();
            String getDate = selectedItem.getDate();
            String getImage = selectedItem.getImage();
            String getDescription = selectedItem.getDescription();
            String getLink = selectedItem.getUrl();

            // intend to load image
            System.out.println(selectedItem.getImage());
            loadImage(selectedItem.getImage());

            Bundle linkToPass = new Bundle();
            linkToPass.putString("news", getLink);
            goToNews.putExtras(linkToPass);

            readBtn.setOnClickListener(click -> {
                startActivity(goToNews);
            });

//            imgView.setImageBitmap(selectedItem.getItemImage());
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, getTitle);
            dataToPass.putString(ITEM_DATE, getDate);
            dataToPass.putString(ITEM_URL, getLink);
            dataToPass.putString(ITEM_DESCRIPTION, getDescription);
            dataToPass.putString(ITEM_IMAGE, getImage);

            DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
            dFragment.setArguments(dataToPass); //pass it a bundle for information
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to save this?")

                    //What is the message:
                    .setMessage("The selected row is: " + pos + "The database id id:" + id)

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        Item selectedItem = elements.get(pos);
                        String getTitle = selectedItem.getTitle();
                        String getDate = selectedItem.getDate();
                        String getImage = selectedItem.getImage();
                        String getDescription = selectedItem.getDescription();
                        String getLink = selectedItem.getUrl();

                        ContentValues newRowValues = new ContentValues();
                        newRowValues.put(SoccerGamesOpener.COL_TITLE, getTitle);
                        newRowValues.put(SoccerGamesOpener.COL_DATE, getDate);
                        newRowValues.put(SoccerGamesOpener.COL_IMAGE, getImage);
                        newRowValues.put(SoccerGamesOpener.COL_LINK, getLink);
                        newRowValues.put(SoccerGamesOpener.COL_DESCRIPTION, getDescription);
                        long newId = db.insert(SoccerGamesOpener.TABLE_NAME, null, newRowValues);

                        Item oneItem = new Item(getTitle, getDate, getImage, getLink, getDescription, newId);
                        savedItems.add(oneItem);
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })
                    //Show the dialog
                    .create().show();
            return true;
        });
    }


    private void loadImage(String url){
        Picasso.get().load(url).resize(20, 20)
                .error(R.mipmap.ic_launcher)
                .into(imgView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Successful");
                    }

                    @Override
                    public void onError(Exception e) {
                        System.out.println("Error in loadImage.");
                    }
                });
    }

    /**
     * This is the AsyncTask to connect to the link and get data from the API.
     */
    private class MyHTTPRequest extends AsyncTask< String, Integer, String> {
        private String title;
        private String date;
        private String image;
        private String link;
        private String description;
//        Bitmap itemImage;

        TextView row = findViewById(R.id.textGoesHere);

        public String doInBackground(String ... args)
        {
            try {


                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                boolean insideItem = false;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                // to get the data from the xml and add data to  array list.
                System.out.println("1.========" + xpp.getText() + "===========");
                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    System.out.println("2.========" + xpp.getText() + "===========");
                    if(eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                            Item item = new Item(title, date, image, link, description);
                            elements.add(item);
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                title = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                date = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                link = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                description = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("media:thumbnail")) {
                            if (insideItem) {
                                image =  xpp.getAttributeValue(null, "url");
//                                try {
//                                    URL imageURL = new URL(image);
//                                    HttpURLConnection connection = (HttpURLConnection) imageURL
//                                            .openConnection();
//                                    connection.setDoInput(true);
//                                    connection.connect();
//                                    InputStream input = connection.getInputStream();
//                                    itemImage = BitmapFactory.decodeStream(input);
//                                } catch (IOException e) {
//                                    return null;
//                                }

//                                itemImage = getBitmapFromURL(image);

//                                if(!fileExistance(image)){
//                                    try {
//                                        URL url2 = new URL(image);
//                                        HttpURLConnection imgConnection = (HttpURLConnection) url2.openConnection();
//                                        imgConnection.connect();
//                                        int responseCode = imgConnection.getResponseCode();
//                                        if (responseCode == 200) {
//                                            itemImage = BitmapFactory.decodeStream(imgConnection.getInputStream());
//                                            publishProgress(100);
//                                        }
//                                        FileOutputStream outputStream = openFileOutput(image + ".jpeg", Context.MODE_PRIVATE);
//                                        itemImage.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//                                        outputStream.flush();
//                                        outputStream.close();
//                                    } catch (IOException e) {
//                                        // Log exception
//                                        Log.e("Weather Image", "It is looking for " + image.toString());
//                                    }
//                                } else {
//                                    FileInputStream fis = null;
//                                    try {
//                                        fis = openFileInput(image);
//                                        Log.e("Weather Image", "It is looking for " + image.toString());
//                                        Log.e("Found or Not", "The image is found locally");
//                                    } catch (FileNotFoundException e) {    e.printStackTrace();  }
//                                    itemImage = BitmapFactory.decodeStream(fis);
//                                }
                            }
                        }
                    }
                        else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
//                }
              }
            }
            catch (Exception e)
            {
                System.out.print("My Error: ");
                System.out.println(e.getStackTrace());
            }
            return "Done";
        }

        //Type 2
        public void onProgressUpdate(Integer ... args)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }
        //Type3
        public void onPostExecute(String fromDoInBackground)
        {
            progressBar.setVisibility(View.GONE);
            myAdapter.notifyDataSetChanged();
        }

        public boolean fileExistance (String image) {
            File file = getBaseContext().getFileStreamPath(image);
            return file.exists();
        }

//        public Bitmap getBitmapFromURL(String src) {
//            try {
//                URL url = new URL(src);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//
//                return myBitmap;
//            } catch (IOException e) {
//                // Log exception
//                System.out.println("BITMAP error");
//                return null;
//            }
//        }

    }

    /**
     * This is the adapter.
     */
    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return elements.size();}

        public Item getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
              View  newView = inflater.inflate(R.layout.row_layout, parent, false);
            Item item = getItem(position);
            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.textGoesHere);
            tView.setText(item.getTitle());

//            tView.setText(getItem(position).toString());
            //return it to be put in the table
            return newView;
        }
    }
}
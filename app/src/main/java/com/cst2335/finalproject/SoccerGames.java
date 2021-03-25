package com.cst2335.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoccerGames extends AppCompatActivity {

//    private TextView mTextView;
    private MyListAdapter myAdapter;
    private ArrayList<Item> elements = new ArrayList<>();

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_IMAGE = "IMAGE";
    public static final String ITEM_ID = "ID";
    public static final String ITEM_DATE = "DATE";
    public static final String ITEM_DESCRIPTION = "DESCRIPTION";
    public static final String ITEM_URL = "URL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_games);

        ListView myList = (ListView) findViewById(R.id.theListView);
        myList.setAdapter(myAdapter = new MyListAdapter());

        MyHTTPRequest req = new MyHTTPRequest();
        req.execute("https://www.goal.com/en/feeds/news");
//        req.execute("https://feeds.24.com/articles/fin24/tech/rss");

        myList.setOnItemClickListener((p, b, pos, id) -> {
            Item selectedItem = elements.get(pos);
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_IMAGE, selectedItem.getImage());
            dataToPass.putString(ITEM_DATE, selectedItem.getDate());
            dataToPass.putString(ITEM_URL, selectedItem.getUrl());
            dataToPass.putString(ITEM_DESCRIPTION, selectedItem.getDescription());

            DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
            dFragment.setArguments(dataToPass); //pass it a bundle for information
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
        });
    }

    private class MyHTTPRequest extends AsyncTask< String, Integer, String> {
        private String title;
        private String date;
        private String image;
        private String link;
        private String description;
        private Bitmap itemImage;

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

                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                boolean insideItem = false;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT


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
                                image = xpp.nextText();
//                                if(!fileExistance(image)){
//                                    URL url2 = new URL(image);
//                                    HttpURLConnection imgConnection = (HttpURLConnection) url2.openConnection();
//                                    imgConnection.connect();
//                                    int responseCode = imgConnection.getResponseCode();
//                                    if (responseCode == 200) {
//                                        itemImage = BitmapFactory.decodeStream(imgConnection.getInputStream());
//                                        publishProgress(100);
//                                    }
//                                    FileOutputStream outputStream = openFileOutput( image + ".jpeg", Context.MODE_PRIVATE);
//                                    itemImage.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//                                    outputStream.flush();
//                                    outputStream.close();
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
                System.out.println("error");
            }
            return "Done";
        }

        //Type 2
        public void onProgressUpdate(Integer ... args)
        {
        }
        //Type3
        public void onPostExecute(String fromDoInBackground)
        {
         myAdapter.notifyDataSetChanged();
        }

        public boolean fileExistance (String image) {
            File file = getBaseContext().getFileStreamPath(image);
            return file.exists();
        }
    }

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
package com.cst2335.finalproject;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoccerGames extends AppCompatActivity {

//    private TextView mTextView;
    private MyListAdapter myAdapter;
    private ArrayList<String> elements = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_games);

        ListView myList = (ListView) findViewById(R.id.theListView);
        myList.setAdapter(myAdapter = new MyListAdapter());

        MyHTTPRequest req = new MyHTTPRequest();
        req.execute("https://www.goal.com/en/feeds/news");
//        req.execute("https://feeds.24.com/articles/fin24/tech/rss");
    }

    private class MyHTTPRequest extends AsyncTask< String, Integer, String> {
        private String title;
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
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item")){
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if(insideItem) {
                                elements.add(xpp.nextText());
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
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
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return elements.size();}

        public Object getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
              View  newView = inflater.inflate(R.layout.row_layout, parent, false);

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.textGoesHere);

            tView.setText(getItem(position).toString());
            //return it to be put in the table
            return newView;
        }
    }
}
package com.cst2335.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This is the SoccerGames class to show the GUI and control the API behavior.
 * @author Feiqiong Deng
 * @version version 1.0
 */
public class SoccerGames extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private MyListAdapter myAdapter;
    private ArrayList<Item> elements = new ArrayList<>();
    ImageView imgView;
    private ProgressBar progressBar;
    public static final String ITEM_TITLE = "TITLE";
    public static final String ITEM_IMAGE = "IMAGE";
    public static final String ITEM_DATE = "DATE";
    public static final String ITEM_DESCRIPTION = "DESCRIPTION";
    public static final String ITEM_URL = "URL";

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this
     * Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_games);

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.navigationOpen, R.string.navigationClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ListView myList = (ListView) findViewById(R.id.theListView);
        myList.setAdapter(myAdapter = new MyListAdapter());
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        imgView = findViewById(R.id.image);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        MyHTTPRequest req = new MyHTTPRequest();
        req.execute("https://www.goal.com/en/feeds/news");

        ImageButton favoriteBtn = findViewById(R.id.favoriteBtn);
        Intent goToSaved = new Intent(SoccerGames.this, SavedSoccerGames.class);

        /*
        This is to create a toast when clicking the favorite button.
         */
        favoriteBtn.setOnClickListener( click -> {
                    startActivity(goToSaved);
                }
        );

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

            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, getTitle);
            dataToPass.putString(ITEM_DATE, getDate);
            dataToPass.putString(ITEM_URL, getLink);
            dataToPass.putString(ITEM_DESCRIPTION, getDescription);
            dataToPass.putString(ITEM_IMAGE, getImage);

            if(isTablet) {
                SoccerGamesFragment dFragment = new SoccerGamesFragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else {
                Intent soccerGamesFragment = new Intent(SoccerGames.this, SoccerGamesEmpty.class);
                soccerGamesFragment.putExtras(dataToPass);
                startActivity(soccerGamesFragment);
            }
        });
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * @param menu Menu: The options menu in which you place your items.
     * @return boolean: return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * @param item MenuItem: The menu item that was selected. This value cannot be null.
     * @return boolean: Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.backHomeItem:
                startActivity(new Intent(SoccerGames.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                startActivity(new Intent(SoccerGames.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SoccerGames.this, SongsterSearch.class));
                break;
            case R.id.carDBItem:
                startActivity(new Intent(SoccerGames.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                startActivity(new Intent(SoccerGames.this, RatingSoccerAPI.class));
                break;
            case R.id.helpItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(SoccerGames.this);
                View view = LayoutInflater.from(SoccerGames.this).inflate(R.layout.soccer_dialog, null);

                TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
                ImageButton dialogBtn = view.findViewById(R.id.dialogBtn);
                TextView dialogContent = (TextView) view.findViewById(R.id.dialogContent);

                dialogTitle.setText("App Help Menu");
                dialogBtn.setImageResource(R.drawable.soccerball);
                dialogContent.setText(getResources().getString(R.string.soccerInstructions1) + "\n"
                        + getResources().getString(R.string.soccerInstructions2) + "\n"
                        + getResources().getString(R.string.soccerInstructions3) + "\n"
                        + getResources().getString(R.string.soccerInstructions4) + "\n"
                        + getResources().getString(R.string.soccerInstructions5));

                builder.setNegativeButton(getResources().getString(R.string.closeHelpDialog), (click, arg) -> {
                });
                builder.setView(view);
                builder.show();
                break;
        }
        return true;
    }


    /**
     * Called when an item in the navigation menu is selected.
     * @param item MenuItem: The selected item.
     * @return boolean: Return true to display the item as the selected item.
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.backHomeItem:
                startActivity(new Intent(SoccerGames.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                startActivity(new Intent(SoccerGames.this, TriviaGameLaunchActivity.class));
                break;
            case R.id.songsterItem:
                startActivity(new Intent(SoccerGames.this, SongsterSearch.class));
                break;
            case R.id.carDBItem:
                startActivity(new Intent(SoccerGames.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                startActivity(new Intent(SoccerGames.this, RatingSoccerAPI.class));
                break;
            case R.id.helpItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(SoccerGames.this);
                View view = LayoutInflater.from(SoccerGames.this).inflate(R.layout.soccer_dialog, null);

                TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
                ImageButton dialogBtn = view.findViewById(R.id.dialogBtn);
                TextView dialogContent = (TextView) view.findViewById(R.id.dialogContent);

                dialogTitle.setText("App Help Menu");
                dialogBtn.setImageResource(R.drawable.soccerball);
                dialogContent.setText(getResources().getString(R.string.soccerInstructions1) + "\n"
                        + getResources().getString(R.string.soccerInstructions2) + "\n"
                        + getResources().getString(R.string.soccerInstructions3) + "\n"
                        + getResources().getString(R.string.soccerInstructions4) + "\n"
                        + getResources().getString(R.string.soccerInstructions5));

                builder.setNegativeButton(getResources().getString(R.string.closeHelpDialog), (click, arg) -> {
                });
                builder.setView(view);
                builder.show();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
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
                while(eventType != XmlPullParser.END_DOCUMENT)
                {
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
            View  newView = inflater.inflate(R.layout.soccer_row_layout, parent, false);
            Item item = getItem(position);
            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.textGoesHere);
            tView.setText(item.getTitle());

            return newView;
        }
    }
}
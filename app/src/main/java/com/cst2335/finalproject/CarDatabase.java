/**
 * This is the first page of the CarDatabase in this app
 * @author Sophie Sun
 * @since 1.0
 */

package com.cst2335.finalproject;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.cst2335.finalproject.MyCarDB.COL_MAKE;
import static com.cst2335.finalproject.MyCarDB.COL_MODEL;

public class CarDatabase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    MyListAdapter myAdapter= new MyListAdapter();
    ArrayList<Model> modelList = new ArrayList<>();
    private String userInput, carDBUrl;
    private EditText makeNameInput;
    private ProgressBar carProgressBar;
    SQLiteDatabase db;
    String selectedModelName;
    String selectedMakeName;
    SharedPreferences makeRef;
    public static final String MAKE_SELECTED = "MAKE";
    public static final String MODEL_SELECTED = "MODEL";


    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_database);

        Button searchMakeButton = findViewById(R.id.searchmake);

        Button searchDetails = findViewById(R.id.searchdetals);
        Button goToShop = findViewById(R.id.shop);
        Button favorites = findViewById(R.id.favorites);
        //Button saveButton = findViewById(R.id.saveButton);
        LinearLayout layout = findViewById(R.id.topfunction);
        RelativeLayout mostOuter = findViewById(R.id.mostOuter);
        ListView listView = findViewById(R.id.model_list);

        TextView makeName = findViewById(R.id.makeName);
        TextView modelName = findViewById(R.id.modelName);

        makeNameInput= findViewById(R.id.makeInput);
        carProgressBar = findViewById(R.id.carProgressBar);

        MyCarDB carDB = new MyCarDB(this);
        db = carDB.getWritableDatabase();

       // ArrayList<Model> elements = new ArrayList<>();

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.navigationOpen, R.string.navigationClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        listView.setAdapter(myAdapter);

        listView.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Model currentModel = modelList.get(pos);
            alertDialogBuilder.setTitle("More information")

                    //What is the message:
                    .setMessage("The selected row is: " + pos + "The database id id:" + id + ". \n"
                    + "ModelID: " + currentModel.getModelID() + ". \n"
                    + "MakeID: " + currentModel.getMakeID() + ".")

                    //Show the dialog
                    .create().show();
            return true;
        });

        listView.setOnItemClickListener((p, b, pos, id) -> {
            Model currentModel = modelList.get(pos);
            selectedModelName = currentModel.getModel();
            Bundle dataToPass = new Bundle();
            dataToPass.putString(MAKE_SELECTED, currentModel.getMake() );
            dataToPass.putString(MODEL_SELECTED, currentModel.getModel());

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
                Intent nextActivity = new Intent(CarDatabase.this, EmptyCarActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        //shared preference for user searched make name
        makeRef = getSharedPreferences("FileName", Context.MODE_PRIVATE);

        String savedString = makeRef.getString("ReserveName", "");
        EditText makeInputField = findViewById(R.id.makeInput);
        makeInputField.setText(savedString);


        searchMakeButton.setOnClickListener(b-> {
            saveSharedPrefs(makeInputField.getText().toString());
            Toast.makeText(CarDatabase.this, "Searched Models for this make", Toast.LENGTH_LONG).show();
            carProgressBar.setVisibility(View.VISIBLE);
            userInput = makeNameInput.getText().toString();
            selectedMakeName = userInput;
            carDBUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/"+userInput+"?format=json";
            CarDataRequest carReq = new CarDataRequest();
            carReq.execute(carDBUrl);  //Type 1
        });


        searchDetails.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("By clicking Yes, you will be redirected to an external link.")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes",(click, arg) -> {
                        String url = "https://www.google.com/search?q=" +selectedMakeName +"+" + selectedModelName;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData( Uri.parse(url) );
                        startActivity(i);
                    })
                    .setNegativeButton("No", (click, arg) -> {})
                    .create().show();
        });

        goToShop.setOnClickListener(v -> {
            String urlShop = "https://www.autotrader.ca/cars/?mdl=" + selectedModelName + "&make=" + selectedMakeName + "&loc=K2G1V8" ;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(urlShop) );
            startActivity(i);
        });

        favorites.setOnClickListener(v -> {
            //This creates a transition to load SecontActivity.java:
            Intent goToFavoritesPage = new Intent(this, Favorites.class);

            //when you click the button, start the next activity:
            startActivity(goToFavoritesPage);
        });

    }



    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = makeRef.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();
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
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.backHomeItem:
                startActivity(new Intent(CarDatabase.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                Toast.makeText(this, "Go to car trivia page", Toast.LENGTH_LONG).show();
                break;
            case R.id.songsterItem:
                Toast.makeText(this, "Go to songster page", Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
                startActivity(new Intent(CarDatabase.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                Toast.makeText(this, "Go to soccer game page", Toast.LENGTH_LONG).show();
                break;
            case R.id.helpItem:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("How to play with the Car Database?")
                        //What is the message:
                        .setMessage("1. Type in the make or brand that you are interested. " +
                                "2. Click the button to search the models of this make. " +
                                "3. After models loaded, long click one item will show you the model info" +
                                "4. You can also list it into your favorites" +
                                "5. Short or normal click will choose that item and your can click search details to get more info for this item with online resources")

                        .create().show();
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

        String message = null;

        switch(item.getItemId())
        {
            case R.id.backHomeItem:
                startActivity(new Intent(CarDatabase.this, MainActivity.class));
                break;
            case R.id.triviaItem:
                Toast.makeText(this, "Go to car trivia page", Toast.LENGTH_LONG).show();
                break;
            case R.id.songsterItem:
                Toast.makeText(this, "Go to songster page", Toast.LENGTH_LONG).show();
                break;
            case R.id.carDBItem:
                startActivity(new Intent(CarDatabase.this, CarDatabase.class));
                break;
            case R.id.soccerItem:
                Toast.makeText(this, "Go to soccer game page", Toast.LENGTH_LONG).show();
                break;
            case R.id.helpItem:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("How to play with the Car Database?")
                        //What is the message:
                        .setMessage("1. Type in the make or brand that you are interested. " +
                                "2. Click the button to search the models of this make. " +
                                "3. After models loaded, long click one item will show you the model info" +
                                "4. You can also list it into your favorites" +
                                "5. Short or normal click will choose that item and your can click search details to get more info for this item with online resources")
                        //What the No button does:
//                        .setNegativeButton(getResources().getString(R.string.closeHelpDialog), (click, arg) -> {
//                        })
                        //Show the dialog
                        .create().show();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }


    public class MyListAdapter extends BaseAdapter{
        public int getCount() { return modelList.size();}

        public Model getItem(int position) { return modelList.get(position); }

        public long getItemId(int position) { return getItem(position).getId(); }

        public View getView(int position, View old, ViewGroup parent)
        {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if(newView == null) {
                newView = inflater.inflate(R.layout.row_layout, parent, false);

            }
            //set what the text should be for this row:
            TextView makeName = newView.findViewById(R.id.makeName);
            TextView modelName = newView.findViewById(R.id.modelName);
            makeName.setText(getItem(position).getMake().toString() );
            modelName.setText(getItem(position).getModel().toString() );

            //return it to be put in the table
            return newView;
        }
    }

    private class CarDataRequest extends AsyncTask< String, Integer, String>{
        String makeName, modelName, makeID, modelID;

        //Type3                     Type1
        public String doInBackground(String ... args)
        {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jObject = new JSONObject(result);
                JSONArray modelJsonArray = jObject.getJSONArray("Results");
                for(int i = 0; i< modelJsonArray.length(); i++) {
                    JSONObject modelObj = (JSONObject) modelJsonArray.get(i);
                    makeName = modelObj.getString("Make_Name");
                    modelName = modelObj.getString("Model_Name");
                    makeID = modelObj.getString("Make_ID");
                    modelID = modelObj.getString("Model_ID");
                    Model model = new Model(makeName, modelName, makeID, modelID);
                    modelList.add(model);
                }

                for(int i = 0; i < modelList.size(); i++) {
                    System.out.println("test" + modelList.get(i). getModel());
                }

            }
            catch (Exception e)
            {

            }

            return "Done";
        }

        //Type 2
        public void onProgressUpdate(Integer ... args)
        {
            carProgressBar.setVisibility(View.VISIBLE);
            carProgressBar.setProgress(args[0]);
        }
        //Type3
        public void onPostExecute(String fromDoInBackground)
        {
            carProgressBar.setVisibility(View.GONE);
            Log.i("HTTP", fromDoInBackground);
            myAdapter.notifyDataSetChanged();

        }
    }




}
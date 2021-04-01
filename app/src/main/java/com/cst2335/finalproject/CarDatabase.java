package com.cst2335.finalproject;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class CarDatabase extends AppCompatActivity {
    MyListAdapter myAdapter= new MyListAdapter();
    ArrayList<Model> modelList = new ArrayList<>();
    private String userInput, carDBUrl;
    private EditText makeNameInput;
    private ProgressBar carProgressBar;
    SQLiteDatabase db;
    String selectedModelName;
    String selectedMakeName;
    SharedPreferences makeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_database);

        Button searchMakeButton = findViewById(R.id.searchmake);

        Button searchDetails = findViewById(R.id.searchdetals);
        Button goToShop = findViewById(R.id.shop);
        Button favorites = findViewById(R.id.favorites);
        Button saveButton = findViewById(R.id.saveButton);
        LinearLayout layout = findViewById(R.id.topfunction);
        LinearLayout mostOuter = findViewById(R.id.mostOuter);
        ListView listView = findViewById(R.id.model_list);

        TextView makeName = findViewById(R.id.makeName);
        TextView modelName = findViewById(R.id.modelName);

        makeNameInput= findViewById(R.id.makeInput);
        carProgressBar = findViewById(R.id.carProgressBar);

        MyCarDB carDB = new MyCarDB(this);
        db = carDB.getWritableDatabase();

        //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);


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
            saveButton.setOnClickListener(v -> {
                ContentValues cValues = new ContentValues();
                cValues.put(MyCarDB.COL_MAKE, makeName.getText().toString());
                cValues.put(MyCarDB.COL_MODEL, modelName.getText().toString());
                long idSave = db.insert(MyCarDB.TABLE_NAME, null, cValues);
                Model model = new Model(makeName.getText().toString(), modelName.getText().toString(), idSave);
                modelList.add(model);
                myAdapter.notifyDataSetChanged();
            });
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
        favorites.setOnClickListener(v -> {
            Snackbar snackbar;
            snackbar = Snackbar.make(mostOuter, "Favorites show your saved car models" , Snackbar.LENGTH_LONG);
            snackbar.show();
            Button goToFavorites = findViewById(R.id.favorites);

            //This creates a transition to load SecontActivity.java:
            Intent goToFavoritesPage = new Intent(this, Favorites.class);

            //when you click the button, start the next activity:
            goToFavorites.setOnClickListener( click -> startActivity( goToFavoritesPage ));
        });

    }

    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = makeRef.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.car_database_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;

        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.helpMenu:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("How to play with the Car Database?")
                        .setMessage("1. Type in the make or brand that you are interested. " +
                                "2. Click the button to search the models of this make. " +
                                "3. After models loaded, long click one item will show you the model info" +
                                "4. You can also list it into your favorites" +
                                "5. Short or normal click will choose that item and your can click search details to get more info for this item with online resources")

                        .create().show();
                break;

        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
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

            /*
            Button saveToFavoriteBtn = newView.findViewById(R.id.saveButton);
            saveToFavoriteBtn.setOnClickListener(v->{
                ContentValues cValues = new ContentValues();
                cValues.put(MyCarDB.COL_MAKE, makeName.getText().toString());
                cValues.put(MyCarDB.COL_MODEL, modelName.getText().toString());
                cValues.put(MyCarDB.COL_MAKEID, getItem(position).getMakeID());
                cValues.put(MyCarDB.COL_MODELID, getItem(position).getModelID());
                long id = db.insert(MyCarDB.TABLE_NAME, null, cValues);
                Model model = new Model(makeName.getText().toString(), modelName.getText().toString(), id);
            });

            */


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
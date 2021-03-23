package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CarDatabase extends AppCompatActivity {
//    private MyListAdapter myAdapter;
    private ArrayList<Model> elements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_database);
        Button searchMakeButton = findViewById(R.id.searchmake);
        Button searchDetails = findViewById(R.id.searchdetals);
        Button goToShop = findViewById(R.id.shop);
        Button favorites = findViewById(R.id.favorites);
        LinearLayout layout = findViewById(R.id.topfunction);
        searchMakeButton.setOnClickListener(b-> Toast.makeText(CarDatabase.this, "Searched Models for this make", Toast.LENGTH_LONG).show());

        ListView listView = findViewById(R.id.model_list);

        searchDetails.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("By clicking Yes, you will be redirected to an external link.")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes",(click, arg) -> {
                        String url = "";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData( Uri.parse(url) );
                        startActivity(i);
                    })
                    .setNegativeButton("No", (click, arg) -> {})
                    .create().show();
        });
        favorites.setOnClickListener(v -> {
            Snackbar snackbar;
            snackbar = Snackbar.make(layout, "Favorites show your saved car models" , Snackbar.LENGTH_LONG);
        });

//        myAdapter = new MyListAdapter();
//        listView.setAdapter(myAdapter);


    }

//    private class MyListAdapter extends BaseAdapter{
//        public int getCount() { return elements.size();}
//
//        public Model getItem(int position) { return elements.get(position); }
//
//        public long getItemId(int position) { return getItem(position).getId(); }
//
//        public View getView(int position, View old, ViewGroup parent)
//        {
//            LayoutInflater inflater = getLayoutInflater();
//            Model model = getItem(position);
//            View newView;
//            if(model.getIsSent()){
//                newView = inflater.inflate(R.layout.send, parent, false);
//            }
//            else {
//                newView = inflater.inflate(R.layout.receive, parent, false);
//            }
//            //set what the text should be for this row:
//            TextView tView = newView.findViewById(R.id.sendView);
//            tView.setText(model.getMessage());
//            return newView;
//        }
//    }

    public class Model{
        private String model;
    }


}
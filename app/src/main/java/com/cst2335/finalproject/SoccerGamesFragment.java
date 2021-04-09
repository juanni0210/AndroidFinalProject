package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.cst2335.finalproject.SavedSoccerGames.savedItems;
import static com.cst2335.finalproject.SavedSoccerGames.mySavedAdapter;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SoccerGamesFragment extends Fragment {

    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    private ImageView imgView;
    SQLiteDatabase db;

    public SoccerGamesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_soccer_games, container, false);
        String title = dataFromActivity.getString(SoccerGames.ITEM_TITLE);
        String image = dataFromActivity.getString(SoccerGames.ITEM_IMAGE);
        String date = dataFromActivity.getString(SoccerGames.ITEM_DATE);
        String description = dataFromActivity.getString(SoccerGames.ITEM_DESCRIPTION);
        String link = dataFromActivity.getString(SoccerGames.ITEM_URL);

        // Show the title of the news
        TextView itemTitle = (TextView)result.findViewById(R.id.newsTitle);
        itemTitle.setText(title);

        // Load the image of the selected news
        imgView = (ImageView) result.findViewById(R.id.image);
        ImageRequest imageReq=new ImageRequest();
        imageReq.execute(image);

        // Show the date of the news
        TextView itemDate = (TextView)result.findViewById(R.id.newsDate);
        itemDate.setText(date);

        // Show the description of the news
        TextView itemDescription = (TextView)result.findViewById(R.id.newsDescription);
        itemDescription.setText(description);

        // Show the link of the news
        TextView itemLink = (TextView)result.findViewById(R.id.newsLink);
        itemLink.setText(link);

        // Load the article’s URL in a browser.
        ImageButton readBtn = result.findViewById(R.id.readBtn);
        readBtn.setOnClickListener(click -> {
                Intent intent=new Intent();
                intent.setData(Uri.parse(dataFromActivity.getString(SoccerGames.ITEM_URL)));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            });

        // connection to database
        SoccerGamesOpener dbOpener = new SoccerGamesOpener(container.getContext());
        db = dbOpener.getWritableDatabase();
        ImageButton saveBtn =result.findViewById(R.id.saveBtn);

        // save items to database
        saveBtn.setOnClickListener(click -> {
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(SoccerGamesOpener.COL_TITLE, title);
            newRowValues.put(SoccerGamesOpener.COL_DATE, date);
            newRowValues.put(SoccerGamesOpener.COL_IMAGE, image);
            newRowValues.put(SoccerGamesOpener.COL_LINK, link);
            newRowValues.put(SoccerGamesOpener.COL_DESCRIPTION, description);
            long newId = db.insert(SoccerGamesOpener.TABLE_NAME, null, newRowValues);

            Item oneItem = new Item(title, date, image, link, description, newId);
            savedItems.add(oneItem);

            Toast.makeText(container.getContext(), "The news is saved.",Toast.LENGTH_SHORT).show();

        });

        ImageButton favoriteBtn = result.findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnClickListener(click -> {
            Intent goToSaved = new Intent(container.getContext(), SavedSoccerGames.class);
            startActivity(goToSaved);
        });


        // Inflate the layout for this fragment
        return result;
    }

        class ImageRequest extends AsyncTask<String,Integer,String>{
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

}
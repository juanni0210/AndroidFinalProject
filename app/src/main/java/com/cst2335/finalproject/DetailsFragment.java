package com.cst2335.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @author Feiqiong Deng
 * @version version 1
 */
public class DetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;


    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static DetailsFragment newInstance(String param1, String param2) {
//        DetailsFragment fragment = new DetailsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
////        }
//    }

    /**
     * This is to create the view of the fragment to show the details of the selected item.
     * @param inflater this is the layout inflater.
     * @param container this is the view group container.
     * @param savedInstanceState this is the bundle containing data from the SoccerGames activity.
     * @return will return the details of the selected item.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_details, container, false);

        TextView itemDate = (TextView)result.findViewById(R.id.pubDate);
        itemDate.setText(dataFromActivity.getString(SoccerGames.ITEM_DATE));

        TextView itemLink = (TextView)result.findViewById(R.id.link);
        itemLink.setText(dataFromActivity.getString(SoccerGames.ITEM_URL));

        TextView itemDesciption = (TextView)result.findViewById(R.id.description);
        itemDesciption.setText(dataFromActivity.getString(SoccerGames.ITEM_DESCRIPTION));

        TextView itemimg = (TextView)result.findViewById(R.id.test);
        itemimg.setText(dataFromActivity.getString(SoccerGames.ITEM_IMAGE));
        // Inflate the layout for this fragment
        return result;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}
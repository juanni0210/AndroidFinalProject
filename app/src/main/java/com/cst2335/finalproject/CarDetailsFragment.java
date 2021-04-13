/**
 * This is is a piece of an application's user interface or behavior that can be placed in an Activity.
 * The the fragment of car details is used to fragment
 * for tablet and inflate an empty page on another thread for phone
 * @author Sophie Sun
 * @since 1.0
 */
package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class CarDetailsFragment extends Fragment {
    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    private String makeName, modelName;
    private TextView makeDetail, modelDetail;
    private Button addRemoveBtn;
    boolean isInDatabase;
    SQLiteDatabase dbFragment;
    MyCarDB carDBopener;



    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return View: the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataFromActivity = getArguments();
        makeName = dataFromActivity.getString(CarDatabase.MAKE_SELECTED);
        modelName = dataFromActivity.getString(CarDatabase.MODEL_SELECTED);

        View newView = inflater.inflate(R.layout.fragment_car_details, container, false);

        makeDetail = newView.findViewById(R.id.makeDetail);
        modelDetail = newView.findViewById(R.id.modelDetail);

        makeDetail.setText(makeName);
        modelDetail.setText(modelName);

        addRemoveBtn = newView.findViewById(R.id.addDeleteBtn);

        carDBopener = new MyCarDB(container.getContext());
        dbFragment = carDBopener.getWritableDatabase();
        setButtonText();

        addRemoveBtn.setOnClickListener(v -> {
            if (isInDatabase) {
                dbFragment.delete(MyCarDB.TABLE_NAME, MyCarDB.COL_MAKE + " = ? and " + MyCarDB.COL_MODEL + " = ?", new String[]{makeName, modelName});
                setButtonText();
            } else {
               ContentValues cValues = new ContentValues();
               cValues.put(MyCarDB.COL_MAKE, makeName);
                cValues.put(MyCarDB.COL_MODEL, modelName);
               long idSave = dbFragment.insert(MyCarDB.TABLE_NAME, null, cValues);
               setButtonText();
               Toast.makeText(container.getContext(), "Added to your favorite list!", Toast.LENGTH_LONG).show();
            }

        });

        Button goToShop = newView.findViewById(R.id.shop);
        goToShop.setOnClickListener(v -> {
            String urlShop = "https://www.autotrader.ca/cars/?mdl=" + modelName + "&make=" + makeName + "&loc=K2G1V8" ;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData( Uri.parse(urlShop) );
            startActivity(i);
        });



        return newView;
    }

    private void setButtonText() {
        Cursor results = dbFragment.query(false, MyCarDB.TABLE_NAME, new String[]{MyCarDB.COL_MAKE, MyCarDB.COL_MODEL},
                MyCarDB.COL_MAKE + " = ? and " + MyCarDB.COL_MODEL + " = ?", new String[]{makeName, modelName}, null, null, null, null );

        if (results.getCount() > 0) {
            isInDatabase = true;
            addRemoveBtn.setText(getResources().getString(R.string.deleteFromFavorite));
        } else {
            isInDatabase = false;
            addRemoveBtn.setText(getResources().getString(R.string.addToFavorite));
        }
    }


}
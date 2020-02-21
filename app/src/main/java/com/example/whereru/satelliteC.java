package com.example.whereru;

import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by prajakta on 3/24/2018.
 */

public class satelliteC extends AppCompatActivity {

    Button setViewe;
    private GoogleMap mMap;

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);


        setViewe = (Button) findViewById(R.id.satellite);
        setViewe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    setViewe.setText("NORM");
                }
                else
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    setViewe.setText("SAT");
                }

            }
        });
    }
}

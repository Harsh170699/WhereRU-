package com.example.whereru;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button setView;
    private static final String TAG = "MapActivity";
    private EditText mSearchText;

    private final static int PLACE_PICKER_REQUEST = 999;

    Button btnset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    /*    init();*/

        //mSearchText=(EditText)findViewById(R.id.input_search);

        btnset=(Button)findViewById(R.id.btnset);
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    btnset.setText("Norm");
                }
                else
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    btnset.setText("Sat");
                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkPermissionOnActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    String placeName = String.format("Place: %s", place.getName());
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        // for activty
                        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                        // for fragment
                        //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }

            }
        }
    }

    private void checkPermissionOnActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                // Do something with the place


            }
        }


    }

 /*   private void init()
    {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER)
                {

                    geoLocate();

                }

                return false;
            }
        });
    }
*/
   /* private void geoLocate()
    {
        Log.d(TAG,"geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list=new ArrayList<>();
        try {

            list=geocoder.getFromLocationName(searchString, 1);


        }
        catch (IOException e)
        {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0)
        {
            Address address= list.get(0);
            Log.d(TAG, "geoLocate: found a location " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        }

    }
*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng india = new LatLng(-34, 151);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        mMap.addMarker(new MarkerOptions().position(india).title("Marker in India"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        if ((ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "ifffff", Toast.LENGTH_SHORT);
            mMap.setMyLocationEnabled(true);

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // Toast.makeText(this, "elseee", Toast.LENGTH_SHORT);
            mMap.setMyLocationEnabled(true);

            Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (!mMap.isMyLocationEnabled()) {
                mMap.setMyLocationEnabled(true);
                Toast.makeText(this, "yesss", Toast.LENGTH_SHORT);
                 LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);



                if (myLocation == null) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    String provider = locationManager.getBestProvider(criteria, true);
                    myLocation = locationManager.getLastKnownLocation(provider);

                } else if (myLocation != null) {
                    LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
                }

            }
             /*btnset=(Button)findViewById(R.id.btnset);
        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    btnset.setText("Norm");
                }
                else
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    btnset.setText("Sat");
                }

            }
        });*/

        }
        }

    }


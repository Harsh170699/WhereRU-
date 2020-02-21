package com.example.whereru;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bolts.Bolts;

public class NaviActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    ImageView img;
    TextView txtEmail, txtName;
    private Button btnGetOthersLocation;
    private Button btnUpdateMyLocation;

    private GoogleMap mMap;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private String email;
    private LatLng myCurrentLocation;
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnGetOthersLocation = (Button) findViewById(R.id.btnGetOthersLocation);
        btnUpdateMyLocation = (Button) findViewById(R.id.btnUpdateMyLocation);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        img = (ImageView) findViewById(R.id.img);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v();
            }
        });
*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference()
                .child("users")
                .child(SignActivity.encodeUserEmail(
                        currentUser.getEmail()
                    )
                )
                .child("friends");

        View headerLayout = navigationView.getHeaderView(0);
        txtEmail = (TextView) headerLayout.findViewById(R.id.txtEmail);
        // txtName =   (TextView) headerLayout.findViewById(R.id.txtName);

        email = new PrefManager(this).getEmail();
        txtEmail.setText(email);
        // txtName.setText(new PrefManager(this).getfname());

        btnGetOthersLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userList.clear();
                mMap.clear();
                mDatabaseReference.orderByChild("requestUser")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildren().iterator().hasNext()) {

                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    final String emailAddress = postSnapshot.getValue(Request.class).getRequestUser();
                                    String encodedEmail = SignActivity.encodeUserEmail(emailAddress);
                                    FirebaseDatabase.getInstance().getReference(
                                            "users/" + encodedEmail
                                    ).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User user = new User();
                                            Boolean flag = false;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.getKey().equals("currentLocation")) {
                                                    user.setCurrentLocation(snapshot.getValue().toString());
                                                    flag = true;
                                                }
                                                if (snapshot.getKey().equals("firstName")) {
                                                    user.setFirstName(snapshot.getValue().toString());
                                                }
                                            }
                                            if (flag) userList.add(user);
                                            showMarkersForEachUsers(userList);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.d("Whereru", "Email " + emailAddress + " is not user yet.");
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });

        btnUpdateMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(myCurrentLocation == null || (myCurrentLocation.latitude == 0 && myCurrentLocation.longitude == 0)) return;

                    final String myLocation = (new StringBuilder())
                            .append(myCurrentLocation.latitude)
                            .append(",")
                            .append(myCurrentLocation.longitude)
                            .toString();
                    final String encodedEmail = SignActivity.encodeUserEmail(email);
                    FirebaseDatabase.getInstance().getReference("users/" + encodedEmail)
                            .child("currentLocation").setValue(myLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showMarkersForEachUsers(List<User> userList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(User user : userList) {
            String latLngStr = user.getCurrentLocation();
            if(latLngStr != null && !latLngStr.isEmpty()) {
                LatLng latLng = new LatLng(Double.parseDouble(latLngStr.split(",")[0]), Double.parseDouble(latLngStr.split(",")[1]));
                String name = user.getFirstName();
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(name));
                marker.showInfoWindow();
                builder.include(latLng);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                mMap.animateCamera(cu);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            new PrefManager(this).clearAll();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finishAffinity();
        } else if (id == R.id.add_friend) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Send Friendship request");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String emailAddress = input.getText().toString();
                    FirebaseUser user = FirebaseAuth.getInstance()
                            .getCurrentUser();
                    Request request = new Request(user.getEmail(), false);
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(
                                    SignActivity.encodeUserEmail(emailAddress)
                            )
                            .child("requests")
                            .child(
                                    SignActivity.encodeUserEmail(user.getEmail())
                            )
                            .setValue(request);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /*@SuppressWarnings("StatementWithEmptyBody")*/
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.Fragment fragment = null;
        /*if (id == R.id.nav_talks) {
            // Handle the camera action
        }--> else */
        if (id == R.id.nav_circles) {
            Fragment Frag;
            Frag = new chats();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_frag, Frag);
            ft.addToBackStack(null);
            ft.commit();




        }else if (id == R.id.nav_requests) {
            Fragment Frag;
            Frag = new requests();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_frag, Frag);
            ft.addToBackStack(null);
            ft.commit();




        }else if(id == R.id.nav_call){

            Fragment Frag;
            Frag = new Contactus();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_frag, Frag);
            ft.addToBackStack(null);
            ft.commit();


        }/* else if (id == R.id.nav_satellite) {

            Fragment Frag;
            Frag = new Satellite();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_frag, Frag);
            ft.addToBackStack(null);
            ft.commit();


        }*/ else if (id == R.id.nav_setting) {

            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);


        } else if (id == R.id.nav_help) {

            Fragment Frag;
            Frag = new help();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_frag, Frag);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_about) {


            Fragment Frag;
            Frag = new AboutUs();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_frag, Frag);
            ft.addToBackStack(null);
            ft.commit();
        }
//replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void v() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(NaviActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {


            @Override

            public void onClick(DialogInterface Dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                } else if (options[item].equals("Cancel")) {

                    Dialog.dismiss();

                }

            }

        });
        builder.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34 22.291924, 151 73.217051);
        LatLng hcmus = new LatLng(-34, 151);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        if (ActivityCompat.checkSelfPermission(NaviActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NaviActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "i ifffff", Toast.LENGTH_SHORT);
            ActivityCompat.requestPermissions(NaviActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // Toast.makeText(this, "elseee", Toast.LENGTH_SHORT);
            if (!mMap.isMyLocationEnabled()) {
                mMap.setMyLocationEnabled(true);
                Toast.makeText(this, "yesss", Toast.LENGTH_SHORT);
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (myLocation == null) {
                   // Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    String provider = locationManager.getBestProvider(criteria, true);
                    myLocation = locationManager.getLastKnownLocation(provider);
                    myCurrentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 14), 1500, null);
                } else if (myLocation != null) {

                    myCurrentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 14), 1500, null);
                }

            }
        }
    }
}


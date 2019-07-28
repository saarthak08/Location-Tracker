package com.sg.hackamu.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.R;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.utils.FirebaseUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton floatingActionButton;
    LocationManager locationManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private Faculty user;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationRequest mLocationRequest;
    LatLng mylatlng;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    double userlatitude;
    double userlongitude;
    Marker marker2;
    private MarkerOptions markerOptions;
    LatLng userlatLng;
    int count=0;
    Polyline polyline1;
    Location marker;
    List<LatLng> polylinelist;
    Location currentLocation;
    private TextView textView;
    private long UPDATE_INTERVAL = 5 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        marker=new Location("");
        markerOptions=new MarkerOptions();
        floatingActionButton=findViewById(R.id.floatingActionButtonMaps);
        polylinelist=new ArrayList<LatLng>(2);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            buildAlertMessageNoGps();
        }
        else{
            checkUserPermission();
        }
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlatitude, userlongitude), 17.0f));
                    }
                } catch (Exception e) {
                    Log.d("Maps",e.getMessage());

                }
            }    });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


    public void MapUpdates()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startLocationUpdates();
        textView=findViewById(R.id.textView4);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseUtils.getDatabase();
        reference=firebaseDatabase.getReference();
        Intent k=getIntent();
        user=k.getParcelableExtra("faculty");
        reference.child("geocordinates").child(user.getUuid()).keepSynced(true);
        reference.child("geocordinates").child(user.getUuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!= 0) {
                    startLocationUpdates();
                    userlatitude = dataSnapshot.child("latitude").getValue(Double.class);
                    userlongitude = dataSnapshot.child("longitude").getValue(Double.class);
                    if(marker!=null)
                    {
                        marker.reset();
                    }
                    marker.setLatitude(userlatitude);
                    marker.setLongitude(userlongitude);
                    marker.setAccuracy(dataSnapshot.child("accuracy").getValue(Float.class));
                    marker.setAltitude(dataSnapshot.child("altitude").getValue(Double.class));
                    userlatLng = new LatLng(userlatitude, userlongitude);
                    // Toast.makeText(MapsActivity.this, "" + userlongitude + userlatitude, Toast.LENGTH_SHORT).show();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    if(mapFragment!=null) {
                        mapFragment.getMapAsync(MapsActivity.this);

                    }
                }
                else{
                    Toast.makeText(MapsActivity.this,user.getName()+" isn't sharing location now.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setTrafficEnabled(true);
        if(userlatLng!=null) {
            if(marker2!=null)
            {
                marker2.remove();
            }
            markerOptions.position(userlatLng).title(user.getName());
            marker2=googleMap.addMarker(markerOptions);
            if(count==0) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlatitude, userlongitude), 17.0f));
                count++;
            }
            if(mylatlng!=null)
            {
                if(polyline1==null) {
                    // Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_SHORT).show();
                    polyline1 = googleMap.addPolyline(new PolylineOptions()
                            .add(mylatlng,
                                    mylatlng,
                                    userlatLng).width(5).color(Color.BLUE).geodesic(true).zIndex(5f).visible(true));
                }
                else
                {
                    //   Toast.makeText(getApplicationContext(),"Hi",Toast.LENGTH_SHORT).show();
                    polylinelist.clear();
                    polylinelist.add(mylatlng);
                    polylinelist.add(userlatLng);
                    polyline1.setPoints(polylinelist);
                }
            }
        }
        mMap = googleMap;
    }

    public void onLocationChanged(Location location) {
        mylatlng = new LatLng(location.getLatitude(), location.getLongitude());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment!=null) {
            mapFragment.getMapAsync(MapsActivity.this);

        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            if(location.hasAccuracy())
                            {
                                if(location.getAccuracy()<10)
                                {
                                    onLocationChanged(location);

                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        if (locationResult != null) {
                            if(marker.hasAccuracy()) {
                                currentLocation = locationResult.getLastLocation();
                                String str=Float.toString(currentLocation.distanceTo(marker));
                                double x=Double.parseDouble(str);
                                DecimalFormat df = new DecimalFormat("#");
                                df.setMaximumFractionDigits(2);
                                textView.setText("Distance: "+ df.format(x)+" metres (approx)");
                            }
                            //Toast.makeText(MapsActivity.this,"Distance: "+currentLocation.distanceTo(marker)+"metres",Toast.LENGTH_SHORT).show();
                            if(locationResult.getLastLocation().hasAccuracy())
                            {
                                if(locationResult.getLastLocation().getAccuracy()<10)
                                {
                                    onLocationChanged(locationResult.getLastLocation());

                                }
                            }
                        }
                    }
                },
                Looper.myLooper());
    }

    void buildAlertMessageNoGps() {
        final AlertDialog.Builder builders = new AlertDialog.Builder(this);
        builders.setMessage("Your GPS seems to be disabled or isn't set to \'High Accuracy\'. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        permissions();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(MapsActivity.this,"Error! Turn on GPS! ",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builders.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MapUpdates();
                } else {
                    Toast.makeText(MapsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
            else
            {
                MapUpdates();
            }
        }
        else{
            MapUpdates();
        }
    }

    public void permissions()
    {
        checkUserPermission();
    }



    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(android.R.drawable.arrow_down_float
                                ), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

}

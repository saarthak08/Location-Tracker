package com.sg.hackamu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.model.User;
import com.sg.hackamu.utils.FirebaseUtils;

import java.io.IOException;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private User user;
    private LocationRequest mLocationRequest;
    LatLng mylatlng;
    double userlatitude;
    double userlongitude;
    MarkerOptions markerOptions;
    LatLng userlatLng;
    Location marker;
    Location currentLocation;
    private TextView textView;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        user=k.getParcelableExtra("user");
        reference.child("geocordinates").child(user.getUuid()).keepSynced(true);
        reference.child("geocordinates").child(user.getUuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!= 0) {
                    startLocationUpdates();
                    userlatitude = dataSnapshot.child("latitude").getValue(Double.class);
                    userlongitude = dataSnapshot.child("longitude").getValue(Double.class);
                    marker = new Location("");
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
                    Toast.makeText(MapsActivity.this,"Error! User has not updated his/her location",Toast.LENGTH_SHORT).show();
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
        googleMap.clear();
        MarkerOptions markerOptions=new MarkerOptions();
        if(userlatLng!=null) {
            markerOptions.position(userlatLng).title("User Position");
            googleMap.addMarker(markerOptions);
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
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {


                        // do work here
                        if (locationResult != null) {
                            if(marker!=null) {
                                currentLocation = locationResult.getLastLocation();
                                currentLocation.distanceTo(marker);
                                textView.setText("Distance: "+ currentLocation.distanceTo(marker)+" metres (approx)");
                            }
                            //Toast.makeText(MapsActivity.this,"Distance: "+currentLocation.distanceTo(marker)+"metres",Toast.LENGTH_SHORT).show();
                            if(locationResult.getLastLocation().hasAccuracy())
                            {
                                if(locationResult.getLastLocation().getAccuracy()<30)
                                {
                                    onLocationChanged(locationResult.getLastLocation());

                                }
                            }
                        }
                    }
                },
                Looper.myLooper());
    }


}
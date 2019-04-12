package com.sg.hackamu.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class GetLocation extends Service {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 20 * 1000;  /* 20 secs */
    private long FASTEST_INTERVAL = 10000; /* 10 sec */
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private Context context;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference();
        startLocationUpdates();
    }


    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
                getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                // do work here
                                // GPS location can be null if GPS is switched off
                                if (locationResult.getLastLocation() != null) {
                                    if(locationResult.getLastLocation().hasAccuracy())
                                    {
                                        if(locationResult.getLastLocation().getAccuracy()<50)
                                        {
                                            onLocationChanged(locationResult.getLastLocation());

                                        }
                                    }

                                }
                            }
                        },
                        Looper.myLooper());
            }

    public void onLocationChanged(Location location) {
        // You can now create a LatLng Object for use with maps
       // Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();
        reference.child("geocordinates").child(firebaseUser.getUid()).setValue(location);
        //reference.child(("geocordinates")).child(firebaseUser.getUid()).child("latitude").setValue(location.getLatitude());
        //reference.child("geocordinates").child(firebaseUser.getUid()).child("longitude").setValue(location.getLongitude());
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                            onLocationChanged(location);
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

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }
}


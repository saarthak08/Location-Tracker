package com.sg.hackamu.faculties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.adapters.StudentsAdapter;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.User;
import com.sg.hackamu.services.ChatNotification;
import com.sg.hackamu.services.GetLocation;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;

public class FacultyMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private String TAG="MainActivityFaculty";
    private ArrayList<User> users=new ArrayList<>();
    String uuid;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    View parent;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    StudentsAdapter studentsAdapter;
    FloatingActionButton floatingActionButton;
    public static boolean check=false;
    int notificationId=1;
    NotificationCompat.Builder builder;
    public static int l=0;
    NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("All Users");
        // getActionBar().setTitle("All Users");
        progressBar=findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.VISIBLE);
        parent=findViewById(android.R.id.content);
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        showNotification();
        myRef.child("students").keepSynced(true);
        floatingActionButton=findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.VISIBLE);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(FacultyMainActivity.this));
        studentsAdapter =new StudentsAdapter(FacultyMainActivity.this,users);
        recyclerView.setAdapter(studentsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(FacultyMainActivity.this,DividerItemDecoration.VERTICAL));
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseUser.reload();
                        mFirebaseDatabase.goOffline();
                        mFirebaseDatabase.goOnline();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },4000);

            }
        });
        View headerView = navigationView.getHeaderView(0);
        TextView email = (TextView) headerView.findViewById(R.id.emailnav);
        email.setText(firebaseUser.getEmail());
        TextView name=headerView.findViewById(R.id.namenav);
        name.setText(firebaseUser.getDisplayName());
        myRef.child("students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                showData(dataSnapshot);
                progressBar.setVisibility(View.INVISIBLE);
                studentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check)
                {
                   check=false;
                   l=0;
                    Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
                    getApplicationContext().stopService(x);
                    notificationManager.cancel(notificationId);
                    Snackbar.make(v,"Location Hidden",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        buildAlertMessageNoGps();
                    }
                    else{
                        check=true;
                        checkUserPermission();
                    }

                }
            }
        });
        Intent o=new Intent(FacultyMainActivity.this, ChatNotification.class);
        startService(o);
    }
    private void showData(DataSnapshot dataSnapshot){
        User fc=new User();
        uuid= dataSnapshot.getKey();
        if(!uuid.equals(firebaseUser.getUid())) {
            fc.setName((dataSnapshot.getValue(User.class).getName()));
            fc.setUuid(uuid);
            fc.setEnno(dataSnapshot.getValue(User.class).getEnno());
            fc.setFacultyno(dataSnapshot.getValue(User.class).getFacultyno());
            fc.setEmail(dataSnapshot.getValue(User.class).getEmail());
            users.add(fc);
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.messages) {
        } else if (id == R.id.requests) {

        } else if (id == R.id.tools) {

        } else if (id == R.id.connections) {

        } else if (id == R.id.signout) {

            firebaseAuth.signOut();
            loadLauncherActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadLauncherActivity()
    {
        startActivity(new Intent(FacultyMainActivity.this, FacultyLogin.class));
        FacultyMainActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    l=1;
                    Snackbar.make(parent,"Location Visible",Snackbar.LENGTH_SHORT).show();
                    notificationManager.notify(notificationId, builder.build());
                    Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
                    startService(x);
                } else {
                    Toast.makeText(FacultyMainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
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
                l=1;
                Snackbar.make(parent,"Location Visible",Snackbar.LENGTH_SHORT).show();
                notificationManager.notify(notificationId, builder.build());
                Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
                startService(x);
            }
        }
        else{
            l=1;
            Snackbar.make(parent,"Location Visible",Snackbar.LENGTH_SHORT).show();
            notificationManager.notify(notificationId, builder.build());
            Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
            startService(x);
        }
    }


    @Override
    protected void onDestroy() {
        myRef.child("geocordinates").child(firebaseUser.getUid()).removeValue();
        Intent x= new Intent(FacultyMainActivity.this, GetLocation.class);
        getApplicationContext().stopService(x);
        Intent o=new Intent(FacultyMainActivity.this, ChatNotification.class);
        getApplicationContext().stopService(o);
        super.onDestroy();
    }
    void buildAlertMessageNoGps() {
        final AlertDialog.Builder builders = new AlertDialog.Builder(this);
        builders.setMessage("Your GPS seems to be disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(FacultyMainActivity.this,"Error! Turn on GPS! ",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builders.create();
        alert.show();
    }

    public void showNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="Location Updates";
            String description = "Your realtime location is currently shared.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, FacultyMainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Location Updates")
                .setContentText("Your realtime location is currently shared.\nTap to hide it.")
                .setColorized(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{500, 500})
                .setPriority(NotificationCompat.PRIORITY_HIGH).setOngoing(true);

        notificationManager  = NotificationManagerCompat.from(getApplicationContext());
        //notificationManager.notify(notificationId, builder.build());
    }

    public void permissions()
    {
        checkUserPermission();
    }

}

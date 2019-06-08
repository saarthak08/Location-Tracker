package com.sg.hackamu.students;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.R;
import com.sg.hackamu.adapters.FacultiesAdapter;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.User;
import com.sg.hackamu.services.ChatNotification;
import com.sg.hackamu.services.LocationNotification;
import com.sg.hackamu.utils.FirebaseUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView emailnav;
    TextView namenav;
    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    User user;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth.AuthStateListener authStateListener;
    private String TAG="MainActivity";
    private ArrayList<Faculty> faculties=new ArrayList<>();
    String uuid;


    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    FacultiesAdapter allConnectionsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connected Faculties");
        progressBar=findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.VISIBLE);
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        myRef = mFirebaseDatabase.getReference();
        myRef.child("faculties").keepSynced(true);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        allConnectionsAdapter=new FacultiesAdapter(MainActivity.this,faculties);
        recyclerView.setAdapter(allConnectionsAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");
            }
        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        myRef.child("students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                        user = dataSnapshot.getValue(User.class);
                        View headerView = navigationView.getHeaderView(0);
                        TextView email = (TextView) headerView.findViewById(R.id.emailnav);
                        if (user.getEmail()==null) {
                            email.setText(user.getPhoneno());
                        } else {
                            email.setText(user.getEmail());
                        }
                        TextView name = headerView.findViewById(R.id.namenav);
                        name.setText(user.getName());
                    }
                }
                    catch(Exception e)
                    {
                        Log.d("NavMenu", e.getMessage());
                    }

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

        navigationView.getMenu().getItem(0).setChecked(true);
        myRef.child("faculties").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                showData(dataSnapshot);
                progressBar.setVisibility(View.INVISIBLE);
                allConnectionsAdapter.notifyDataSetChanged();

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
              //  Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        Intent l=new Intent(MainActivity.this,LocationNotification.class);
        Intent o=new Intent(MainActivity.this, ChatNotification.class);
        startService(l);
        startService(o);
    }
    private void showData(DataSnapshot dataSnapshot) {
        Faculty u = new Faculty();
        uuid = dataSnapshot.getKey();
        if (firebaseUser != null) {
            if (!uuid.equals(firebaseUser.getUid())) {
                try {
                    u.setName((dataSnapshot.getValue(Faculty.class).getName()));
                    u.setUuid(uuid);
                    u.setCollege(dataSnapshot.getValue(Faculty.class).getCollege());
                    u.setPhoneno(dataSnapshot.getValue(Faculty.class).getPhoneno());
                    u.setEmail(dataSnapshot.getValue(Faculty.class).getEmail());
                    u.setDepartment(dataSnapshot.getValue(Faculty.class).getDepartment());
                    u.setEmployeeid(dataSnapshot.getValue(Faculty.class).getEmployeeid());
                } catch (Exception e) {
                    Log.d("showDataStudent", e.getMessage());
                }
                faculties.add(u);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
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
            if(authStateListener!=null)
            {
                firebaseAuth.removeAuthStateListener(authStateListener);
            }
            Intent l=new Intent(MainActivity.this,LocationNotification.class);
            Intent o=new Intent(MainActivity.this, ChatNotification.class);
            getApplicationContext().stopService(l);
            getApplicationContext().stopService(o);
            firebaseAuth.signOut();
            loadLauncherActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadLauncherActivity()
    {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        MainActivity.this.finish();
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


}

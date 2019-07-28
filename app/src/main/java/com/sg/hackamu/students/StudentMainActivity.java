package com.sg.hackamu.students;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.R;
import com.sg.hackamu.adapters.FacultiesAdapter;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.models.Faculty;
import com.sg.hackamu.models.Student;
import com.sg.hackamu.services.ChatNotification;
import com.sg.hackamu.services.LocationNotification;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.viewmodel.FacultyViewModel;
import com.sg.hackamu.viewmodel.StudentViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StudentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView emailnav;
    TextView namenav;
    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    Student student;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FacultyViewModel facultyViewModel;
    private StudentViewModel studentViewModel;
    FirebaseAuth.AuthStateListener authStateListener;
    private String TAG="StudentMainActivity";
    private ArrayList<Faculty> faculties=new ArrayList<>();
    String uuid;
    private NavigationView navigationView;
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
        mFirebaseDatabase = FirebaseUtils.getDatabase();
        studentViewModel= ViewModelProviders.of(StudentMainActivity.this).get(StudentViewModel.class);
        facultyViewModel=ViewModelProviders.of(StudentMainActivity.this).get(FacultyViewModel.class);
        myRef = mFirebaseDatabase.getReference();
        myRef.child("faculties").keepSynced(true);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentMainActivity.this));
        allConnectionsAdapter=new FacultiesAdapter(StudentMainActivity.this,faculties);
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
        recyclerView.addItemDecoration(new DividerItemDecoration(StudentMainActivity.this,DividerItemDecoration.VERTICAL));
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseUser.reload();
                        mFirebaseDatabase.goOffline();
                        mFirebaseDatabase.goOnline();
                        myRef.child("students").keepSynced(true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },4000);

            }
        });
        loadDataFromDatabase();
        loadNavigationMenu();
        Intent l=new Intent(StudentMainActivity.this,LocationNotification.class);
        Intent o=new Intent(StudentMainActivity.this, ChatNotification.class);
        startService(l);
        startService(o);
    }

    private void loadDataFromDatabase(){
        facultyViewModel.getAllFaculties().observe(StudentMainActivity.this, new Observer<List<DataSnapshot>>() {
            @Override
            public void onChanged(List<DataSnapshot> dataSnapshots) {
                faculties.clear();
                for(DataSnapshot dataSnapshot:dataSnapshots){
                    showData(dataSnapshot);
                    progressBar.setVisibility(View.INVISIBLE);
                    allConnectionsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadNavigationMenu(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        studentViewModel.getAllStudents().observe(StudentMainActivity.this, new Observer<List<DataSnapshot>>() {
            @Override
            public void onChanged(List<DataSnapshot> dataSnapshots) {
                for (DataSnapshot dataSnapshot : dataSnapshots) {
                    try {
                        if (dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                            student = dataSnapshot.getValue(Student.class);
                            View headerView = navigationView.getHeaderView(0);
                            TextView email = (TextView) headerView.findViewById(R.id.emailnav);
                            if (student.getEmail() == null) {
                                email.setText(student.getPhoneno());
                            } else {
                                email.setText(student.getEmail());
                            }
                            TextView name = headerView.findViewById(R.id.namenav);
                            name.setText(student.getName());
                            ImageView imageView = headerView.findViewById(R.id.imageViewMe);
                            if (firebaseUser.getPhotoUrl() != null) {
                                Glide.with(StudentMainActivity.this).load(firebaseUser.getPhotoUrl()).into(imageView);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("NavMenu", e.getMessage());
                    }
                }
            }
        });
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
            Intent l=new Intent(StudentMainActivity.this,LocationNotification.class);
            Intent o=new Intent(StudentMainActivity.this, ChatNotification.class);
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
        startActivity(new Intent(StudentMainActivity.this, StudentLogin.class));
        StudentMainActivity.this.finish();
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

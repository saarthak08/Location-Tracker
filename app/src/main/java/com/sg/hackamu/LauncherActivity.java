package com.sg.hackamu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sg.hackamu.databinding.ActivityLauncherBinding;
import com.sg.hackamu.faculties.FacultyLogin;
import com.sg.hackamu.faculties.FacultyMainActivity;
import com.sg.hackamu.students.StudentLogin;
import com.sg.hackamu.students.StudentMainActivity;
import com.sg.hackamu.utils.FirebaseUtils;
import com.sg.hackamu.utils.VerifyActivity;


public class LauncherActivity extends AppCompatActivity {
    private ActivityLauncherBinding launcherBinding;
    private Button fcbutton;
    private Button stbutton;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseUtils.getDatabase();
        databaseReference=firebaseDatabase.getReference();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        if (firebaseUser!= null) {
            try{
            if(!firebaseUser.isEmailVerified()&&firebaseUser.getEmail().length()!=0)
            {
                startActivity(new Intent(LauncherActivity.this, VerifyActivity.class));
                LauncherActivity.this.finish();
            }
            else{
                databaseReference.child("faculties").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot s: dataSnapshot.getChildren())
                        {
                            if(firebaseUser.getUid().equals(s.getKey()))
                            {
                                startActivity(new Intent(LauncherActivity.this, FacultyMainActivity.class));
                                LauncherActivity.this.finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                databaseReference.child("students").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot s: dataSnapshot.getChildren())
                        {
                            if(firebaseUser.getUid().equals(s.getKey()))
                            {
                                startActivity(new Intent(LauncherActivity.this, StudentMainActivity.class));
                                LauncherActivity.this.finish();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }}
          catch (Exception e)
            {
                Log.d("LauncherException",e.getMessage());
            }}else {
            setContentView(R.layout.activity_launcher);
            launcherBinding = DataBindingUtil.setContentView(LauncherActivity.this, R.layout.activity_launcher);
            launcherBinding.setClickHandlers(new LauncherActivityClickHandlers());
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

    public class LauncherActivityClickHandlers{
        public void onFacultyButtonClicked(View view)
        {
            startActivity(new Intent(LauncherActivity.this, FacultyLogin.class));
            LauncherActivity.this.finish();
        }


        public void onStudentButtonClicked(View view)
        {
            startActivity(new Intent(LauncherActivity.this, StudentLogin.class));
            LauncherActivity.this.finish();
        }
    }
}
package com.sg.hackamu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sg.hackamu.databinding.ActivityLauncherBinding;
import com.sg.hackamu.offlinelogin.DBViewModel;
import com.sg.hackamu.offlinelogin.model.User;


public class LauncherActivity extends AppCompatActivity {
    private DBViewModel viewModel;
    private ActivityLauncherBinding launcherBinding;
    private Button fcbutton;
    private Button stbutton;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= ViewModelProviders.of(LauncherActivity.this).get(DBViewModel.class);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        if (firebaseUser!= null) {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        } else {
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

        }


        public void onStudentButtonClicked(View view)
        {
            startActivity(new Intent(LauncherActivity.this,LoginActivity.class));
            LauncherActivity.this.finish();
        }
    }
}

package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= ViewModelProviders.of(LauncherActivity.this).get(DBViewModel.class);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
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

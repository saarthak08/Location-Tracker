package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sg.hackamu.databinding.ActivityLauncherBinding;
import com.sg.hackamu.login.LoginRepostory;
import com.sg.hackamu.login.model.User;


public class LauncherActivity extends AppCompatActivity {
    private LoginRepostory loginRepostory;
    private ActivityLauncherBinding launcherBinding;
    private Button fcbutton;
    private Button stbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginRepostory = new LoginRepostory(getApplication());
        User user = loginRepostory.getLogin();
        if (user != null) {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
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
        }
    }
}

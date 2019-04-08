package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.sg.hackamu.databinding.ActivityLauncherBinding;
import com.sg.hackamu.login.LoginRepostory;
import com.sg.hackamu.login.model.User;


public class LauncherActivity extends AppCompatActivity {
    private ActivityLauncherBinding launcherBinding;
    private LoginRepostory loginRepostory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginRepostory = new LoginRepostory(getApplication());
        User user = loginRepostory.getLogin();
        if (user != null) {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            intent.putExtra("user", (Parcelable) user);
        } else {
            setContentView(R.layout.activity_launcher);
            launcherBinding = DataBindingUtil.setContentView(LauncherActivity.this, R.layout.activity_launcher);
            launcherBinding.setClickHandlers(new LauncherActivityClickHandlers());
        }
    }


    public class LauncherActivityClickHandlers{
        public void onButton1Clicked(View view)
        {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void onButton2Clicked(View view)
        {

        }

        public void onButton3Clicked(View view)
        {
            startActivity(new Intent(LauncherActivity.this,LoginActivity.class));
        }

        public void onButton4Clicked(View view)
        {

        }

    }
}

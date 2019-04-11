package com.sg.hackamu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.sg.hackamu.model.User;

public class ChatActivity extends AppCompatActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent i=getIntent();
        if(i.hasExtra("user"))
        {
            user=i.getParcelableExtra("user");
        }
        getSupportActionBar().setTitle(user.getName());
    }
}

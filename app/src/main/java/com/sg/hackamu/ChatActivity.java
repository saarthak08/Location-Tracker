package com.sg.hackamu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.adapter.ChatAdapter;
import com.sg.hackamu.model.ChatMessage;
import com.sg.hackamu.model.User;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    User user;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase firebaseDatabase= FirebaseUtils.getDatabase();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ProgressBar progressBar;
    private ArrayList<ChatMessage> chatMessages=new ArrayList<>();
    EditText editText;
    FloatingActionButton floatingActionButton;
    ChatMessage senderchatMessage,recieverchatmessage;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;

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
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        progressBar=findViewById(R.id.progressBarChat);
        progressBar.setVisibility(View.VISIBLE);
        editText=findViewById(R.id.chattext);
        floatingActionButton=findViewById(R.id.floatingActionButtonSend);
        recyclerView=findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ChatActivity.this,DividerItemDecoration.VERTICAL));
        chatAdapter=new ChatAdapter(chatMessages,ChatActivity.this,firebaseUser);
        recyclerView.setAdapter(chatAdapter);
        reference=firebaseDatabase.getReference();
        reference.child("chats").child(firebaseUser.getUid()).child(user.getUuid()).keepSynced(true);
        reference.child("chats").child(firebaseUser.getUid()).child(user.getUuid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot!=null) {
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessageText(dataSnapshot.getValue(ChatMessage.class).getMessageText());
                        chatMessage.setSenderuuid(dataSnapshot.getValue(ChatMessage.class).getSenderuuid());
                        chatMessage.setRecieveruuid(dataSnapshot.getValue(ChatMessage.class).getRecieveruuid());
                        chatMessage.setMessageTime(dataSnapshot.getValue(ChatMessage.class).getMessageTime());
                        chatMessages.add(chatMessage);
                        progressBar.setVisibility(View.INVISIBLE);
                        chatAdapter.notifyDataSetChanged();
                    }
                progressBar.setVisibility(View.INVISIBLE);


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
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser=firebaseAuth.getCurrentUser();
                Log.d("Auth State","Auth State Changed");

            }
        };
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().length() != 0) {
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                }
                senderchatMessage = new ChatMessage();
                senderchatMessage.setMessageText(editText.getText().toString().trim());
                senderchatMessage.setRecieveruuid(user.getUuid());
                senderchatMessage.setSenderuuid(firebaseUser.getUid());
                reference.child("chats").child(firebaseUser.getUid()).child(user.getUuid()).child(Long.toString(senderchatMessage.getMessageTime())).setValue(senderchatMessage);
                reference.child("chats").child(user.getUuid()).child(firebaseUser.getUid()).child(Long.toString(senderchatMessage.getMessageTime())).setValue(senderchatMessage);
                editText.setText("");

            }
        });
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

}

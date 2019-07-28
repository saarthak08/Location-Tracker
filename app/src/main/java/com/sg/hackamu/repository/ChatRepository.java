package com.sg.hackamu.repository;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sg.hackamu.ChatActivity;
import com.sg.hackamu.adapters.ChatAdapter;
import com.sg.hackamu.models.ChatMessage;
import com.sg.hackamu.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    public FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MutableLiveData<List<ChatMessage>> mutableLiveData=new MutableLiveData<>();


    public ChatRepository(){
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseUtils.getDatabase();
        chatMessages=new ArrayList<>();
        databaseReference=firebaseDatabase.getReference();
    }

    public void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public LiveData<List<ChatMessage>> getAllChatMessages(final String otherUID){
        databaseReference.child("chats").child(firebaseUser.getUid()).child(otherUID).keepSynced(true);
        databaseReference.child("chats").child(firebaseUser.getUid()).child(otherUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshots, @Nullable String s) {
                if (dataSnapshots != null&& ChatActivity.running) {
                    final ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessageText(dataSnapshots.getValue(ChatMessage.class).getMessageText());
                    chatMessage.setSenderuuid(dataSnapshots.getValue(ChatMessage.class).getSenderuuid());
                    chatMessage.setRecieveruuid(dataSnapshots.getValue(ChatMessage.class).getRecieveruuid());
                    chatMessage.setMessageTime(dataSnapshots.getValue(ChatMessage.class).getMessageTime());
                    chatMessage.setRead(true);
                    databaseReference.child("chats").child(otherUID).child(firebaseUser.getUid()).child(Long.toString(chatMessage.getMessageTime())).setValue(chatMessage);
                    chatMessage.setRead(dataSnapshots.getValue(ChatMessage.class).isRead());
                    chatMessages.add(chatMessage);
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.scrollToPosition(chatMessages.size()-1);
                    mutableLiveData.postValue(chatMessages);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    ChatMessage chatMessage=new ChatMessage();
                    int index=-1;
                    chatMessage.setMessageText(dataSnapshot.getValue(ChatMessage.class).getMessageText());
                    chatMessage.setSenderuuid(dataSnapshot.getValue(ChatMessage.class).getSenderuuid());
                    chatMessage.setRecieveruuid(dataSnapshot.getValue(ChatMessage.class).getRecieveruuid());
                    chatMessage.setMessageTime(dataSnapshot.getValue(ChatMessage.class).getMessageTime());
                    chatMessage.setRead(true);
                    for(ChatMessage c:chatMessages)
                    {
                        if(chatMessage.getMessageTime()==c.getMessageTime())
                        {
                            index=chatMessages.indexOf(c);
                        }
                    }
                    chatMessages.set(index,chatMessage);
                    chatAdapter.notifyDataSetChanged();
                    mutableLiveData.postValue(chatMessages);
                }
                catch (Exception e)
                {
                    Log.d("ReadStatusUpdateError",e.getMessage());
                }

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
        return mutableLiveData;
    }

    public void addChatMessage(ChatMessage chatMessage,String otherUID){
        databaseReference.child("chats").child(firebaseUser.getUid()).child(otherUID).child(Long.toString(chatMessage.getMessageTime())).setValue(chatMessage);
        databaseReference.child("chats").child(otherUID).child(firebaseUser.getUid()).child(Long.toString(chatMessage.getMessageTime())).setValue(chatMessage);
    }
}

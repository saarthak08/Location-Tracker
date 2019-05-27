package com.sg.hackamu.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.sg.hackamu.R;
import com.sg.hackamu.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    public static int VIEW_TYPE_MYCHAT= 0;
    public static int VIEW_TYPE_OTHERSCHAT = 1;
    FirebaseUser firebaseUser;


    public ChatAdapter(ArrayList<ChatMessage> chatMessages, Context context, FirebaseUser firebaseUser) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.firebaseUser=firebaseUser;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_MYCHAT)
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatmy,parent,false);
            return new MyChatAdapterViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatothers,parent,false);
            return new OtherChatAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyChatAdapterViewHolder)
        {
            ((MyChatAdapterViewHolder) holder).message.setText(chatMessages.get(position).getMessageText());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy\nhh:mm a");
            String messageTime = simpleDateFormat.format(chatMessages.get(position).getMessageTime());
            ((MyChatAdapterViewHolder) holder).Time.setText(messageTime);
            if(chatMessages.get(position).isRead())
            {
                ((MyChatAdapterViewHolder)holder).read.setText("Read: Yes");
            }
            else{
                ((MyChatAdapterViewHolder)holder).read.setText("Read: No");
            }

        }
        else {
            ((OtherChatAdapterViewHolder) holder).message.setText(chatMessages.get(position).getMessageText());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy\nhh:mm a");
            String messageTime = simpleDateFormat.format(chatMessages.get(position).getMessageTime());
            ((OtherChatAdapterViewHolder) holder).time.setText(messageTime);
        }


    }

    @Override
    public int getItemCount() {
        return chatMessages==null?0:chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).getSenderuuid().equals(firebaseUser.getUid()))
        {
                return VIEW_TYPE_MYCHAT;
        }else
        {
            return VIEW_TYPE_OTHERSCHAT;
        }
    }

    public class MyChatAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView Time;
        TextView read;

        public MyChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.textViewmytext);
            Time=itemView.findViewById(R.id.textViewmytime);
            read=itemView.findViewById(R.id.textViewread);
        }
    }

    public class OtherChatAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView time;
        public OtherChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.textViewotherstext);
            time=itemView.findViewById(R.id.textViewotherstime);
        }
    }
}

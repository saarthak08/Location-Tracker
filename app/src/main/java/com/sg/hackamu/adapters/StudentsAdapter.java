package com.sg.hackamu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sg.hackamu.ChatActivity;
import com.sg.hackamu.R;
import com.sg.hackamu.databinding.StudentsAdapterListItemBinding;
import com.sg.hackamu.models.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.AllConnectionsViewHolder> {
    public Context context;
    private ArrayList<User> users;

    public StudentsAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public AllConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StudentsAdapterListItemBinding StudentsAdapterListItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.students_adapter_list_item,parent,false);
        return new AllConnectionsViewHolder(StudentsAdapterListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllConnectionsViewHolder holder, int position) {
        holder.StudentsAdapterListItemBinding.setUser(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users==null?0:users.size();
    }

    public class AllConnectionsViewHolder extends RecyclerView.ViewHolder
    {
        private StudentsAdapterListItemBinding StudentsAdapterListItemBinding;
        public AllConnectionsViewHolder(@NonNull final StudentsAdapterListItemBinding StudentsAdapterListItemBinding) {
            super(StudentsAdapterListItemBinding.getRoot());
            this.StudentsAdapterListItemBinding=StudentsAdapterListItemBinding;
            StudentsAdapterListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Intent i=new Intent(context, ChatActivity.class);
                    i.putExtra("user",users.get(pos));
                    context.startActivity(i);
                }
            });
        }
    }
}

package com.sg.hackamu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sg.hackamu.ChatActivity;
import com.sg.hackamu.R;

import com.sg.hackamu.databinding.FacultyAdapterListItemBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class faculty_Adapter extends RecyclerView.Adapter<faculty_Adapter.AllConnectionsViewHolder> {
    private Context context;
    private ArrayList<com.sg.hackamu.model.Faculty> Faculty;

    public faculty_Adapter(Context context, ArrayList<com.sg.hackamu.model.Faculty> Faculty) {
        this.context = context;
        this.Faculty = Faculty;
    }

    @NonNull
    @Override
    public AllConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FacultyAdapterListItemBinding allconnectionsListItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.faculty_adapter_list_item,parent,false);
        return new AllConnectionsViewHolder(allconnectionsListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllConnectionsViewHolder holder, int position) {
        holder.allconnectionsListItemBinding.setFaculty(Faculty.get(position));

    }

    @Override
    public int getItemCount() {
        return Faculty==null?0:Faculty.size();
    }

    public class AllConnectionsViewHolder extends RecyclerView.ViewHolder
    {
        private FacultyAdapterListItemBinding allconnectionsListItemBinding;
        public AllConnectionsViewHolder(@NonNull final FacultyAdapterListItemBinding allconnectionsListItemBinding) {
            super(allconnectionsListItemBinding.getRoot());
            this.allconnectionsListItemBinding=allconnectionsListItemBinding;
            allconnectionsListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Intent i=new Intent(context, ChatActivity.class);
                    i.putExtra("Faculty",Faculty.get(pos));
                    context.startActivity(i);
                }
            });
        }
    }
}

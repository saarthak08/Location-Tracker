package com.sg.hackamu.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sg.hackamu.view.ChatActivity;
import com.sg.hackamu.R;

import com.sg.hackamu.databinding.FacultiesAdapterListItemBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class FacultiesAdapter extends RecyclerView.Adapter<FacultiesAdapter.AllConnectionsViewHolder> {
    public Context context;
    private ArrayList<com.sg.hackamu.models.Faculty> Faculty;

    public FacultiesAdapter(Context context, ArrayList<com.sg.hackamu.models.Faculty> Faculty) {
        this.context = context;
        this.Faculty = Faculty;
    }

    @NonNull
    @Override
    public AllConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FacultiesAdapterListItemBinding allconnectionsListItemBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.faculties_adapter_list_item,parent,false);
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
        private FacultiesAdapterListItemBinding allconnectionsListItemBinding;
        public AllConnectionsViewHolder(@NonNull final FacultiesAdapterListItemBinding allconnectionsListItemBinding) {
            super(allconnectionsListItemBinding.getRoot());
            this.allconnectionsListItemBinding=allconnectionsListItemBinding;
            allconnectionsListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Intent i=new Intent(context, ChatActivity.class);
                    i.putExtra("faculty",Faculty.get(pos));
                    context.startActivity(i);
                }
            });
        }
    }
}

package com.example.imageclassificationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imageclassificationapp.model.Photo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder>{
    private final Context context;
    private List<Photo> items = new ArrayList<>();
    public ResultAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userNameInput.setText(items.get(position).userName);
        holder.highestResult.setText(items.get(position).highestScore);
        holder.secondResult.setText(items.get(position).secondHighestScore);
        holder.thirdResult.setText(items.get(position).thirdHighestScore);
        holder.photoName.setText(items.get(position).photoName);

    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public List<Photo> getItems() {
        return items;
    }
    public void addItem(Photo photo){
        boolean isInList = false;
        for(Photo p : items)
        {
            if(p.photoName.equals(photo.photoName))
                isInList=true;
        }
        if(!isInList)
        items.add(photo);
    }
    public void setItems(List<Photo> items) {
        this.items = items = new ArrayList<>();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        TextView userNameInput ;
        TextView highestResult ;
        TextView secondResult ;
        TextView thirdResult ;
        TextView photoName ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameInput = itemView.findViewById(R.id.userNameInput);
            highestResult = itemView.findViewById(R.id.highestResult);
            secondResult = itemView.findViewById(R.id.secondResult);
            thirdResult = itemView.findViewById(R.id.thirdResult);
            photoName = itemView.findViewById(R.id.photoName);

        }
    }
}

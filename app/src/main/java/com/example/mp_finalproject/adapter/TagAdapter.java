package com.example.mp_finalproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_finalproject.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {

    private List<String> tags;

    public TagAdapter(List<String> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TagAdapter", "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Log.d("TagAdapter", "onBindViewHolder called for position: " + position);
        holder.bind(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }
}
package com.example.mp_finalproject;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        Log.d("TagViewHolder", "TagViewHolder created");
        textView = itemView.findViewById(R.id.tag_text1);
    }

    public void bind(String tag) {
        Log.d("TagViewHolder", "bind called with tag: " + tag);
        textView.setText(tag);
    }
}
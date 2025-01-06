package com.example.mp_finalproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tag_text);
    }

    public void bind(String tag) {
        textView.setText(tag);
    }
}

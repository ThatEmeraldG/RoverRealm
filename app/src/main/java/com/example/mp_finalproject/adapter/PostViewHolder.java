package com.example.mp_finalproject.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_finalproject.R;

public class PostViewHolder extends RecyclerView.ViewHolder{

    ImageView iv_image, iv_profilePic, iv_upvote;
    TextView tv_title, tv_upvote, tv_date, tv_author;
    RecyclerView rv_tags;
    CardView layout;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_image = itemView.findViewById(R.id.iv_images);
        iv_profilePic = itemView.findViewById(R.id.iv_profilePic);
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_author = itemView.findViewById(R.id.tv_author);
        tv_date = itemView.findViewById(R.id.tv_dateTime);
        tv_upvote = itemView.findViewById(R.id.tv_upvote);
        iv_upvote = itemView.findViewById(R.id.iv_upvote);
        rv_tags = itemView.findViewById(R.id.rv_tags);
        layout = itemView.findViewById(R.id.posts_row);
    }
}

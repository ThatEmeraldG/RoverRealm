package com.example.mp_finalproject;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder{

    ImageView iv_image, iv_profilePic;
    TextView tv_title, tv_upvote;
    CardView layout;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_image = itemView.findViewById(R.id.iv_images);
        iv_profilePic = itemView.findViewById(R.id.iv_profilePic);
        tv_title = itemView.findViewById(R.id.tv_title);
        tv_upvote = itemView.findViewById(R.id.tv_upvote);
        layout = itemView.findViewById(R.id.posts_row);
    }
}

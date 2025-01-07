package com.example.mp_finalproject;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_finalproject.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    Context context;
    List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_view, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        String postUpvotes = postList.get(position).getUpvote() + " upvotes";

        holder.tv_title.setText(postList.get(position).getTitle());
        holder.tv_upvote.setText(postUpvotes);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("title", postList.get(pos).getTitle());
                intent.putExtra("upvote", postList.get(pos).getUpvote());
//                intent.putExtra("image", postList.get(pos).getImage());
                intent.putExtra("description", postList.get(pos).getDescription());
                intent.putExtra("date", postList.get(pos).getDate());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

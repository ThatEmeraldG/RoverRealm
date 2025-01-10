package com.example.mp_finalproject;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_finalproject.model.Post;
import com.example.mp_finalproject.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        String authorId = postList.get(position).getAuthorId();
        String title = postList.get(position).getTitle();
        Date date = postList.get(position).getDate();
        int upvote = postList.get(position).getUpvote();

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm:ss z", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        holder.tv_title.setText(title);
        holder.tv_upvote.setText(String.valueOf(upvote));
        holder.tv_date.setText(formattedDate);
        fetchAuthorName(authorId, new AuthorNameCallback() {
            @Override
            public void onCallback(String authorName) {
                holder.tv_author.setText(authorName);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("post_id", postList.get(pos).getPostId());
                intent.putExtra("title", postList.get(pos).getTitle());
                intent.putExtra("description", postList.get(pos).getDescription());
                intent.putExtra("author_name", postList.get(pos).getAuthorId());
                intent.putExtra("date", postList.get(pos).getDate());
                intent.putExtra("upvote", postList.get(pos).getUpvote());
//                intent.putExtra("image", postList.get(pos).getImage());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return postList.size();
    }

    public void setPosts(List<Post> posts) {
        this.postList = posts;
    }

    private void fetchAuthorName(String authorId, AuthorNameCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(authorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onCallback(user.getUsername());
                        } else {
                            callback.onCallback("Unknown Author");
                        }
                    } else {
                        callback.onCallback("Unknown Author");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching author name", e);
                    callback.onCallback("Error, unknown author");
                });
    }
}

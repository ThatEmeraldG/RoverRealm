package com.example.mp_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mp_finalproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {
    // UI Components
    private ImageView backBtn;
    private TextView tv_author, tv_title, tv_upvote, tv_dateTime, tv_description;
    private LinearLayout btn_upvote;
    private ImageView iv_upvote;

    // Firestore
    private FirebaseFirestore db;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_details);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        backBtn = findViewById(R.id.btn_back);
        btn_upvote = findViewById(R.id.btn_upvote);
        tv_author = findViewById(R.id.tv_author);
        tv_title = findViewById(R.id.tv_title);
        tv_upvote = findViewById(R.id.tv_upvote);
        tv_dateTime = findViewById(R.id.tv_dateTime);
        tv_description = findViewById(R.id.tv_description);
        iv_upvote = findViewById(R.id.iv_upvote);

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Get intent data
        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("post_id");
            String description = intent.getStringExtra("description");
            String authorId = intent.getStringExtra("author");
            String title = intent.getStringExtra("title");
            int upvote = intent.getIntExtra("upvote", 0);
            Date date = (Date) intent.getSerializableExtra("date");

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm:ss Z", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            // Set data to UI
            tv_title.setText(title);
            tv_upvote.setText(String.valueOf(upvote));
            tv_dateTime.setText(formattedDate);
            tv_description.setText(description);
            fetchAuthorName(authorId, new AuthorNameCallback() {
                @Override
                public void onCallback(String authorName) {
                    tv_author.setText(authorName);
                }
            });

            // Check if post is already liked
            checkIfLiked();
        }

        // Back button functionality
        backBtn.setOnClickListener(v -> finish());

        // Upvote button functionality
        btn_upvote.setOnClickListener(v -> updateUpvote());
    }

    private void checkIfLiked() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> upvotedPosts = (List<String>) documentSnapshot.get("upvotedPostId");
                        boolean isLiked = upvotedPosts != null && upvotedPosts.contains(postId);

                        // Update like button based on the liked status
                        updateLikeButton(isLiked);
                    } else {
                        Log.w("Firestore", "User document not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user data", e);
                });
    }



    private void updateUpvote() {
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Post ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the user has already upvoted the post
        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> upvotedPosts = (List<String>) documentSnapshot.get("upvotedPostId");
                        boolean alreadyUpvoted = upvotedPosts != null && upvotedPosts.contains(postId);

                        // Get current upvote count
                        db.collection("Posts")
                                .document(postId)
                                .get()
                                .addOnSuccessListener(postSnapshot -> {
                                    if (postSnapshot.exists()) {
                                        Long currentUpvotes = postSnapshot.getLong("upvote");
                                        if (currentUpvotes == null) currentUpvotes = 0L;

                                        Long newUpvotes;
                                        FieldValue updateAction;

                                        if (alreadyUpvoted) {
                                            // User already upvoted, cancel upvote
                                            newUpvotes = currentUpvotes - 1;
                                            updateAction = FieldValue.arrayRemove(postId);
                                            updateLikeButton(false);
                                        } else {
                                            // User has not upvoted, add upvote
                                            newUpvotes = currentUpvotes + 1;
                                            updateAction = FieldValue.arrayUnion(postId);
                                            updateLikeButton(true);
                                        }

                                        // Update post upvotes
                                        db.collection("Posts")
                                                .document(postId)
                                                .update("upvote", newUpvotes)
                                                .addOnSuccessListener(unused -> {
                                                    // Update user's upvoted posts
                                                    db.collection("Users")
                                                            .document(userId)
                                                            .update("upvotedPostId", updateAction)
                                                            .addOnSuccessListener(unused2 -> {
                                                                // Update UI
                                                                tv_upvote.setText(String.valueOf(newUpvotes));
                                                                String message = alreadyUpvoted ? "Upvote removed!" : "Upvoted!";
                                                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("FirestoreError", "Error updating user's upvoted posts", e);
                                                                Toast.makeText(this, "Error updating user data", Toast.LENGTH_SHORT).show();
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("FirestoreError", "Error updating post upvotes", e);
                                                    Toast.makeText(this, "Error updating post upvotes", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Error fetching post", e);
                                    Toast.makeText(this, "Error fetching post details", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user data", e);
                    Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                });
    }
    private void updateLikeButton(boolean isUpvoted) {
        if (isUpvoted) {
            iv_upvote.setImageResource(R.drawable.thumb_up_fill_24px);
        } else {
            iv_upvote.setImageResource(R.drawable.thumb_up_24px);
        }
    }

    private void fetchAuthorName(String authorId, AuthorNameCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(authorId)
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

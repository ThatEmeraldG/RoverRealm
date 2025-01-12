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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mp_finalproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm '|' zzz", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            tv_title.setText(title);
            tv_upvote.setText(String.valueOf(upvote));
            tv_dateTime.setText(formattedDate);
            tv_description.setText(description);
            fetchAuthorName(authorId, new UserCallback() {
                @Override
                public void onCallback(String authorName) {
                    tv_author.setText(authorName);
                }
            });

            // Check if post is already liked
            checkIfLiked();
        }

        backBtn.setOnClickListener(v -> finish());

        btn_upvote.setOnClickListener(v -> updateUpvote());
    }

    private void checkIfLiked() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> upvotedPosts = (List<String>) documentSnapshot.get("upvotedPostId");
                        boolean isLiked = upvotedPosts != null && upvotedPosts.contains(postId);

                        // Update like button based on the liked status
                        updateUpvoteButton(isLiked);
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
            Log.w("Upvote", "Post ID is missing."); // Use Log.w for warnings
            Toast.makeText(this, "Post ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.w("Upvote", "User is not logged in.");
            Toast.makeText(this, "You must be logged in to upvote.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        DocumentReference userRef = db.collection("Users").document(userId);
        DocumentReference postRef = db.collection("Posts").document(postId);

        userRef.get().addOnSuccessListener(userSnapshot -> {
            if (!userSnapshot.exists()) {
                Log.w("Upvote", "User document does not exist for ID: " + userId);
                Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> upvotedPosts = (List<String>) userSnapshot.get("upvotedPostId");
            if (upvotedPosts == null) {
                upvotedPosts = new ArrayList<>();
            }

            boolean alreadyUpvoted = upvotedPosts.contains(postId);

            postRef.get().addOnSuccessListener(postSnapshot -> {
                if (!postSnapshot.exists()) {
                    Log.w("Upvote", "Post document does not exist for ID: " + postId);
                    Toast.makeText(this, "Post not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                WriteBatch batch = db.batch();

                if (alreadyUpvoted) {
                    batch.update(postRef, "upvote", FieldValue.increment(-1));
                    batch.update(userRef, "upvotedPostId", FieldValue.arrayRemove(postId));
                    updateUpvoteButton(false);
                } else {
                    batch.update(postRef, "upvote", FieldValue.increment(1));
                    batch.update(userRef, "upvotedPostId", FieldValue.arrayUnion(postId));
                    updateUpvoteButton(true);
                }

                batch.commit().addOnSuccessListener(v -> {
                            postRef.get().addOnSuccessListener(updatedPostSnapshot -> {
                                Long updatedUpvotes = updatedPostSnapshot.getLong("upvote");
                                if(updatedUpvotes == null) updatedUpvotes = 0L;
                                if (tv_upvote != null) { // Check if tv_upvote is not null
                                    tv_upvote.setText(String.valueOf(updatedUpvotes));
                                    String message = alreadyUpvoted ? "Upvote removed!" : "Upvoted!";
                                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.w("Upvote", "tv_upvote is null. Cannot update UI.");
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("FirestoreError", "Error fetching updated post data for UI: ", e);
                                Toast.makeText(this, "Failed to update UI.", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreError", "Batch commit failed: ", e);
                            Toast.makeText(this, "Failed to update upvote.", Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(e -> {
                Log.e("FirestoreError", "Error fetching post data", e);
                Toast.makeText(this, "Error fetching post details", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e("FirestoreError", "Error fetching user data", e);
            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUpvoteButton(boolean isUpvoted) {
        if (isUpvoted) {
            iv_upvote.setImageResource(R.drawable.thumb_up_fill_24px);
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                iv_upvote.setImageTintList(null);
            }
        } else {
            iv_upvote.setImageResource(R.drawable.thumb_up_24px);
        }
    }

    private void fetchAuthorName(String authorId, UserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(authorId);

        userRef.get()
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

package com.example.mp_finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mp_finalproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Field;
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
    private ImageView iv_postMenu;

    // Firestore
    private FirebaseFirestore db;
    private FirebaseUser user;
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
        iv_postMenu = findViewById(R.id.post_menu);

        // Firestore
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get intent data
        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("post_id");
            String authorId = intent.getStringExtra("author");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
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

            if(authorId.equals(user.getUid())){
                iv_postMenu.setVisibility(View.VISIBLE);
                iv_postMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu optionsMenu = new PopupMenu(PostDetailsActivity.this, v);
                        getMenuInflater().inflate(R.menu.post_menu, optionsMenu.getMenu());

                        try {
                            Field f = optionsMenu.getClass().getDeclaredField("mPopup");
                            f.setAccessible(true);
                            Object menuPopup = f.get(optionsMenu);
                            assert menuPopup != null;
                            menuPopup.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menuPopup, true);
                        } catch (Exception e) {
                            // Handle error if reflection fails
                            Log.e("PopupMenu", "Error forcing icons to show", e);
                        }

                        // Handle post menu (3-dots)
                        optionsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int itemId = item.getItemId();
                                if (itemId == R.id.opt_editPost) {
                                    Intent intent = new Intent(PostDetailsActivity.this, EditPostActivity.class);
                                    intent.putExtra("post_id", postId);
                                    intent.putExtra("author_id", authorId);
                                    intent.putExtra("title", title);
                                    intent.putExtra("description", description);

                                    startActivity(intent);
                                    return true;
                                } else if (itemId == R.id.opt_deletePost) {
                                    deletePost(postId);
                                    return true;
                                }
                                return false;
                            }
                        });

                        optionsMenu.show();
                    }
                });
            }
        }

        backBtn.setOnClickListener(v -> finish());

        btn_upvote.setOnClickListener(v -> updateUpvote());
    }

    private void deletePost(String postId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post Confirmation")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    DocumentReference postRef = db.collection("Posts").document(postId);

                    // delete from Users collection, from field upVotedPostId if any
                    db.collection("Users")
                            .whereArrayContains("upvotedPostId", postId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                WriteBatch batch = db.batch();

                                batch.delete(postRef);

                                // Remove postId from upvotedPostId in each user's document
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    DocumentReference userRef = document.getReference();
                                    batch.update(userRef, "upvotedPostId", FieldValue.arrayRemove(postId));
                                }

                                batch.commit()
                                        .addOnSuccessListener(v -> {
                                            Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error deleting post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("DeletePost", "Error committing batch delete", e);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error finding upvoters: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("DeletePost", "Error finding upvoters", e);
                            });
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    Toast.makeText(this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                }) // Do nothing if "No"
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void checkIfLiked() {
        String userId = user.getUid();
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

        if (user == null) {
            Log.w("Upvote", "User is not logged in.");
            Toast.makeText(this, "You must be logged in to upvote.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

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
                                if (tv_upvote != null) {
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

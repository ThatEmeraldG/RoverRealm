package com.example.mp_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {
    // UI Components
    private ImageView backBtn;
    private TextView tv_author, tv_title, tv_upvote, tv_dateTime, tv_description;
    private LinearLayout btn_upvote;

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

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Get intent data
        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("post_id");
            String description = intent.getStringExtra("description");
            String author = intent.getStringExtra("author");
            String title = intent.getStringExtra("title");
            int upvote = intent.getIntExtra("upvote", 0);
            Date date = (Date) intent.getSerializableExtra("date");

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            // Set data to UI
            tv_author.setText(author);
            tv_title.setText(title);
            tv_upvote.setText(String.valueOf(upvote));
            tv_dateTime.setText(formattedDate);
            tv_description.setText(description);
        }

        // Back button functionality
        backBtn.setOnClickListener(v -> finish());

        // Upvote button functionality
        btn_upvote.setOnClickListener(v -> updateUpvote());
    }

    private void updateUpvote() {
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Post ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current upvote count
        db.collection("Posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentUpvotes = documentSnapshot.getLong("upvote");
                        if (currentUpvotes == null) currentUpvotes = 0L;

                        // Increment upvote count
                        Long newUpvotes = currentUpvotes + 1;

                        // Update Firestore
                        db.collection("Posts")
                                .document(postId)
                                .update("upvote", newUpvotes)
                                .addOnSuccessListener(unused -> {
                                    // Update UI
                                    tv_upvote.setText(String.valueOf(newUpvotes));
                                    Toast.makeText(this, "Upvoted!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Error updating upvote", e);
                                    Toast.makeText(this, "Error updating upvote", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching post", e);
                    Toast.makeText(this, "Error fetching post details", Toast.LENGTH_SHORT).show();
                });
    }
}

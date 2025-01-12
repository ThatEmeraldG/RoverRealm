package com.example.mp_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private EditText etPostTitle, etPostDescription;
    private Button saveBtn;
    private ImageView backBtn;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String authorId = intent.getStringExtra("author_id");
        String postTitle = intent.getStringExtra("title");
        String postDescription = intent.getStringExtra("description");
        postId = intent.getStringExtra("post_id");

        etPostTitle = findViewById(R.id.et_postTitle);
        etPostDescription = findViewById(R.id.et_postDescription);
        backBtn = findViewById(R.id.btn_back);
        saveBtn = findViewById(R.id.btn_save);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        etPostTitle.setText(postTitle);
        etPostDescription.setText(postDescription);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = etPostTitle.getText().toString().trim();
                String newDescription = etPostDescription.getText().toString().trim();

                if(validatePost(authorId, newTitle, newDescription)){
                    updatePost(newTitle, newDescription);
                }
            }
        });
    }

    private void updatePost(String title, String description) {
        Map<String, Object> postUpdates = new HashMap<>();
        postUpdates.put("title", title);
        postUpdates.put("description", description);

        DocumentReference postRef = db.collection("Posts").document(postId);
        postRef.update(postUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Log.d("FirestoreSuccess", "Post updated successfully");
                        Toast.makeText(EditPostActivity.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error updating post", e);
                    Toast.makeText(this, "Error updating post", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validatePost(String authorId, String title, String description){
        String currentUser = user.getUid();
        if(!currentUser.equals(authorId)){
            Toast.makeText(this, "You are not this post's author", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(title)){
            etPostTitle.setError("Post title cannot be empty!");
            etPostTitle.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(description)){
            etPostDescription.setError("Post description cannot be empty!");
            etPostDescription.requestFocus();
            return false;
        }
        return true;
    }
}
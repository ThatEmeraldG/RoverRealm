package com.example.mp_finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mp_finalproject.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private ImageView backBtn;
    private Button submitBtn;
    private List<String> tags;
    private Date date;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser authUser;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        backBtn = findViewById(R.id.btn_back);
        submitBtn = findViewById(R.id.btn_submit);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        authUser = auth.getCurrentUser();
        authorId = authUser.getUid();
        tags = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                date = new Date();

                if (validatePost(title, description)) {
                    createPost(title, description);
                }
            }
        });
    }

    private void createPost(String title, String description) {
        String postID = db.collection("Posts").document().getId();
        Post post = new Post(postID, title, description, authorId, "", 0, date, tags);
        db.collection("Posts").document(postID).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreatePostActivity.this, "Post created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreatePostActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validatePost(String title, String description){
        if(TextUtils.isEmpty(title)){
            etTitle.setError("Post title cannot be empty!");
            etTitle.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(description)){
            etDescription.setError("Post description cannot be empty!");
            etDescription.requestFocus();
            return false;
        }
        return true;
    }

}
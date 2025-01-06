package com.example.mp_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private Button submitBtn;
    private List<String> tags;
    private Date date;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
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
        etDescription =findViewById(R.id.et_description);
        submitBtn = findViewById(R.id.btn_submit);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        authorId = user.getUid();
        tags = new ArrayList<>();

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                date = new Date();

                createPost(title, description);
            }


        });
    }

    private void createPost(String title, String description) {
        Post post = new Post("1", title, description, authorId, "", 0, date, tags);
        db.collection("posts").add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CreatePostActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreatePostActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
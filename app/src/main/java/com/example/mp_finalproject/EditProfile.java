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
import androidx.lifecycle.ViewModelProvider;

import com.example.mp_finalproject.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button saveBtn;
    private ImageView backBtn;
    private FirebaseFirestore db;
    private String userId;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        userId = intent.getStringExtra("user_id");

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        backBtn = findViewById(R.id.btn_back);
        saveBtn = findViewById(R.id.btn_save);
        db = FirebaseFirestore.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etUsername.setText(username);
        etEmail.setText(email);
        etPassword.setText(password);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = etUsername.getText().toString().trim();
                String txt_email = etEmail.getText().toString().trim();
                String txt_password = etPassword.getText().toString().trim();

                if(validateUser(txt_username, txt_email, txt_password)){
                    updateProfile(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    private void updateProfile(String username, String email, String password) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("email", email);
        userUpdates.put("password", password);

        db.collection("Users")
                .document(userId)
                .update(userUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FirestoreSuccess", "User profile updated successfully");
                        Toast.makeText(EditProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error updating user profile", e);
                    Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateUser(String username, String email, String password){
        if(TextUtils.isEmpty(username)){
            etUsername.setError("Username cannot be empty!");
            etUsername.requestFocus();
            return false;
        } else if (username.length() > 20){
            etUsername.setError("Username cannot exceed 20 characters!");
            etUsername.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)){
            etEmail.setError("Email cannot be empty!");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)){
            etPassword.setError("Password cannot be empty!");
            etPassword.requestFocus();
            return false;
        } else if (password.length() < 6){
            etPassword.setError("Password needs to be at least 6 characters long!");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
}
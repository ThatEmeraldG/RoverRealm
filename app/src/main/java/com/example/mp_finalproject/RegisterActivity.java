package com.example.mp_finalproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mp_finalproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button submitBtn;
    private TextView loginRedirect;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirmPassword);
        loginRedirect = findViewById(R.id.loginRedirect);
        submitBtn = findViewById(R.id.btn_submit);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = new ProgressBar(RegisterActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.addView(progressBar, params);

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = etUsername.getText().toString().trim();
                String txt_email = etEmail.getText().toString().trim();
                String txt_password = etPassword.getText().toString().trim();
                String txt_confirmPassword = etConfirmPassword.getText().toString().trim();

                if(validateRegister(txt_username, txt_email, txt_password, txt_confirmPassword)){
                    registerUser(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    private void registerUser(String username, String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if(task.isSuccessful()){
                    String userId = auth.getCurrentUser().getUid();
                    User user = new User(userId, username, email, password, "");

                    db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateRegister(String username, String email, String password, String confirmPassword){
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
        if (TextUtils.isEmpty(confirmPassword)){
            etConfirmPassword.setError("Confirm Password cannot be empty!");
            etConfirmPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)){
            etConfirmPassword.setError("Password do not match!");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }
}
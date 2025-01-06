package com.example.mp_finalproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button submitBtn;
    private TextView loginRedirect;
    private ImageView ivLogo;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

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
        ivLogo = findViewById(R.id.iv_logo);
        submitBtn = findViewById(R.id.btn_submit);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
                    db.collection("Users").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Values added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    registerUser(txt_email, txt_password);
                }
            }
        });
    }

    private void registerUser(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
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
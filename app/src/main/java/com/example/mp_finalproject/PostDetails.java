package com.example.mp_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {
    // Initialize Variable
    ImageView imageView;
    TextView tv_title;
    TextView tv_description;
    TextView tv_upvote;
    TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Button backBtn = findViewById(R.id.btn_back);
        if(intent == null){
            imageView = findViewById(R.id.imageview);
            tv_title = findViewById(R.id.tv_title);
            tv_description = findViewById(R.id.tv_description);
            tv_upvote = findViewById(R.id.tv_upvote);
            tv_date = findViewById(R.id.tv_date);

            int image = intent.getIntExtra("image", -1);
            String name = intent.getStringExtra("title");
            int upvote = intent.getIntExtra("upvote", -1);
            String synopsis = intent.getStringExtra("description");
            Date date = (Date) intent.getSerializableExtra("date");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            if (image != -1) {
                imageView.setImageResource(image);
            }
            tv_title.setText(name);
            tv_description.setText(synopsis);
            tv_upvote.setText(String.valueOf(upvote));
            tv_date.setText(formattedDate);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
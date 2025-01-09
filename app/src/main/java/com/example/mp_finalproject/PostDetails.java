package com.example.mp_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private ImageView iv_image, backBtn;
    private TextView tv_author, tv_title, tv_upvote, tv_dateTime;

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
        backBtn = findViewById(R.id.btn_back);

        if(intent == null){
//            iv_image = findViewById(R.id.iv_images);
            tv_author = findViewById(R.id.tv_author);
            tv_title = findViewById(R.id.tv_title);
            tv_upvote = findViewById(R.id.tv_upvote);
            tv_dateTime = findViewById(R.id.tv_dateTime);

//            int image = intent.getIntExtra("image", -1);
            String author = intent.getStringExtra("author");
            String title = intent.getStringExtra("title");
            int upvote = intent.getIntExtra("upvote", 0);
            Date date = (Date) intent.getSerializableExtra("date");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

//            if (image != -1) {
//                iv_image.setImageResource(image);
//            }
            tv_author.setText(author);
            tv_title.setText(title);
            tv_upvote.setText(String.valueOf(upvote));
            tv_dateTime.setText(formattedDate);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
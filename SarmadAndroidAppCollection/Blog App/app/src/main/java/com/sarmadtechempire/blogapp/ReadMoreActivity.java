package com.sarmadtechempire.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.databinding.ActivityMainBinding;
import com.sarmadtechempire.blogapp.databinding.ActivityReadMoreBinding;

public class ReadMoreActivity extends AppCompatActivity {

    private ActivityReadMoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityReadMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadMoreActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        BlogItemModel blogs = getIntent().getParcelableExtra("blogItems");

        if(blogs != null)
        {
           // Retrive User related Blog data here example blog title e.t.c
            binding.mainTitteHeadingTv.setText(blogs.getHeading());
            binding.bloggerNameTv.setText(blogs.getUserName());
            binding.dateTv.setText(blogs.getDate());
            binding.fullBlogDescriptionTv.setText(Html.fromHtml(blogs.getPost(),Html.FROM_HTML_MODE_LEGACY));

            String userImageUrl = blogs.getImageUrl();
            Glide.with(this)
                    .load(userImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileIv);
        }
        else {
            Toast.makeText(ReadMoreActivity.this,"Failed to load Blogs",Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onBackPressed() {
        // Finish the activity to return to MainActivity
        super.onBackPressed();
        finish();
    }
}
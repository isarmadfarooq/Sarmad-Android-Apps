package com.sarmadtechempire.blogapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.adapter.BlogAdapter;
import com.sarmadtechempire.blogapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseReference databaseReference;
    private List<BlogItemModel> blogItemModel;
    private List<BlogItemModel> filteredBlogItemModel;
    private BlogAdapter blogAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize lists
        blogItemModel = new ArrayList<>();
        filteredBlogItemModel = new ArrayList<>();

        // to go save blog activity page
        binding.saveArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SaveBlogActivity.class);
                startActivity(intent);
            }
        });

        // to go to profile activity page
        binding.userProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("blogs");

        String userId = auth.getCurrentUser().getUid();

        if (userId != null) {
            loadUserProfileImage(userId);
        }

        RecyclerView recyclerView = binding.blogRv;
        blogAdapter = new BlogAdapter(this, filteredBlogItemModel);
        recyclerView.setAdapter(blogAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) {
                    return; // Prevent further processing if the activity is destroyed
                }

                blogItemModel.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BlogItemModel blogItem = dataSnapshot.getValue(BlogItemModel.class);
                    if (blogItem != null) {
                        blogItemModel.add(blogItem);
                    }
                }
                filterBlogs(""); // Reset filter to display all items initially
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Blog loading failed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addArticleFloatActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddArticleActivity.class);
                intent.putExtra("origin", "main");
                startActivity(intent);
            }
        });


// Set up the SearchView
        binding.searchBlog.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBlogs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBlogs(newText);
                return false;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void loadUserProfileImage(String userId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("users").child(userId);

        userReference.child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) {
                    return; // Prevent further processing if the activity is destroyed
                }

                String profileImageUrl = snapshot.getValue(String.class);
                if (profileImageUrl != null) {
                    Glide.with(MainActivity.this)
                            .load(profileImageUrl)
                            .into(binding.profileIv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load user profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBlogs(String query) {
        filteredBlogItemModel.clear();
        if (query.isEmpty()) {
            filteredBlogItemModel.addAll(blogItemModel);
        } else {
            for (BlogItemModel blogItem : blogItemModel) {
                if (blogItem.getHeading().toLowerCase().contains(query.toLowerCase()) ||
                        blogItem.getPost().toLowerCase().contains(query.toLowerCase())) {
                    filteredBlogItemModel.add(blogItem);
                }
            }
        }
        blogAdapter.notifyDataSetChanged();
    }
}

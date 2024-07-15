package com.sarmadtechempire.blogapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Initialize view binding for the main activity layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("blogs");
        blogItemModel = new ArrayList<>(); // Initialize the list

        String userId = auth.getCurrentUser().getUid();

        // Set UserProfileImage
        if (userId != null) {
            loadUserProfileImage(userId);
        }

        // Initialize the recycler view and set adapter
        RecyclerView recyclerView = binding.blogRv;
        BlogAdapter blogAdapter = new BlogAdapter(this, blogItemModel);
        recyclerView.setAdapter(blogAdapter);

        // Fetch data from Firebase Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blogItemModel.clear(); // Clear the list before adding new data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BlogItemModel blogItem = dataSnapshot.getValue(BlogItemModel.class);
                    if (blogItem != null) {
                        blogItemModel.add(blogItem);
                    }
                }
                // reverse the list
                Collections.reverse(blogItemModel);
                // Notify the adapter that data has changed
                blogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Blog loading failed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addArticleFloatActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addArticleIntent = new Intent(MainActivity.this, AddArticleActivity.class);
                startActivity(addArticleIntent);
                finish();
            }
        });

        // Handle window insets for the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserProfileImage(String userId) {
        // Load user profile image from Firebase storage or database
        DatabaseReference userReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("users").child(userId);

        userReference.child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageUrl = snapshot.getValue(String.class);
                if (profileImageUrl != null) {
                    Glide.with(MainActivity.this)
                            .load(profileImageUrl)
                            .into(binding.profileIv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
                Toast.makeText(MainActivity.this, "Failed to load user profile image", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

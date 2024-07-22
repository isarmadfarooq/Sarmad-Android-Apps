package com.sarmadtechempire.blogapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.adapter.BlogAdapter;
import com.sarmadtechempire.blogapp.databinding.ActivitySaveBlogBinding;

import java.util.ArrayList;
import java.util.List;

public class SaveBlogActivity extends ComponentActivity {

    private ActivitySaveBlogBinding binding;
    private final List<BlogItemModel> savedBlogArticles = new ArrayList<>();
    private BlogAdapter blogAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySaveBlogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        blogAdapter = new BlogAdapter(this, savedBlogArticles);

        RecyclerView recyclerView = binding.savedBlogsRv;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Set LayoutManager
        recyclerView.setAdapter(blogAdapter);

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users").child(userId).child("saveBlogPosts");

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    savedBlogArticles.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String postId = dataSnapshot.getKey();
                        Boolean isSaved = dataSnapshot.getValue(Boolean.class);

                        if (postId != null && isSaved != null && isSaved) {
                            Log.d("SaveBlogActivity", "Fetching blog for postId: " + postId);
                            fetchBlogItemById(postId, blogItemModel -> {
                                if (blogItemModel != null) {
                                    Log.d("SaveBlogActivity", "BlogItemModel fetched: " + blogItemModel.getHeading());
                                    savedBlogArticles.add(blogItemModel);
                                    blogAdapter.notifyDataSetChanged();  // Notify adapter of data change
                                } else {
                                    Log.d("SaveBlogActivity", "BlogItemModel is null for postId: " + postId);
                                }
                            });
                        } else {
                            Log.d("SaveBlogActivity", "PostId or isSaved is null or false");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SaveBlogActivity", "Failed to fetch saved blogs", error.toException());
                }
            });
        }

        binding.backIconIv.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchBlogItemById(String postId, OnFetchBlogItemListener listener) {
        DatabaseReference blogReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("blogs").child(postId);

        blogReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BlogItemModel blogItemModel = dataSnapshot.getValue(BlogItemModel.class);
                Log.d("SaveBlogActivity", "Fetched BlogItem: " + blogItemModel);
                listener.onFetchBlogItem(blogItemModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SaveBlogActivity", "Failed to fetch blog item", error.toException());
            }
        });
    }

    interface OnFetchBlogItemListener {
        void onFetchBlogItem(BlogItemModel blogItemModel);
    }
}

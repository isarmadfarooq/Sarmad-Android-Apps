package com.sarmadtechempire.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.adapter.ArticleAdapter;
import com.sarmadtechempire.blogapp.databinding.ActivityArticlesBinding;

import java.util.ArrayList;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {

    private ActivityArticlesBinding binding;
    private DatabaseReference databaseReference;
    private List<BlogItemModel> blogSaveList;
    private FirebaseAuth auth;
    private ArticleAdapter articleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the binding
        binding = ActivityArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();

        blogSaveList = new ArrayList<>();

        // get current user Id
        String currentUserId = auth.getCurrentUser().getUid();

        articleAdapter = new ArticleAdapter(this, blogSaveList, new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(BlogItemModel blogItem) {

            }

            @Override
            public void onReadMoreClick(BlogItemModel blogItem) {

                Intent intent = new Intent(ArticlesActivity.this, ReadMoreActivity.class);
                intent.putExtra("blogItems", blogItem);
                startActivity(intent);

            }

            @Override
            public void onDeleteClick(BlogItemModel blogItem) {
                showDeleteConfirmationDialog(blogItem);

            }
        });
        RecyclerView recyclerView = binding.blogRv;
        recyclerView.setAdapter(articleAdapter);

        databaseReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("blogs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) {
                    return; // Prevent further processing if the activity is destroyed
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BlogItemModel blogList = dataSnapshot.getValue(BlogItemModel.class);
                    if (blogList != null && blogList.getUserId().equals(currentUserId)) {
                        blogSaveList.add(blogList);
                    }
                }
                articleAdapter.setData(blogSaveList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ArticlesActivity.this, "Blog loading failed", Toast.LENGTH_SHORT).show();

            }
        });

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDeleteConfirmationDialog(BlogItemModel blogItem) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this blog post?")
                .setPositiveButton("Yes", (dialog, which) -> deleteBlogPost(blogItem))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteBlogPost(BlogItemModel blogItem) {
        String postId = blogItem.getPostId();
        DatabaseReference blogPostReference = databaseReference.child(postId);
        blogPostReference.removeValue().addOnSuccessListener(unused -> {
            Toast.makeText(ArticlesActivity.this, "Blog deleted successfully", Toast.LENGTH_SHORT).show();
            blogSaveList.remove(blogItem);
            articleAdapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ArticlesActivity.this, "Blog deletion failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }



package com.sarmadtechempire.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.Model.UserData;
import com.sarmadtechempire.blogapp.databinding.ActivityAddArticleBinding;
import com.sarmadtechempire.blogapp.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddArticleActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app");

    DatabaseReference databaseReference = database.getReference("blogs");
    DatabaseReference userReference = database.getReference("users");
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private ActivityAddArticleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = binding.blogTitleTextIL.getEditText().getText().toString().trim();
                String description = binding.blogDescriptionTextIl.getEditText().getText().toString().trim();

                if(title.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(AddArticleActivity.this, "Please Fill All The fields", Toast.LENGTH_SHORT).show();
                }

                //get current user

                FirebaseUser user = auth.getCurrentUser();

                if(user!=null)
                {
                    String userId = user.getUid();
                    String userName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";
                    String userImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                    //fetch userName and userProfile from database

                    userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserData userData = snapshot.getValue(UserData.class);

                            if(userData!=null)
                            {
                                String userNameFromDb = userData.getName();
                                String userImageUrlFromDb = userData.getProfileImage();

                                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                                // create a blogItemModel
                                BlogItemModel blogItem  = new BlogItemModel(
                                        title,
                                        userNameFromDb != null ? userNameFromDb : "",
                                        currentDate,
                                        description,
                                        0, // likeCount initialized to 0
                                        userImageUrlFromDb != null ? userImageUrlFromDb : ""
                                );

                                //create a unique key for blog post
                                String key = databaseReference.push().getKey();

                                if(key!=null)
                                {
                                    DatabaseReference blogReference = databaseReference.child(key);
                                    blogReference.setValue(blogItem).addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                finish();
                                            }

                                            else {
                                                // Operation failed
                                                Exception e = task.getException();
                                                if (e != null) {
                                                    Log.e("TAG", "Error writing blog to database: " + e.getMessage());
                                                    Toast.makeText(AddArticleActivity.this, "Failed to save blog", Toast.LENGTH_SHORT).show();
                                                    // Handle failure scenario as needed
                                                }
                                            }

                                        }
                                    });

                                }


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }
                    );
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
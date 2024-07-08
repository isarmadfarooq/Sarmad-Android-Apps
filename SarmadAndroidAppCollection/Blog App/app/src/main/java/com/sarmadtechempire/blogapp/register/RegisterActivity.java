package com.sarmadtechempire.blogapp.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sarmadtechempire.blogapp.Model.UserData;
import com.sarmadtechempire.blogapp.R;
import com.sarmadtechempire.blogapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri = null;
    private ActivityRegisterBinding binding;

    private static final int REGISTRATION_TIMEOUT = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing View Binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app");
        storage = FirebaseStorage.getInstance();


        setupUI();
    }

    private void setupUI() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignIn();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        binding.userProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImage();
            }
        });
    }

    private void navigateToSignIn() {
        Intent signIn = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(signIn);
        finish();
        Toast.makeText(RegisterActivity.this, "Welcome to login screen", Toast.LENGTH_SHORT).show();
    }

    private void registerUser() {
        String name = binding.registerNameEt.getText().toString();
        String email = binding.registerMailEt.getText().toString();
        String password = binding.registerPasswordEt.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please Fill All The Details", Toast.LENGTH_SHORT).show();
        } else if (!isConnectedToInternet()) {
            Toast.makeText(RegisterActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            binding.circularProgressBar.setVisibility(View.VISIBLE); // Show progress bar

            // Handler for low internet speed
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (binding.circularProgressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(RegisterActivity.this, "Low Internet Speed, It may take time to register", Toast.LENGTH_SHORT).show();
                }
            }, REGISTRATION_TIMEOUT);

            long startTime = System.currentTimeMillis(); // Log start time
            Log.d("Registration", "Registration started at: " + startTime);

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            long endTime = System.currentTimeMillis(); // Log end time
                            Log.d("Registration", "Registration completed at: " + endTime);
                            Log.d("Registration", "Total time taken: " + (endTime - startTime) + " ms");

                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar

                            if (task.isSuccessful()) {
                                handleRegistrationSuccess();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    private void handleRegistrationSuccess() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = database.getReference("users");
            String userId = user.getUid();
            UserData userData = new UserData(binding.registerNameEt.getText().toString(), binding.registerMailEt.getText().toString(), null);

            binding.circularProgressBar.setVisibility(View.VISIBLE); // Show progress bar
            long startTime = System.currentTimeMillis(); // Log start time
            Log.d("Registration", "Saving user data started at: " + startTime);

            databaseReference.child(userId).setValue(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            long endTime = System.currentTimeMillis(); // Log end time
                            Log.d("Registration", "User data saved at: " + endTime);
                            Log.d("Registration", "Time taken to save user data: " + (endTime - startTime) + " ms");

                            if (imageUri != null) {
                                uploadProfileImage(userId);
                            } else {
                                saveUserRegistrationStatus();
                                navigateToWelcome(); // Navigate to WelcomeActivity
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                            Log.e("TAG", "Error saving user data: " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadProfileImage(String userId) {
        if (imageUri != null) {
            StorageReference storageReference = storage.getReference().child("profile_image/" + userId + ".jpg");

            long startTime = System.currentTimeMillis(); // Log start time
            Log.d("Registration", "Profile image upload started at: " + startTime);

            storageReference.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        long endTime = System.currentTimeMillis(); // Log end time
                        Log.d("Registration", "Profile image uploaded at: " + endTime);
                        Log.d("Registration", "Time taken to upload profile image: " + (endTime - startTime) + " ms");

                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl()
                                    .addOnCompleteListener(uriTask -> {
                                        if (uriTask.isSuccessful()) {
                                            String imageUrl = uriTask.getResult().toString();
                                            DatabaseReference userReference = database.getReference("users");
                                            userReference.child(userId).child("profileImage").setValue(imageUrl)
                                                    .addOnCompleteListener(imageUploadTask -> {
                                                        if (imageUploadTask.isSuccessful()) {
                                                            Glide.with(RegisterActivity.this)
                                                                    .load(imageUri)
                                                                    .apply(RequestOptions.circleCropTransform())
                                                                    .into(binding.profileIv);
                                                            saveUserRegistrationStatus(); // Save registration status
                                                            navigateToWelcome(); // Navigate to WelcomeActivity
                                                        } else {
                                                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                                                            Toast.makeText(RegisterActivity.this, "Failed to save image URL", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                                            Toast.makeText(RegisterActivity.this, "Failed to get image download URL", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                            Toast.makeText(RegisterActivity.this, "Image Upload Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            saveUserRegistrationStatus(); // Save registration status
            navigateToWelcome(); // Navigate to WelcomeActivity
        }
    }

    private void saveUserRegistrationStatus() {
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_new_user", true); // Set the flag for a new user
        editor.apply();
    }

    private void navigateToWelcome() {
        Toast.makeText(RegisterActivity.this, "User Registered Successfully 😊", Toast.LENGTH_SHORT).show();
        binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileIv);
        }
    }
}

package com.sarmadtechempire.blogapp.register;

import static android.content.ContentValues.TAG;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarmadtechempire.blogapp.MainActivity;
import com.sarmadtechempire.blogapp.Model.UserData;
import com.sarmadtechempire.blogapp.R;
import com.sarmadtechempire.blogapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 20;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri = null;
    private ActivityRegisterBinding binding;

    private static final int REGISTRATION_TIMEOUT = 30000; // 30 seconds

    private SharedPreferences preferences;

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

        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

        binding.googleRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAuth();
            }
        });
    }

    private void googleAuth() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        binding.circularProgressBar.setVisibility(View.VISIBLE); // Show progress bar
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileIv);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.circularProgressBar.setVisibility(View.GONE);

                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                String profileImage = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
                                UserData userData = new UserData(name, email, profileImage);
                                DatabaseReference databaseReference = database.getReference("users");
                                String userId = user.getUid();
                                databaseReference.child(userId).setValue(userData);
                                // Set is_new_user flag
                                preferences.edit()
                                        .putBoolean("is_logged_in", true)
                                        .putBoolean("is_new_user", true)
                                        .apply();
                                saveUserLoginStatus();
                                navigateToMainScreen();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToMainScreen() {
            Intent mainScreen = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(mainScreen);
            finish();
            Toast.makeText(RegisterActivity.this, "Welcome to Main screen", Toast.LENGTH_SHORT).show();
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
            Log.d("Registration", "Data storage started at: " + startTime);

            databaseReference.child(userId).setValue(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            long endTime = System.currentTimeMillis(); // Log end time
                            Log.d("Registration", "Data storage completed at: " + endTime);
                            Log.d("Registration", "Total time taken for data storage: " + (endTime - startTime) + " ms");

                            if (task.isSuccessful()) {
                                saveUserFCMToken();
                                if (imageUri != null) {
                                    uploadProfileImage(userId, userData);
                                } else {
                                    navigateToWelcome();
                                    Toast.makeText(RegisterActivity.this, "Welcome to welcome screen", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                                Toast.makeText(RegisterActivity.this, "Data storage failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

  private void saveUserFCMToken() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("tokens");
                            tokensRef.child(userId).setValue(token)
                                    .addOnCompleteListener(tokenTask -> {
                                        if (tokenTask.isSuccessful()) {
                                            Log.d(TAG, "FCM token saved successfully");
                                        } else {
                                            Log.w(TAG, "Failed to save FCM token", tokenTask.getException());
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                        }
                    });
        }
    }

    private void uploadProfileImage(String userId, UserData userData) {
        StorageReference storageReference = storage.getReference().child("profileImages").child(userId);

        long startTime = System.currentTimeMillis(); // Log start time
        Log.d("Registration", "Image upload started at: " + startTime);

        storageReference.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    long endTime = System.currentTimeMillis(); // Log end time
                                    Log.d("Registration", "Image upload completed at: " + endTime);
                                    Log.d("Registration", "Total time taken for image upload: " + (endTime - startTime) + " ms");

                                    userData.setProfileImage(uri.toString());
                                    DatabaseReference databaseReference = database.getReference("users");
                                    databaseReference.child(userId).setValue(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                                                    if (task.isSuccessful()) {
                                                        navigateToWelcome();
                                                        Toast.makeText(RegisterActivity.this, "Welcome to welcome screen", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Failed to save profile image URL: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                                    Toast.makeText(RegisterActivity.this, "Failed to get profile image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar
                            Toast.makeText(RegisterActivity.this, "Failed to upload profile image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveUserLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putBoolean("is_new_user", false); // Assuming Google registration means the user is not new
        editor.apply();
    }

    private void navigateToWelcome() {
        Intent welcome = new Intent(RegisterActivity.this, WelcomeActivity.class);
        startActivity(welcome);
        finish();
    }
}


package com.sarmadtechempire.blogapp.register;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;
import com.sarmadtechempire.blogapp.MainActivity;
import com.sarmadtechempire.blogapp.databinding.ActivitySignInBinding;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth auth;
    private ActivitySignInBinding binding;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Hide the progress text
        binding.circularProgressBar.setProgressFormatter(null);

        setupUI();
    }

    private void setupUI() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                loginUser();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Register button clicked");
                navigateToRegister();
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ForgotPasswordActivity
                navigateToForgotPasswordActivity();
            }
        });
    }

    private void navigateToForgotPasswordActivity() {
        Intent register = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(register);
        finish();
    }

    private void loginUser() {
        String email = binding.loginMailEt.getText().toString();
        String password = binding.loginPasswordEt.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please Fill All The Details", Toast.LENGTH_SHORT).show();
        } else {
            binding.circularProgressBar.setVisibility(View.VISIBLE); // Show progress bar

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.circularProgressBar.setVisibility(View.GONE); // Hide progress bar

                            if (task.isSuccessful()) {
                                Log.d(TAG, "Login successful");
                                saveUserLoginStatus();
                                handleLoginSuccess();
                            } else {
                                Log.d(TAG, "Login failed: " + task.getException().getMessage());
                                Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void handleLoginSuccess() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            onLoginSuccess();
        }
    }

    private void onLoginSuccess() {
        // Update SharedPreferences to indicate the user has logged in
        preferences.edit()
                .putBoolean("is_logged_in", true)
                .putBoolean("is_new_user", false)
                .apply();

        saveUserFCMToken();

        // Navigate to Main Activity
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

private void saveUserFCMToken() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        String userId = user.getUid();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("tokens");
                tokensRef.child(userId).setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "FCM token saved successfully");
                        } else {
                            Log.w(TAG, "Failed to save FCM token", task.getException());
                        }
                    }
                });
            }
        });
    }
}

    // For if user Logged in with  Google_Auth then no need of login
    private void saveUserLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void navigateToRegister() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
        finish();
    }
}
package com.sarmadtechempire.blogapp.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarmadtechempire.blogapp.MainActivity;
import com.sarmadtechempire.blogapp.databinding.ActivitySignInBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth auth;
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Hide the progress text
        binding.circularProgressBar.setProgressFormatter(null);

        // Initializing Firebase Auth
        auth = FirebaseAuth.getInstance();

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
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Toast.makeText(LoginActivity.this, "Login Successful 😊", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToRegister() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
        finish();
    }
}

package com.sarmadtechempire.blogapp.register;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sarmadtechempire.blogapp.MainActivity;
import com.sarmadtechempire.blogapp.R;
import com.sarmadtechempire.blogapp.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing Firebase Auth
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app");

        setupUI();
    }

    public void setupUI() {

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegistration();
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateToRegistration() {
        Intent register = new Intent(SignInActivity.this, RegisterActivity.class);
        startActivity(register);
        finish();
        Toast.makeText(SignInActivity.this, "Welcome to Register screen", Toast.LENGTH_SHORT).show();
    }

    private void loginUser() {
        String loginEmail = binding.loginMailEt.getText().toString();
        String loginPassword = binding.loginPasswordEt.getText().toString();

        if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Please Fill All The Details", Toast.LENGTH_SHORT).show();
        } else {
            binding.progressBar.setVisibility(View.VISIBLE); // Show progress bar

            auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.progressBar.setVisibility(View.GONE); // Hide progress bar

                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Login Successfully 😊", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Login Failed. Please Enter Correct Details", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

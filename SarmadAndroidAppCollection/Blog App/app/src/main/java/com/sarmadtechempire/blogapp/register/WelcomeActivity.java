package com.sarmadtechempire.blogapp.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarmadtechempire.blogapp.MainActivity;
import com.sarmadtechempire.blogapp.R;
import com.sarmadtechempire.blogapp.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        auth = FirebaseAuth.getInstance();

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClick();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void onClick() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(signIn);
                finish();

                Toast.makeText(WelcomeActivity.this, "Welcome to login screen", Toast.LENGTH_SHORT).show();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();

                Toast.makeText(WelcomeActivity.this, "Welcome to Register screen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        binding.progressBar.setVisibility(View.VISIBLE); // Show progress bar

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.progressBar.setVisibility(View.GONE); // Hide progress bar
    }
}

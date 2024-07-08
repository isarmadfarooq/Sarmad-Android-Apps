//package com.sarmadtechempire.blogapp;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.sarmadtechempire.blogapp.databinding.ActivitySplashBinding;
//import com.sarmadtechempire.blogapp.register.WelcomeActivity;
//
//public class SplashActivity extends AppCompatActivity {
//
//    private FirebaseAuth auth;
//    private ActivitySplashBinding binding;
//    private SharedPreferences preferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
//        // Initialize view binding
//        binding = ActivitySplashBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance();
//
//        // Initialize SharedPreferences
//        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                navigateToNextScreen();
//            }
//        }, 4000);
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void navigateToNextScreen() {
//        FirebaseUser currentUser = auth.getCurrentUser();
//        boolean isNewUser = preferences.getBoolean("is_new_user", true); // Adjusted to check if it's a new user
//
//        Intent nextIntent;
//
//        if (isNewUser) {
//            // Navigate to Welcome Activity
//            nextIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
//            // Clear the flag after first navigation to WelcomeActivity
//            preferences.edit().putBoolean("is_new_user", false).apply();
//        } else if (currentUser != null) {
//            // User is signed in, navigate to Main Activity
//            nextIntent = new Intent(SplashActivity.this, MainActivity.class);
//        } else {
//            // No user is signed in, navigate to Welcome Activity
//            nextIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
//        }
//
//        startActivity(nextIntent);
//        finish();
//    }
//}
package com.sarmadtechempire.blogapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarmadtechempire.blogapp.databinding.ActivitySplashBinding;
import com.sarmadtechempire.blogapp.register.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivitySplashBinding binding;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize view binding
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, 4000);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateToNextScreen() {
        FirebaseUser currentUser = auth.getCurrentUser();
        boolean isNewUser = preferences.getBoolean("is_new_user", true);
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

        Intent nextIntent;

        if (isNewUser || !isLoggedIn) {
            // Navigate to Welcome Activity
            nextIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
        } else if (currentUser != null) {
            // User is signed in and has logged in before, navigate to Main Activity
            nextIntent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // No user is signed in, navigate to Welcome Activity
            nextIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
        }

        startActivity(nextIntent);
        finish();
    }
}

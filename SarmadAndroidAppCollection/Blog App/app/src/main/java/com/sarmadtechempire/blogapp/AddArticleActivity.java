package com.sarmadtechempire.blogapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.sarmadtechempire.blogapp.NotificationUtils.FcmNotificationsSender;
import com.sarmadtechempire.blogapp.databinding.ActivityAddArticleBinding;


import java.text.SimpleDateFormat;
import java.util.Date;

public class AddArticleActivity extends AppCompatActivity {

    private static final String TAG = "AddArticleActivity";

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference databaseReference = database.getReference("blogs");
    DatabaseReference userReference = database.getReference("users");
    DatabaseReference tokensReference = database.getReference("tokens");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ActivityAddArticleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BlogDescription_textEditor_Funcionalities();

        binding.backIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMain();
            }
        });

        binding.addBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.circularProgressBar.setVisibility(View.VISIBLE); // Show progress bar
                long startTime = System.currentTimeMillis();
                String title = binding.blogTitleTextIL.getEditText().getText().toString().trim();
                String description = Html.toHtml(binding.blogDescriptionTextIl.getEditText().getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddArticleActivity.this, "Please Fill All The fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    String userName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";
                    String userImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                    userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserData userData = snapshot.getValue(UserData.class);
                            if (userData != null) {
                                String userNameFromDb = userData.getName();
                                String userImageUrlFromDb = userData.getProfileImage();
                                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                BlogItemModel blogItemModel = new BlogItemModel(userId, userImageUrlFromDb, title, description, currentDate, userNameFromDb);
                                String key = databaseReference.push().getKey();
                                if (key != null) {
                                    blogItemModel.setPostId(key);
                                    databaseReference.child(key).setValue(blogItemModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Blog added successfully");
                                                Toast.makeText(AddArticleActivity.this, "Blog Added Successfully", Toast.LENGTH_SHORT).show();
                                                sendNotificationToAllUsers(userNameFromDb, title, description, userImageUrlFromDb);
                                                navigateToMain();
                                                binding.circularProgressBar.setVisibility(View.GONE);
                                            } else {
                                                Log.e(TAG, "Error adding blog: " + task.getException());
                                                Toast.makeText(AddArticleActivity.this, "Error Adding Blog", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "Blog key is null");
                                }
                            } else {
                                Log.e(TAG, "UserData is null");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error fetching user data: " + error.toException());
                        }
                    });
                } else {
                    Log.e(TAG, "No authenticated user found");
                }
            }
        });
    }
    public void BlogDescription_textEditor_Funcionalities() {

        binding.boldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStyle(Typeface.BOLD);
            }
        });

        binding.italicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStyle(Typeface.ITALIC);
            }
        });
        binding.underLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUnderLine();

            }
        });

        binding.noFormatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = binding.blogDescriptionTextIl.getEditText();
                String stringText = binding.blogDescriptionTextIl.getEditText().getText().toString();
                editText.setText(stringText);
            }
        });

        binding.alignLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyAlignment("left");
            }
        });

        binding.alignRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyAlignment("right");
            }
        });

        binding.alignCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyAlignment("center");
            }
        });

    }

    private void toggleStyle(int style) {
        EditText editText = binding.blogDescriptionTextIl.getEditText();
        if (editText != null) {
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            Spannable spannableString = new SpannableStringBuilder(editText.getText());

            // Check if the selected text has the style applied
            boolean hasStyle = false;
            StyleSpan[] styleSpans = spannableString.getSpans(start, end, StyleSpan.class);
            for (StyleSpan span : styleSpans) {
                if (span.getStyle() == style) {
                    hasStyle = true;
                    break;
                }
            }

            if (hasStyle) {
                // Remove the style
                spannableString.removeSpan(styleSpans[0]);
            } else {
                // Apply the style
                spannableString.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            editText.setText(spannableString);
            editText.setSelection(start, end);
        }
    }

    private void toggleUnderLine()
    {
        EditText editText = binding.blogDescriptionTextIl.getEditText();

        if (editText != null) {
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            Spannable spannableString = new SpannableStringBuilder(editText.getText());

            // Check if the selected text underlined
            boolean hasUnderlined = false;
            UnderlineSpan[] underlineSpans = spannableString.getSpans(start, end, UnderlineSpan.class);
            if(underlineSpans.length > 0)
            {
                hasUnderlined = true;
            }

            if(hasUnderlined)
            {
                spannableString.removeSpan(underlineSpans[0]);
            }
            else
            {
                spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            editText.setText(spannableString);
            editText.setSelection(start, end);
        }
    }


    private void applyAlignment(String alignment) {
        try {
            EditText editText = binding.blogDescriptionTextIl.getEditText();
            if (editText != null) {
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                // Wrap the selected text with the alignment div
                String before = editText.getText().toString().substring(0, start);
                String selected = editText.getText().toString().substring(start, end);
                String after = editText.getText().toString().substring(end);

                String alignedText = before + "<div style='text-align:" + alignment + ";'>" + selected + "</div>" + after;

                editText.setText(Html.fromHtml(alignedText, Html.FROM_HTML_MODE_LEGACY));
                editText.setSelection(start, end + alignment.length() + 20);  // Adjust the cursor position
            }
        } catch (Exception e) {
            Log.e("AddArticleActivity", "Error applying alignment: " + alignment, e);
        }
    }


    private void sendNotificationToAllUsers(String userName, String title, String description, String imageUrl) {
        tokensReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userToken = dataSnapshot.getValue(String.class);
                    if (userToken != null && !userToken.isEmpty()) {
                        Log.d(TAG, "Sending notification to token: " + userToken);
                        String notificationBody = userName + " added a new blog: " + title;
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken, title, notificationBody, imageUrl, getApplicationContext(), AddArticleActivity.this);
                        notificationsSender.sendNotification();
                    } else {
                        Log.e(TAG, "User token is null or empty");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching tokens: " + error.toException());
            }
        });
    }

    private void navigateToMain() {
        Intent welcome = new Intent(AddArticleActivity.this, MainActivity.class);
        startActivity(welcome);
        finish();
    }
}
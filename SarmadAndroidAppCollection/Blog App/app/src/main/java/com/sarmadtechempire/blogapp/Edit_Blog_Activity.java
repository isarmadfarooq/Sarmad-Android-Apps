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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.databinding.ActivityEditBlogBinding;

public class Edit_Blog_Activity extends AppCompatActivity {

    private ActivityEditBlogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEditBlogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BlogDescription_textEditor_Functionalities();

        binding.backIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        BlogItemModel blogItem = getIntent().getParcelableExtra("blogItems");

        if (blogItem != null) {
            binding.blogTitleTextIL.getEditText().setText(blogItem.getHeading());
            binding.blogDescriptionTextIl.getEditText().setText(Html.fromHtml(blogItem.getPost(), Html.FROM_HTML_MODE_LEGACY));
        }

        binding.addBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedTitle = binding.blogTitleTextIL.getEditText().getText().toString().trim();
                String updatedDescription = Html.toHtml(binding.blogDescriptionTextIl.getEditText().getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);

                if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                    Toast.makeText(Edit_Blog_Activity.this, "Please Fill All The fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (blogItem != null) {
                        blogItem.setHeading(updatedTitle);
                        blogItem.setPost(updatedDescription);
                        updateDataInFirebase(blogItem);
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateDataInFirebase(BlogItemModel blogItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("blogs");
        String postId = blogItem.getPostId();

        databaseReference.child(postId).setValue(blogItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Edit_Blog_Activity.this, "Blog Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Edit_Blog_Activity.this, "Blog Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void BlogDescription_textEditor_Functionalities() {
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

    private void toggleUnderLine() {
        EditText editText = binding.blogDescriptionTextIl.getEditText();

        if (editText != null) {
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            Spannable spannableString = new SpannableStringBuilder(editText.getText());

            // Check if the selected text is underlined
            boolean hasUnderlined = false;
            UnderlineSpan[] underlineSpans = spannableString.getSpans(start, end, UnderlineSpan.class);
            if (underlineSpans.length > 0) {
                hasUnderlined = true;
            }

            if (hasUnderlined) {
                spannableString.removeSpan(underlineSpans[0]);
            } else {
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
                editText.setSelection(start, start + alignedText.length());
            }
        } catch (Exception e) {
            Log.e("Edit_Blog_Activity", "Error applying alignment: " + alignment, e);
        }
    }

    @Override
    public void onBackPressed() {
        // Navigate to the MainActivity instead of exiting the app
        super.onBackPressed();
        navigateBack();
        finish();
    }

    private void navigateBack() {
        navigateToProfile();
        finish();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(Edit_Blog_Activity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

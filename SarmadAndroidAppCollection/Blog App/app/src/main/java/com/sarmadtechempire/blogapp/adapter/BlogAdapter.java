package com.sarmadtechempire.blogapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.R;
import com.sarmadtechempire.blogapp.ReadMoreActivity;
import com.sarmadtechempire.blogapp.databinding.BlogItemsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<BlogItemModel> items;
    private Context context;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://blog-app-389b6-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public BlogAdapter(Context context, List<BlogItemModel> items) {
        this.context = context;
        this.items = items;
        // Reverse the list
        Collections.reverse(this.items);
    }

    @NonNull
    @Override
    public BlogAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BlogItemsBinding binding = BlogItemsBinding.inflate(inflater, parent, false);
        return new BlogViewHolder(binding, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.BlogViewHolder holder, int position) {
        BlogItemModel blogItem = items.get(position);
        holder.bind(blogItem);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {

        private BlogItemsBinding binding;
        private Context context;

        public BlogViewHolder(@NonNull BlogItemsBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        public void bind(BlogItemModel blogItemModel) {

            if (blogItemModel == null || blogItemModel.getPostId() == null) {
                Log.e("BlogAdapter", "BlogItemModel or its PostId is null");
                return; // Prevent further processing if data is invalid
            }

            binding.blogHeadingText.setText(blogItemModel.getHeading());
            Glide.with(binding.profileIv.getContext())
                    .load(blogItemModel.getImageUrl())
                    .into(binding.profileIv);

            binding.bloggerNameTv.setText(blogItemModel.getUserName());
            binding.dateTv.setText(blogItemModel.getDate());
//            binding.blogColumnTv.setText(blogItemModel.getPost());
            binding.blogColumnTv.setText(Html.fromHtml(blogItemModel.getPost(), Html.FROM_HTML_MODE_LEGACY));
            binding.likeCountsTv.setText(String.valueOf(blogItemModel.getLikeCount()));

            // Set onClickListener
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReadMoreActivity.class);
                    intent.putExtra("blogItems", blogItemModel);
                    context.startActivity(intent);
                }

            });

            // Check if the current user has liked the post and update the like button image
            DatabaseReference postLikeReference = databaseReference.child("blogs").child(blogItemModel.getPostId()).child("likes");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if(currentUser!=null)
            {
                String uid = currentUser.getUid();
                postLikeReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            binding.blogLikeClick.setImageResource(R.drawable.like_red_icon);
                        }
                        else
                        {
                            binding.blogLikeClick.setImageResource(R.drawable.like_white_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            // handle likebutton clicks

            binding.blogLikeClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentUser!=null)
                    {
                        handleLikedIconClicked(blogItemModel.getPostId(), blogItemModel, binding);
                    }
                    else
                    {
                        Toast.makeText(context,"You have to login first",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // set the initial icon based on saved status
            DatabaseReference userReference = databaseReference.child("users").child(currentUser.getUid() != null ? currentUser.getUid() : "");
            DatabaseReference postSaveReference = userReference.child("saveBlogPosts").child(blogItemModel.getPostId());

            postSaveReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        //if blog is already saved
                        binding.saveBlogBtn.setImageResource(R.drawable.post_icon_red_fill);

                    }
                    else
                    {
                        binding.saveBlogBtn.setImageResource(R.drawable.post_icon_red);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // handle save button clicks
            binding.saveBlogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(currentUser!=null)
                    {
                        handleSaveIconClicked(blogItemModel.getPostId(), blogItemModel, binding);
                    }
                    else
                    {
                        Toast.makeText(context,"You have to login first",Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }



    private void handleLikedIconClicked(String postId, BlogItemModel blogItemModel, BlogItemsBinding binding) {
            DatabaseReference userReference = databaseReference.child("users").child(currentUser.getUid());
            DatabaseReference postLikeReference = databaseReference.child("blogs").child(postId).child("likes");

            postLikeReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userReference.child("likes").child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                postLikeReference.child(currentUser.getUid()).removeValue();
                                List<String> likesByList = blogItemModel.getLikesBy();
                                likesByList.remove(currentUser.getUid());
                                blogItemModel.setLikesBy(new MutableLiveData<>(likesByList)); // Update MutableLiveData
                                updateLikeLikeButtonImage(binding, false);
                                int newLikeCount = blogItemModel.getLikeCount() - 1;
                                binding.likeCountsTv.setText(String.valueOf(newLikeCount));
                                databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Unliked the post", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("LikedClicked", "Failed to unlike the blog", e);
                                Toast.makeText(context, "Failed to unlike the post", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        userReference.child("likes").child(postId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                postLikeReference.child(currentUser.getUid()).setValue(true);
                                List<String> likesByList = blogItemModel.getLikesBy();
                                if (likesByList == null) {
                                    likesByList = new ArrayList<>();
                                }
                                likesByList.add(currentUser.getUid());
                                blogItemModel.setLikesBy(new MutableLiveData<>(likesByList)); // Update MutableLiveData
                                updateLikeLikeButtonImage(binding, true);
                                int newLikeCount = blogItemModel.getLikeCount() + 1;
                                binding.likeCountsTv.setText(String.valueOf(newLikeCount));
                                databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Liked the post", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("LikedClicked", "Failed to like the blog", e);
                                Toast.makeText(context, "Failed to like the post", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential errors here
                }
            });
        }



        private void updateLikeLikeButtonImage(BlogItemsBinding binding,boolean liked) {
            if(liked) {
                binding.blogLikeClick.setImageResource(R.drawable.like_white_icon);
            }
            else {
                binding.blogLikeClick.setImageResource(R.drawable.like_red_icon);
            }
        }

        private void handleSaveIconClicked(String postId, BlogItemModel blogItemModel, BlogItemsBinding binding)
        {
            DatabaseReference userReference = databaseReference.child("users").child(currentUser.getUid());
            userReference.child("saveBlogPosts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // If the blog is currently saved, so unsaved the blog
                    if(snapshot.exists())
                    {
                        userReference.child("saveBlogPosts").child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // update the UI

                                BlogItemModel clickedBlogItem = null;

                                for(BlogItemModel item: items)
                                {
                                    if(item.getPostId().equals(postId))
                                    {
                                        clickedBlogItem = item;
                                        break;
                                    }
                                }
                                if(clickedBlogItem!=null)
                                {
                                    clickedBlogItem.setIsSaved(false);
                                }
                                notifyDataSetChanged();

                                Context context = binding.getRoot().getContext();

                                Toast.makeText(context,"Blog UnSaved",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Context context = binding.getRoot().getContext();
                                Toast.makeText(context,"Fail to unsaved the blog",Toast.LENGTH_SHORT).show();
                            }
                        });
                        binding.saveBlogBtn.setImageResource(R.drawable.post_icon_red);
                    }
                    else
                    {
                        // If blog is not save so saved the blog
                        userReference.child("saveBlogPosts").child(postId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                BlogItemModel clickBlogItem = null;
                                for (BlogItemModel item : items) {
                                    if(item.getPostId().equals(postId))
                                    {
                                        clickBlogItem = item;
                                        break;
                                    }
                                }

                                if (clickBlogItem != null) {
                                clickBlogItem.setIsSaved(true);
                            }
                            notifyDataSetChanged();

                                Context context = binding.getRoot().getContext();

                                Toast.makeText(context,"Blog saved successfully",Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Context context = binding.getRoot().getContext();
                                Toast.makeText(context,"Fail to save the blog",Toast.LENGTH_SHORT).show();
                            }
                        });

                        binding.saveBlogBtn.setImageResource(R.drawable.post_icon_red_fill);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}

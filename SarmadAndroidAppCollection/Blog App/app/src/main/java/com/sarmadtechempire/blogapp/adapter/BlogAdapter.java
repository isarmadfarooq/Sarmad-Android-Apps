package com.sarmadtechempire.blogapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.ReadMoreActivity;
import com.sarmadtechempire.blogapp.databinding.BlogItemsBinding;

import java.util.Collections;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<BlogItemModel> items;
    private Context context;

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
            binding.blogHeadingText.setText(blogItemModel.getHeading());
            Glide.with(binding.profileIv.getContext())
                    .load(blogItemModel.getImageUrl())
                    .into(binding.profileIv);

            binding.bloggerNameTv.setText(blogItemModel.getUserName());
            binding.dateTv.setText(blogItemModel.getDate());
            binding.blogColumnTv.setText(blogItemModel.getPost());
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
        }
    }
}

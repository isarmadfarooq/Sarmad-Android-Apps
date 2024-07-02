package com.sarmadtechempire.blogapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.databinding.BlogItemsBinding;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<BlogItemModel> items;

    public BlogAdapter(List<BlogItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BlogAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BlogItemsBinding binding = BlogItemsBinding.inflate(inflater, parent, false);
        return new BlogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.BlogViewHolder holder, int position) {

        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        private BlogItemsBinding binding;
        public BlogViewHolder(@NonNull BlogItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public BlogItemsBinding getBinding()
        {
            return binding;
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
        }
    }

}

package com.sarmadtechempire.blogapp.adapter;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sarmadtechempire.blogapp.Model.BlogItemModel;
import com.sarmadtechempire.blogapp.databinding.ArticleItemsBinding;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<BlogItemModel> items;
    private Context context;
    private AdapterView.OnItemClickListener itemClickListener;

    public ArticleAdapter(Context context, List<BlogItemModel> items, AdapterView.OnItemClickListener itemClickListener)
    {
        this.context = context;
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ArticleItemsBinding binding = ArticleItemsBinding.inflate(inflater, parent, false);
        return new ArticleViewHolder(binding, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapter.ArticleViewHolder holder, int position) {
        BlogItemModel blogItem = items.get(position);
        holder.bind(blogItem);
    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setData(List<BlogItemModel> blogSaveList) {
        this.items = blogSaveList;
        notifyDataSetChanged();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        private ArticleItemsBinding binding;
        private Context context;

        public ArticleViewHolder(@NonNull ArticleItemsBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        public void bind(BlogItemModel blogItem) {
            if (blogItem == null || blogItem.getPostId() == null) {
                Log.e("BlogAdapter", "BlogItemModel or its PostId is null");
                return; // Prevent further processing if data is invalid
            }

            binding.blogHeadingText.setText(blogItem.getHeading());
            Glide.with(binding.profileIv.getContext())
                    .load(blogItem.getImageUrl())
                    .into(binding.profileIv);

            binding.bloggerNameTv.setText(blogItem.getUserName());
            binding.dateTv.setText(blogItem.getDate());
            binding.blogColumnTv.setText(Html.fromHtml(blogItem.getPost(), Html.FROM_HTML_MODE_LEGACY));
        }
    }
}

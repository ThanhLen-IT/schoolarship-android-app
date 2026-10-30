package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookmarkedPostAdapter extends RecyclerView.Adapter<BookmarkedPostAdapter.ViewHolder> {

    public interface OnUnbookmarkClickListener {
        void onUnbookmarkClick(BookmarkedPostItem item, int position);
    }

    private final List<BookmarkedPostItem> items;
    private final OnUnbookmarkClickListener listener;

    public BookmarkedPostAdapter(List<BookmarkedPostItem> items, OnUnbookmarkClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmarked_post_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkedPostItem item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvAuthor.setText(item.getAuthor());
        holder.tvHashtag.setText(item.getHashtag());

        holder.btnUnbookmark.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onUnbookmarkClick(item, pos);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Mở bài viết: " + item.getTitle(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvHashtag, btnUnbookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvHashtag = itemView.findViewById(R.id.tv_post_hashtag);
            btnUnbookmark = itemView.findViewById(R.id.btn_unbookmark);
        }
    }
}

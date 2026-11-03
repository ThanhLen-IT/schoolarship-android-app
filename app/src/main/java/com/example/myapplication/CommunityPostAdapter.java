package com.example.myapplication;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.ViewHolder> {

    private final List<CommunityPost> postList;

    public CommunityPostAdapter(List<CommunityPost> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityPost post = postList.get(position);

        holder.ivAvatar.setImageResource(post.getAvatarResId());
        holder.tvAuthorName.setText(post.getAuthorName());
        holder.tvPostTime.setText(post.getTimeAgo());
        holder.tvContent.setText(post.getContent());

        if (post.isVerified()) {
            holder.ivVerificationBadge.setVisibility(View.VISIBLE);
        } else {
            holder.ivVerificationBadge.setVisibility(View.GONE);
        }

        if (post.getMediaImageResId() != 0) {
            holder.ivMedia.setImageResource(post.getMediaImageResId());
        } else {
            holder.ivMedia.setImageResource(R.drawable.nen1);
        }

        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        holder.tvShareCount.setText(String.valueOf(post.getShareCount()));

        updateLikeIconState(holder, post.isLiked());
        updateSaveIconState(holder, post.isSaved());

        // Like click toggle
        holder.btnLike.setOnClickListener(v -> {
            boolean newLiked = !post.isLiked();
            post.setLiked(newLiked);
            post.setLikeCount(newLiked ? post.getLikeCount() + 1 : post.getLikeCount() - 1);
            holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
            updateLikeIconState(holder, newLiked);
        });

        // Comment click
        holder.btnComment.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Comment on post by " + post.getAuthorName(), Toast.LENGTH_SHORT).show()
        );

        // Share click
        holder.btnShare.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Share post by " + post.getAuthorName(), Toast.LENGTH_SHORT).show()
        );

        // Bookmark click
        holder.btnBookmark.setOnClickListener(v -> {
            boolean newSaved = !post.isSaved();
            post.setSaved(newSaved);
            updateSaveIconState(holder, newSaved);
            Toast.makeText(v.getContext(), newSaved ? "Post saved" : "Post removed from saved", Toast.LENGTH_SHORT).show();
        });

        // Connect with mentor
        holder.layoutConnectMentor.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Connecting with mentor for " + post.getAuthorName(), Toast.LENGTH_SHORT).show()
        );

        // Post menu
        holder.btnMenu.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Post options", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateLikeIconState(ViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.ivLikeIcon.setColorFilter(Color.parseColor("#EF4444"), PorterDuff.Mode.SRC_IN);
        } else {
            holder.ivLikeIcon.clearColorFilter();
        }
    }

    private void updateSaveIconState(ViewHolder holder, boolean isSaved) {
        if (isSaved) {
            holder.btnBookmark.setColorFilter(Color.parseColor("#0F0954"), PorterDuff.Mode.SRC_IN);
        } else {
            holder.btnBookmark.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivVerificationBadge, ivMedia, ivLikeIcon, ivCommentIcon, ivShareIcon, btnBookmark;
        TextView tvAuthorName, tvPostTime, tvContent, tvLikeCount, tvCommentCount, tvShareCount;
        View btnLike, btnComment, btnShare, layoutConnectMentor;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_author_avatar);
            ivVerificationBadge = itemView.findViewById(R.id.iv_verification_badge);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvPostTime = itemView.findViewById(R.id.tv_post_time);
            btnMenu = itemView.findViewById(R.id.btn_post_menu);
            tvContent = itemView.findViewById(R.id.tv_post_content);
            ivMedia = itemView.findViewById(R.id.iv_post_media);
            layoutConnectMentor = itemView.findViewById(R.id.layout_connect_mentor);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
            btnShare = itemView.findViewById(R.id.btn_share);
            ivLikeIcon = itemView.findViewById(R.id.iv_like_icon);
            ivCommentIcon = itemView.findViewById(R.id.iv_comment_icon);
            ivShareIcon = itemView.findViewById(R.id.iv_share_icon);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            tvShareCount = itemView.findViewById(R.id.tv_share_count);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
        }
    }
}

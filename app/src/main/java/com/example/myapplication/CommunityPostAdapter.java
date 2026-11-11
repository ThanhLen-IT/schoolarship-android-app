package com.example.myapplication;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.ViewHolder> {

    private final List<CommunityPost> postList;
    private Runnable reload;

    public CommunityPostAdapter(List<CommunityPost> postList) {
        this.postList = postList;
    }

    public CommunityPostAdapter(List<CommunityPost> posts, Runnable reload) {
        this(posts);
        this.reload = reload;
    }

    private android.app.Activity activity(android.content.Context context) {
        while (context instanceof android.content.ContextWrapper
                && !(context instanceof android.app.Activity))
            context = ((android.content.ContextWrapper) context).getBaseContext();
        return (android.app.Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_community_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityPost post = postList.get(position);

        RemoteImage.show(holder.ivAvatar, post.getAvatarUrl());
        holder.tvAuthorName.setText(post.getAuthorName());
        holder.tvPostTime.setText(post.getTimeAgo());
        holder.tvContent.setText(post.getContent());

        if (post.isVerified()) {
            holder.ivVerificationBadge.setVisibility(View.VISIBLE);
        } else {
            holder.ivVerificationBadge.setVisibility(View.GONE);
        }

        holder.ivMedia.setVisibility(
                post.getImageUrl() == null || post.getImageUrl().isEmpty()
                        ? View.GONE
                        : View.VISIBLE);
        RemoteImage.show(holder.ivMedia, post.getImageUrl());
        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        holder.tvShareCount.setText(String.valueOf(post.getShareCount()));

        updateLikeIconState(holder, post.isLiked());
        updateSaveIconState(holder, post.isSaved());

        holder.btnLike.setOnClickListener(
                v -> {
                    android.app.Activity owner = activity(v.getContext());
                    if (!ApiUi.requireLogin(owner)) return;
                    holder.btnLike.setEnabled(false);
                    ApiUi.request(
                            owner,
                            ApiClient.getApiService().likePost(ApiUi.bearer(owner), post.getId()),
                            result -> {
                                holder.btnLike.setEnabled(true);
                                if (result.getLiked() != null) post.setLiked(result.getLiked());
                                if (result.getLikeCount() != null)
                                    post.setLikeCount(result.getLikeCount());
                                notifyDataSetChanged();
                            },
                            message -> {
                                holder.btnLike.setEnabled(true);
                                ApiUi.error(owner, message);
                            });
                });
        holder.btnComment.setOnClickListener(
                v -> comments(activity(v.getContext()), post, 1, new java.util.ArrayList<>()));
        holder.btnShare.setOnClickListener(
                v -> {
                    android.app.Activity owner = activity(v.getContext());
                    owner.startActivity(
                            android.content.Intent.createChooser(
                                    new android.content.Intent(android.content.Intent.ACTION_SEND)
                                            .setType("text/plain")
                                            .putExtra(
                                                    android.content.Intent.EXTRA_TEXT,
                                                    post.getContent()),
                                    "Chia sẻ bài viết"));
                });
        holder.tvShareCount.setVisibility(View.GONE);
        holder.btnBookmark.setVisibility(View.GONE);
        holder.layoutConnectMentor.setVisibility(View.GONE);
        holder.btnMenu.setVisibility(View.GONE);
    }

    private void comments(
            android.app.Activity owner,
            CommunityPost post,
            int page,
            java.util.List<PostComment> accumulated) {
        ApiUi.request(
                owner,
                ApiClient.getApiService().getComments(post.getId(), page, 50),
                result -> {
                    if (result.getData() != null) accumulated.addAll(result.getData());
                    StringBuilder text = new StringBuilder();
                    for (PostComment comment : accumulated)
                        text.append(comment.user_name == null ? "Người dùng" : comment.user_name)
                                .append(": ")
                                .append(comment.content)
                                .append("\n\n");
                    android.widget.EditText input = new android.widget.EditText(owner);
                    input.setHint("Viết bình luận…");
                    androidx.appcompat.app.AlertDialog dialog =
                            new androidx.appcompat.app.AlertDialog.Builder(owner)
                                    .setTitle("Bình luận")
                                    .setMessage(
                                            text.length() == 0
                                                    ? "Chưa có bình luận."
                                                    : text.toString())
                                    .setView(input)
                                    .setNegativeButton("Đóng", null)
                                    .setPositiveButton("Gửi", null)
                                    .setNeutralButton(
                                            result.getTotalPages() != null
                                                            && page < result.getTotalPages()
                                                    ? "Tải thêm"
                                                    : null,
                                            (d, w) -> comments(owner, post, page + 1, accumulated))
                                    .create();
                    dialog.setOnShowListener(
                            d ->
                                    dialog.getButton(-1)
                                            .setOnClickListener(
                                                    v -> {
                                                        if (!ApiUi.requireLogin(owner)) return;
                                                        String value =
                                                                input.getText().toString().trim();
                                                        if (value.isEmpty()) {
                                                            input.setError("Nhập nội dung.");
                                                            return;
                                                        }
                                                        dialog.getButton(-1).setEnabled(false);
                                                        java.util.Map<String, String> body =
                                                                new java.util.HashMap<>();
                                                        body.put("content", value);
                                                        ApiUi.request(
                                                                owner,
                                                                ApiClient.getApiService()
                                                                        .addComment(
                                                                                ApiUi.bearer(owner),
                                                                                post.getId(),
                                                                                body),
                                                                sent -> {
                                                                    dialog.dismiss();
                                                                    if (reload != null)
                                                                        reload.run();
                                                                    comments(
                                                                            owner,
                                                                            post,
                                                                            1,
                                                                            new java.util
                                                                                    .ArrayList<>());
                                                                },
                                                                message -> {
                                                                    dialog.getButton(-1)
                                                                            .setEnabled(true);
                                                                    ApiUi.error(owner, message);
                                                                });
                                                    }));
                    dialog.show();
                },
                message -> ApiUi.error(owner, message));
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
        ImageView ivAvatar,
                ivVerificationBadge,
                ivMedia,
                ivLikeIcon,
                ivCommentIcon,
                ivShareIcon,
                btnBookmark;
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

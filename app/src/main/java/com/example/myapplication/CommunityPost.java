package com.example.myapplication;

import java.io.Serializable;

public class CommunityPost implements Serializable {
    private String id;

    @com.google.gson.annotations.SerializedName("author_name")
    private String authorName;

    @com.google.gson.annotations.SerializedName("author_avatar")
    private String avatarUrl;

    @com.google.gson.annotations.SerializedName("image_url")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("author_role")
    private String authorRole;

    private String title;
    private int avatarResId;

    @com.google.gson.annotations.SerializedName("created_at")
    private String timeAgo;

    private boolean isVerified;
    private String content;
    private int mediaImageResId;

    @com.google.gson.annotations.SerializedName("like_count")
    private int likeCount;

    @com.google.gson.annotations.SerializedName("comment_count")
    private int commentCount;

    private int shareCount;
    private boolean isLiked;
    private boolean isSaved;

    public CommunityPost(
            String id,
            String authorName,
            int avatarResId,
            String timeAgo,
            boolean isVerified,
            String content,
            int mediaImageResId,
            int likeCount,
            int commentCount,
            int shareCount,
            boolean isLiked,
            boolean isSaved) {
        this.id = id;
        this.authorName = authorName;
        this.avatarResId = avatarResId;
        this.timeAgo = timeAgo;
        this.isVerified = isVerified;
        this.content = content;
        this.mediaImageResId = mediaImageResId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.isLiked = isLiked;
        this.isSaved = isSaved;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName == null ? "" : authorName;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public boolean isVerified() {
        return "mentor".equals(authorRole);
    }

    public String getContent() {
        return (title == null ? "" : title + "\n\n") + (content == null ? "" : content);
    }

    public int getMediaImageResId() {
        return mediaImageResId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
}

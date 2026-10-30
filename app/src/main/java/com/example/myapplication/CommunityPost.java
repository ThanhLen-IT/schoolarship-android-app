package com.example.myapplication;

import java.io.Serializable;

public class CommunityPost implements Serializable {
    private String id;
    private String authorName;
    private int avatarResId;
    private String timeAgo;
    private boolean isVerified;
    private String content;
    private int mediaImageResId;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private boolean isLiked;
    private boolean isSaved;

    public CommunityPost(String id, String authorName, int avatarResId, String timeAgo, 
                         boolean isVerified, String content, int mediaImageResId, 
                         int likeCount, int commentCount, int shareCount, 
                         boolean isLiked, boolean isSaved) {
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

    public String getId() { return id; }
    public String getAuthorName() { return authorName; }
    public int getAvatarResId() { return avatarResId; }
    public String getTimeAgo() { return timeAgo; }
    public boolean isVerified() { return isVerified; }
    public String getContent() { return content; }
    public int getMediaImageResId() { return mediaImageResId; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public int getShareCount() { return shareCount; }
    public boolean isLiked() { return isLiked; }
    public boolean isSaved() { return isSaved; }

    public void setLiked(boolean liked) { isLiked = liked; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setSaved(boolean saved) { isSaved = saved; }
}

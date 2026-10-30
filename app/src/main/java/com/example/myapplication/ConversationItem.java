package com.example.myapplication;

import java.io.Serializable;

public class ConversationItem implements Serializable {
    private String id;
    private String name;
    private String preview;
    private String timestamp;
    private int avatarResId;
    private boolean isUnread;

    public ConversationItem(String id, String name, String preview, String timestamp, int avatarResId, boolean isUnread) {
        this.id = id;
        this.name = name;
        this.preview = preview;
        this.timestamp = timestamp;
        this.avatarResId = avatarResId;
        this.isUnread = isUnread;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPreview() { return preview; }
    public String getTimestamp() { return timestamp; }
    public int getAvatarResId() { return avatarResId; }
    public boolean isUnread() { return isUnread; }
}

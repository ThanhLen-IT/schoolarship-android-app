package com.example.myapplication;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private String id;
    private String title;
    private String timestamp;
    private int iconResId;
    private boolean isRead;

    public NotificationItem(String id, String title, String timestamp, int iconResId, boolean isRead) {
        this.id = id;
        this.title = title;
        this.timestamp = timestamp;
        this.iconResId = iconResId;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getTimestamp() { return timestamp; }
    public int getIconResId() { return iconResId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}

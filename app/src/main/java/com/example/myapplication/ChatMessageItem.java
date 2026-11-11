package com.example.myapplication;

import java.io.Serializable;

public class ChatMessageItem implements Serializable {
    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_OUTGOING = 2;
    public static final int TYPE_ATTACHMENT = 3;

    private String id;
    private int type; // 1 = incoming, 2 = outgoing, 3 = attachment
    private String text;
    private String timestamp;
    private String fileName;
    private String fileSize;
    private int avatarResId;

    public ChatMessageItem(String id, int type, String text, String timestamp, int avatarResId) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.timestamp = timestamp;
        this.avatarResId = avatarResId;
    }

    public ChatMessageItem(String id, int type, String fileName, String fileSize, String timestamp, int avatarResId) {
        this.id = id;
        this.type = type;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.timestamp = timestamp;
        this.avatarResId = avatarResId;
    }

    public String getId() { return id; }
    public int getType() { return type; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }
    public String getFileName() { return fileName; }
    public String getFileSize() { return fileSize; }
    public int getAvatarResId() { return avatarResId; }
}

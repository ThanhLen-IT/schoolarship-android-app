package com.example.myapplication;

import java.io.Serializable;

public class ContactAvatarItem implements Serializable {
    private String id;
    private String name;
    private int avatarResId;
    private boolean isOnline;

    public ContactAvatarItem(String id, String name, int avatarResId, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.avatarResId = avatarResId;
        this.isOnline = isOnline;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAvatarResId() { return avatarResId; }
    public boolean isOnline() { return isOnline; }
}

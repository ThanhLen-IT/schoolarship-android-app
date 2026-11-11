package com.example.myapplication;

import java.io.Serializable;

public class MyApplicationItem implements Serializable {
    private String id;
    private String mentorName;
    private String organization;
    private String applicationDate;
    private String status; // "Đang chờ phản hồi", "Đã được nhận", "Chưa phù hợp"
    private String objective;
    private int avatarResId;

    public MyApplicationItem(String id, String mentorName, String organization, String applicationDate, String status, String objective, int avatarResId) {
        this.id = id;
        this.mentorName = mentorName;
        this.organization = organization;
        this.applicationDate = applicationDate;
        this.status = status;
        this.objective = objective;
        this.avatarResId = avatarResId;
    }

    public String getId() { return id; }
    public String getMentorName() { return mentorName; }
    public String getOrganization() { return organization; }
    public String getApplicationDate() { return applicationDate; }
    public String getStatus() { return status; }
    public String getObjective() { return objective; }
    public int getAvatarResId() { return avatarResId; }
}

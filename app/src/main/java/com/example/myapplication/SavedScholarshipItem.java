package com.example.myapplication;

import java.io.Serializable;

public class SavedScholarshipItem implements Serializable {
    private String id;
    private String title;
    private String organization;
    private String deadline;
    private String followStatus;

    public SavedScholarshipItem(String id, String title, String organization, String deadline, String followStatus) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.deadline = deadline;
        this.followStatus = followStatus;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getDeadline() { return deadline; }
    public String getFollowStatus() { return followStatus; }
}

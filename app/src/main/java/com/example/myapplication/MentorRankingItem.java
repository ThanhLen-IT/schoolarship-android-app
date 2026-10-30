package com.example.myapplication;

import java.io.Serializable;

public class MentorRankingItem implements Serializable {
    private int rank;
    private String mentorName;
    private String successRate;
    private String avgRating;
    private String completionRate;
    private String responseRate;
    private String experience;

    public MentorRankingItem(int rank, String mentorName, String successRate, String avgRating, String completionRate, String responseRate, String experience) {
        this.rank = rank;
        this.mentorName = mentorName;
        this.successRate = successRate;
        this.avgRating = avgRating;
        this.completionRate = completionRate;
        this.responseRate = responseRate;
        this.experience = experience;
    }

    public int getRank() { return rank; }
    public String getMentorName() { return mentorName; }
    public String getSuccessRate() { return successRate; }
    public String getAvgRating() { return avgRating; }
    public String getCompletionRate() { return completionRate; }
    public String getResponseRate() { return responseRate; }
    public String getExperience() { return experience; }
}

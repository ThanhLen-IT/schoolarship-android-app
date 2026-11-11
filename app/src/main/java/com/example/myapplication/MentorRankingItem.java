package com.example.myapplication;

import java.io.Serializable;

public class MentorRankingItem implements Serializable {
    private int rank;
    private String mentorName;
    private String total;
    private String successRate;
    private String avgRating;
    private String completionRate;
    private String responseRate;

    public MentorRankingItem(int rank, String mentorName, String total, String successRate, String avgRating, String completionRate, String responseRate) {
        this.rank = rank;
        this.mentorName = mentorName;
        this.total = total;
        this.successRate = successRate;
        this.avgRating = avgRating;
        this.completionRate = completionRate;
        this.responseRate = responseRate;
    }

    public int getRank() { return rank; }
    public String getMentorName() { return mentorName; }
    public String getTotal() { return total; }
    public String getSuccessRate() { return successRate; }
    public String getAvgRating() { return avgRating; }
    public String getCompletionRate() { return completionRate; }
    public String getResponseRate() { return responseRate; }
}

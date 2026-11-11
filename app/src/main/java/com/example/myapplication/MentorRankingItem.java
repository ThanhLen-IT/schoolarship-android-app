package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;

public class MentorRankingItem implements Serializable {
    private Integer rank;

    @SerializedName("full_name")
    private String mentorName;

    @SerializedName("ranking_score")
    private Double total;

    @SerializedName("mentee_success_rate")
    private Double successRate;

    @SerializedName("average_rating")
    private Double avgRating;

    @SerializedName("mentoring_completion_rate")
    private Double completionRate;

    @SerializedName("response_rate")
    private Double responseRate;

    public int getRank() {
        return rank == null ? 0 : rank;
    }

    public String getMentorName() {
        return mentorName == null ? "" : mentorName;
    }

    private String percent(Double value) {
        return value == null ? "Chưa đủ dữ liệu" : String.format(Locale.ROOT, "%.1f%%", value);
    }

    public String getTotal() {
        return percent(total);
    }

    public String getSuccessRate() {
        return percent(successRate);
    }

    public String getAvgRating() {
        return avgRating == null
                ? "Chưa có đánh giá"
                : String.format(Locale.ROOT, "%.1f/5", avgRating);
    }

    public String getCompletionRate() {
        return percent(completionRate);
    }

    public String getResponseRate() {
        return percent(responseRate);
    }
}

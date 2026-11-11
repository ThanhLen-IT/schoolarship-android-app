package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Public scholarship response, shared by browsing, detail and bookmarks. */
public class Scholarship implements Serializable {
    private String id,
            title,
            organization,
            country,
            type,
            deadline,
            description,
            requirements,
            benefits;
    private List<String> majors;

    @SerializedName("study_levels")
    private List<String> studyLevels;

    @SerializedName("application_link")
    private String applicationLink;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("content_html")
    private String contentHtml;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_at")
    private String createdAt;

    private static String text(String value) {
        return value == null ? "" : value;
    }

    public String getId() {
        return text(id);
    }

    public String getTitle() {
        return text(title);
    }

    public String getOrganization() {
        return text(organization);
    }

    public String getCountry() {
        return text(country);
    }

    public String getDegreeLevel() {
        if (studyLevels == null || studyLevels.isEmpty()) return "Chưa rõ bậc học";
        java.util.ArrayList<String> labels = new java.util.ArrayList<>();
        for (String level : studyLevels) labels.add(levelLabel(level));
        return String.join(", ", labels);
    }

    static String levelLabel(String value) {
        if (value == null) return "Chưa rõ";
        switch (value) {
            case "bachelor":
                return "Cử nhân";
            case "master":
                return "Thạc sĩ";
            case "doctoral":
                return "Tiến sĩ";
            case "all":
                return "Mọi bậc học";
            case "other":
                return "Khác";
            case "unknown":
                return "Chưa rõ";
            default:
                return value;
        }
    }

    public String getFieldOfStudy() {
        return majors == null ? "" : String.join(", ", majors);
    }

    public String getFundingType() {
        return text(type);
    }

    public String getDeadlineStr() {
        Date date = getDeadlineDate();
        return date == null
                ? "Chưa công bố"
                : new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(date);
    }

    private static Date date(String value) {
        if (value == null || value.length() < 10) return null;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            parser.setLenient(false);
            return parser.parse(value.substring(0, 10));
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public Date getDeadlineDate() {
        return date(deadline);
    }

    public Date getUpdatedAt() {
        return date(updatedAt == null ? createdAt : updatedAt);
    }

    public String getApplicationLink() {
        return text(applicationLink);
    }

    public String getImageUrl() {
        return text(imageUrl);
    }

    public String getContentHtml() {
        return text(contentHtml);
    }

    public String getDescription() {
        return text(description);
    }

    public String getRequirements() {
        return text(requirements);
    }

    public String getBenefits() {
        return text(benefits);
    }
}

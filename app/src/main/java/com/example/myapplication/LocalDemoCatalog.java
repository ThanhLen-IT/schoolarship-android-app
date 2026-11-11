package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Local fixtures for UX review. IDs and scores do not represent backend records. */
public final class LocalDemoCatalog {
    public static final String LEVEL_EXCHANGE = "Trao đổi ngắn hạn";
    private LocalDemoCatalog() {}
    public static List<Scholarship> scholarships() {
        List<Scholarship> allScholarships = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        try {
            allScholarships.add(new Scholarship(
                    "1", "Global Excellence Scholarship", "ASEAN University Network", "Đông Nam Á",
                    "Thạc sĩ", "Công nghệ thông tin", "Toàn phần",
                    "17/10/2026", dateFormat.parse("17/10/2026"), dateFormat.parse("01/01/2026"), 100.0
            ));
            allScholarships.add(new Scholarship(
                    "2", "KGSP Korean Government Scholarship", "NIIED Korea", "Hàn Quốc",
                    "Thạc sĩ", "Khoa học xã hội", "Toàn phần",
                    "30/11/2026", dateFormat.parse("30/11/2026"), dateFormat.parse("05/01/2026"), 95.0
            ));
            allScholarships.add(new Scholarship(
                    "3", "MEXT Government Scholarship", "Japanese Government", "Nhật Bản",
                    "Thạc sĩ", "Kinh doanh", "Toàn phần",
                    "05/11/2026", dateFormat.parse("05/11/2026"), dateFormat.parse("10/01/2026"), 98.0
            ));
            allScholarships.add(new Scholarship(
                    "4", "Chinese Government Scholarship", "CSC China", "Trung Quốc",
                    "Thạc sĩ", "Khoa học", "Bán phần",
                    "15/12/2026", dateFormat.parse("15/12/2026"), dateFormat.parse("12/01/2026"), 80.0
            ));
            allScholarships.add(new Scholarship(
                    "5", "Taiwan ICDF Scholarship", "Taiwan ICDF", "Đài Loan",
                    "Đại học", "Y tế", "Toàn phần",
                    "20/10/2026", dateFormat.parse("20/10/2026"), dateFormat.parse("02/01/2026"), 90.0
            ));
            allScholarships.add(new Scholarship(
                    "6", "Chương trình trao đổi ngắn hạn (minh họa)", "Đối tác đại học (minh họa)", "Hàn Quốc",
                    LEVEL_EXCHANGE, "Công nghệ thông tin", "Bán phần",
                    "31/12/2026", dateFormat.parse("31/12/2026"), dateFormat.parse("01/11/2026"), 70.0
            ));
        } catch (ParseException e) {
            throw new IllegalStateException("Invalid local scholarship date", e);
        }
        return allScholarships;
    }


    public static Scholarship find(String id) {
        for (Scholarship s : scholarships()) if (s.getId().equals(id)) return s;
        return null;
    }

    public static List<MentorRankingItem> mentors(String scholarshipId) {
        List<MentorRankingItem> items = new ArrayList<>();
        switch (scholarshipId) {
            case "1":
                items.add(new MentorRankingItem(1, "Nguyen Kim Loan", "96.0%", "92%", "4.9/5", "96%", "98%"));
                items.add(new MentorRankingItem(2, "Minh An", "93.0%", "88%", "4.8/5", "93%", "95%"));
                break;
            case "2":
                items.add(new MentorRankingItem(1, "Ha Linh", "91.0%", "85%", "4.7/5", "91%", "94%"));
                break;
            case "3":
                items.add(new MentorRankingItem(1, "Tran Minh Hieu", "88.5%", "82%", "4.6/5", "89%", "91%"));
                break;
            case "4":
                items.add(new MentorRankingItem(1, "Le Thu Mai", "87.0%", "80%", "4.5/5", "88%", "90%"));
                break;
            default: // Scholarship 5 intentionally has no mentor for the empty state.
                break;
        }
        return items;
    }
}

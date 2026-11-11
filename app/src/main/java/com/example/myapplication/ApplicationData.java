package com.example.myapplication;

public class ApplicationData {
    public String id,
            mentor_id,
            mentee_id,
            mentor_name,
            mentee_name,
            scholarship_title,
            created_at,
            status,
            introduction,
            viewer_role;

    public String label() {
        if (status == null) return "Chưa rõ";
        switch (status) {
            case "pending":
                return "Chờ phản hồi";
            case "accepted":
                return "Đã chấp nhận";
            case "in_progress":
                return "Đang mentoring";
            case "completed":
                return "Đã hoàn thành";
            case "cancelled":
                return "Đã hủy do tài khoản đóng";
            case "rejected":
                return "Đã từ chối";
            default:
                return status;
        }
    }
}

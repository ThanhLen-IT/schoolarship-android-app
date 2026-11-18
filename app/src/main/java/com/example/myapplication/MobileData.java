package com.example.myapplication;

import java.util.List;

public final class MobileData {
    public static class Filters {
        public List<String> countries, types, majors, study_levels;
    }

    public static class Conversation {
        public String id, peer_id, peer_name, avatar_url, last_message, last_message_at;
        public int unread_count;
    }

    public static class Message {
        public String id, sender_id, message, created_at, client_message_id;
        public boolean is_read;
    }

    public static class Notice {
        public String id, title, content, type, reference_id, created_at;
        public boolean is_read;
    }

    public static class Ticket {
        public String id, subject, message, status, admin_response, created_at;
    }

    public static class Avatar {
        public String avatar_url;
    }

    public static class Mentor {
        public UserProfile mentor;
    }

    public static class Request {
        public String id, request_note, requested_at, responded_at, response_note;
    }
}

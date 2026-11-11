package com.example.myapplication;

public class ChatDetailActivity extends UnavailableActivity {
    public static final String EXTRA_CHAT_NAME = "extra_chat_name",
            EXTRA_CHAT_AVATAR = "extra_chat_avatar";

    @Override
    protected String feature() {
        return "Trò chuyện";
    }
}

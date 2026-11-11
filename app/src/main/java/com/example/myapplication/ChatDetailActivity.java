package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_NAME = "extra_chat_name";
    public static final String EXTRA_CHAT_AVATAR = "extra_chat_avatar";

    private List<ChatMessageItem> messageList;
    private ChatMessageAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

                return insets;
            });
        }

        // Back Button
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Partner Details
        String chatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        int avatarResId = getIntent().getIntExtra(EXTRA_CHAT_AVATAR, R.drawable.avata_nu);

        TextView tvName = findViewById(R.id.tv_chat_name);
        ImageView ivAvatar = findViewById(R.id.iv_chat_avatar);

        if (tvName != null && !TextUtils.isEmpty(chatName)) {
            tvName.setText(chatName);
        }
        if (ivAvatar != null && avatarResId != 0) {
            ivAvatar.setImageResource(avatarResId);
        }

        // Load Conversation Data
        loadSampleConversation(avatarResId);

        recyclerView = findViewById(R.id.message_list);
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ChatMessageAdapter(messageList);
            recyclerView.setAdapter(adapter);
        }

        // Send Button
        etMessage = findViewById(R.id.et_message);
        ImageButton btnSend = findViewById(R.id.btn_send);
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> sendMessage());
        }
    }

    private void loadSampleConversation(int avatarResId) {
        messageList = new ArrayList<>();

        messageList.add(new ChatMessageItem("1", ChatMessageItem.TYPE_INCOMING, "Japan looks amazing!", "10:58 AM", avatarResId));
        messageList.add(new ChatMessageItem("2", ChatMessageItem.TYPE_OUTGOING, "Do you know what time is it?", "10:59 AM", 0));
        messageList.add(new ChatMessageItem("3", ChatMessageItem.TYPE_INCOMING, "Good morning!", "11:00 AM", avatarResId));
        messageList.add(new ChatMessageItem("4", ChatMessageItem.TYPE_OUTGOING, "It's morning in Tokyo", "11:01 AM", 0));
        messageList.add(new ChatMessageItem("5", ChatMessageItem.TYPE_INCOMING, "What is the most popular meal in Japan?", "11:02 AM", avatarResId));
        messageList.add(new ChatMessageItem("6", ChatMessageItem.TYPE_INCOMING, "Do you like it?", "11:03 AM", avatarResId));
        messageList.add(new ChatMessageItem("7", ChatMessageItem.TYPE_OUTGOING, "I think top two are:", "11:04 AM", 0));

        // Image Attachments
        messageList.add(new ChatMessageItem("8", ChatMessageItem.TYPE_ATTACHMENT, "IMG_0475.PNG", "2.4 MB", "11:05 AM", 0));
        messageList.add(new ChatMessageItem("9", ChatMessageItem.TYPE_ATTACHMENT, "IMG_0481.PNG", "2.8 MB", "11:05 AM", 0));
        messageList.add(new ChatMessageItem("10", ChatMessageItem.TYPE_ATTACHMENT, "IMG_0483.PNG", "2.8 MB", "11:05 AM", 0));
        messageList.add(new ChatMessageItem("11", ChatMessageItem.TYPE_ATTACHMENT, "IMG_0484.PNG", "2.6 MB", "11:05 AM", 0));
    }

    private void sendMessage() {
        if (etMessage == null) return;

        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeStr = timeFormat.format(new Date());

        messageList.add(new ChatMessageItem(
                String.valueOf(System.currentTimeMillis()),
                ChatMessageItem.TYPE_OUTGOING,
                text,
                timeStr,
                0
        ));

        if (adapter != null) {
            adapter.notifyItemInserted(messageList.size() - 1);
        }

        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        }

        etMessage.setText("");
        Toast.makeText(this, "Tin nhắn đã được gửi cục bộ", Toast.LENGTH_SHORT).show();
    }
}

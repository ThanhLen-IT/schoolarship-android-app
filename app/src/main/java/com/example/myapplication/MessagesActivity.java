package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private List<ContactAvatarItem> contactList;
    private List<ConversationItem> conversationList;
    private List<ConversationItem> filteredConversations;
    private ConversationAdapter conversationAdapter;

    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages);

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

        // Initialize Data
        loadContacts();
        loadConversations();

        // Contact Avatars RecyclerView (Horizontal)
        RecyclerView recyclerContacts = findViewById(R.id.recycler_contact_avatars);
        if (recyclerContacts != null) {
            recyclerContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ContactAvatarAdapter contactAdapter = new ContactAvatarAdapter(contactList);
            recyclerContacts.setAdapter(contactAdapter);
        }

        // Conversations RecyclerView (Vertical)
        filteredConversations = new ArrayList<>(conversationList);
        RecyclerView recyclerConversations = findViewById(R.id.recycler_conversations);
        if (recyclerConversations != null) {
            recyclerConversations.setLayoutManager(new LinearLayoutManager(this));
            conversationAdapter = new ConversationAdapter(filteredConversations);
            recyclerConversations.setAdapter(conversationAdapter);
        }

        // Search Input
        EditText etSearch = findViewById(R.id.et_messages_search);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().trim();
                    filterConversations();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void loadContacts() {
        contactList = new ArrayList<>();
        contactList.add(new ContactAvatarItem("1", "Tâm Anh", R.drawable.avata_nam, true));
        contactList.add(new ContactAvatarItem("2", "DABI's", R.drawable.avata_nu, true));
        contactList.add(new ContactAvatarItem("3", "Kim Ngân", R.drawable.avata_nam, false));
        contactList.add(new ContactAvatarItem("4", "Lên", R.drawable.avata_nu, true));
        contactList.add(new ContactAvatarItem("5", "Thu Trâm", R.drawable.avata_nam, false));
    }

    private void loadConversations() {
        conversationList = new ArrayList<>();
        conversationList.add(new ConversationItem(
                "1",
                "Tâm Anh",
                "Bạn: Chúc em thành công.",
                "10:58",
                R.drawable.avata_nam,
                false
        ));
        conversationList.add(new ConversationItem(
                "2",
                "DABI's",
                "DABI's đã gửi một file đính kèm.",
                "10:28",
                R.drawable.avata_nu,
                false
        ));
        conversationList.add(new ConversationItem(
                "3",
                "Kim Ngân",
                "2 tin nhắn mới",
                "09:14",
                R.drawable.avata_nam,
                true
        ));
        conversationList.add(new ConversationItem(
                "4",
                "Lên",
                "526 tin nhắn mới",
                "T.5",
                R.drawable.avata_nu,
                true
        ));
        conversationList.add(new ConversationItem(
                "5",
                "Thu Trâm",
                "Đã bày tỏ cảm xúc ❤️ về tin nhắn của...",
                "T.5",
                R.drawable.avata_nam,
                false
        ));
        conversationList.add(new ConversationItem(
                "6",
                "Thu Vo",
                "Ok bé",
                "T.4",
                R.drawable.avata_nu,
                false
        ));
    }

    private void filterConversations() {
        filteredConversations.clear();

        for (ConversationItem item : conversationList) {
            if (!TextUtils.isEmpty(currentSearchQuery)) {
                boolean matchesName = item.getName().toLowerCase().contains(currentSearchQuery.toLowerCase());
                boolean matchesPreview = item.getPreview().toLowerCase().contains(currentSearchQuery.toLowerCase());
                if (!matchesName && !matchesPreview) {
                    continue;
                }
            }
            filteredConversations.add(item);
        }

        if (conversationAdapter != null) {
            conversationAdapter.notifyDataSetChanged();
        }
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.nav_home);
        View navRanking = findViewById(R.id.nav_mentor_ranking);
        View navCommunity = findViewById(R.id.nav_community);
        View navMessages = findViewById(R.id.nav_messages);
        View navProfile = findViewById(R.id.nav_profile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(MessagesActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(v -> {
                Intent intent = new Intent(MessagesActivity.this, MentorRankingActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(MessagesActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> Toast.makeText(this, "Already in Messages", Toast.LENGTH_SHORT).show());
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MessagesActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}

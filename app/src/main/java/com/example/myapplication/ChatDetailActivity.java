package com.example.myapplication;

import android.os.*;
import android.widget.*;

import java.util.*;

public class ChatDetailActivity extends androidx.appcompat.app.AppCompatActivity {
    public static final String EXTRA_CHAT_NAME = "extra_chat_name",
            EXTRA_CHAT_AVATAR = "extra_chat_avatar";
    private MobileScreen ui;
    private String cid, self, latest, oldest, pendingKey;
    private boolean loading, older = true, sending, active;
    private EditText input;
    private Button send;
    private final List<MobileData.Message> items = new ArrayList<>();
    private final Handler poll = new Handler(Looper.getMainLooper());
    private retrofit2.Call<?> activeCall;
    private final Runnable refresh = () -> load(false);

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        cid = getIntent().getStringExtra("conversation_id");
        ui =
                new MobileScreen(
                        this,
                        getIntent().getStringExtra("peer_name") == null
                                ? "Trò chuyện"
                                : getIntent().getStringExtra("peer_name"));
        if (cid == null) {
            finish();
            return;
        }
        input = new EditText(this);
        input.setHint("Nhập tin nhắn…");
        input.setFilters(
                new android.text.InputFilter[] {new android.text.InputFilter.LengthFilter(4000)});
        ui.footer.addView(input);
        send = ui.button(ui.footer, "Gửi", () -> send());
        if (b != null) {
            pendingKey = b.getString("pending_key");
            input.setText(b.getString("draft", ""));
            if (pendingKey != null) input.setEnabled(false);
        }
        ApiUi.request(
                this,
                ApiClient.getApiService().getCurrentUser(ApiUi.bearer(this)),
                r -> {
                    if (r.getData() != null) {
                        self = r.getData().getId();
                        render(false);
                    }
                },
                m -> ApiUi.error(this, m));
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        if (ApiUi.requireLogin(this)) load(false);
    }

    private void load(boolean before) {
        if (loading || !active) return;
        loading = true;
        if (items.isEmpty()) ui.loading();
        String cursor = before ? oldest : null;
        activeCall =
                ApiUi.request(
                        this,
                        ApiClient.getApiService()
                                .messages(
                                        ApiUi.bearer(this),
                                        cid,
                                        cursor,
                                        before ? null : latest,
                                        50),
                        r -> {
                            loading = false;
                            List<MobileData.Message> data =
                                    r.getData() == null ? Collections.emptyList() : r.getData();
                            boolean initial = items.isEmpty();
                            Set<String> ids = new HashSet<>();
                            for (MobileData.Message m : items) ids.add(m.id);
                            List<MobileData.Message> fresh = new ArrayList<>();
                            for (MobileData.Message m : data) if (ids.add(m.id)) fresh.add(m);
                            if (before) items.addAll(0, fresh);
                            else items.addAll(fresh);
                            if (before || initial) older = r.hasMore();
                            if (!items.isEmpty()) {
                                oldest = items.get(0).id;
                                latest = items.get(items.size() - 1).id;
                                ApiUi.request(
                                        this,
                                        ApiClient.getApiService()
                                                .readConversation(
                                                        ApiUi.bearer(this),
                                                        cid,
                                                        MobileForms.body("through_id", latest)),
                                        x -> {},
                                        m -> {});
                            }
                            for (MobileData.Message message : items) {
                                if (!sending
                                        && pendingKey != null
                                        && pendingKey.equals(message.client_message_id)) {
                                    pendingKey = null;
                                    input.setText("");
                                    input.setEnabled(true);
                                    break;
                                }
                            }
                            render(!before && !fresh.isEmpty());
                            poll.removeCallbacks(refresh);
                            if (active)
                                poll.postDelayed(
                                        refresh, !before && !initial && r.hasMore() ? 0 : 5000);
                        },
                        m -> {
                            loading = false;
                            ui.failed(m, () -> load(false));
                            poll.removeCallbacks(refresh);
                            if (active) poll.postDelayed(refresh, 10000);
                        });
    }

    private void render(boolean bottom) {
        ui.rows.removeAllViews();
        if (older && !items.isEmpty()) ui.button(ui.rows, "Tin nhắn cũ hơn", () -> load(true));
        for (MobileData.Message m : items) {
            LinearLayout row = ui.card();
            ui.text(row, Objects.equals(self, m.sender_id) ? "Bạn" : "Người đối thoại", 12);
            ui.text(row, m.message, 16);
            ui.text(row, m.created_at, 11);
        }
        ui.ready(items.isEmpty());
        if (bottom) ui.scroll.post(() -> ui.scroll.fullScroll(android.view.View.FOCUS_DOWN));
    }

    private void send() {
        String value = input.getText().toString().trim();
        if (value.isEmpty() || sending) return;
        if (pendingKey == null) pendingKey = UUID.randomUUID().toString();
        Map<String, String> body = MobileForms.body("message", value);
        body.put("client_message_id", pendingKey);
        sending = true;
        send.setEnabled(false);
        input.setEnabled(false);
        ApiUi.request(
                this,
                ApiClient.getApiService().sendMessage(ApiUi.bearer(this), cid, body),
                r -> {
                    sending = false;
                    send.setEnabled(true);
                    input.setEnabled(true);
                    input.setText("");
                    pendingKey = null;
                    load(false);
                },
                m -> {
                    sending = false;
                    send.setEnabled(true);
                    input.setEnabled(false);
                    ui.failed(m, () -> send());
                });
    }

    @Override
    protected void onStop() {
        active = false;
        poll.removeCallbacksAndMessages(null);
        if (activeCall != null) activeCall.cancel();
        loading = false;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        b.putString("draft", input.getText().toString());
        b.putString("pending_key", pendingKey);
        super.onSaveInstanceState(b);
    }
}

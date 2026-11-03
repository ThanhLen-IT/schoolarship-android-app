package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationItem> notifications;

    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvTimestamp.setText(item.getTimestamp());
        holder.ivIcon.setImageResource(item.getIconResId());

        holder.viewUnreadDot.setVisibility(item.isRead() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            item.setRead(true);
            holder.viewUnreadDot.setVisibility(View.GONE);
            Toast.makeText(v.getContext(), "Mở thông báo", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvTimestamp;
        View viewUnreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notif_icon);
            tvTitle = itemView.findViewById(R.id.tv_notif_title);
            tvTimestamp = itemView.findViewById(R.id.tv_notif_timestamp);
            viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);
        }
    }
}

package com.example.myapplication;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private final List<ConversationItem> items;

    public ConversationAdapter(List<ConversationItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationItem item = items.get(position);

        holder.ivAvatar.setImageResource(item.getAvatarResId());
        holder.tvName.setText(item.getName());
        holder.tvPreview.setText(item.getPreview());
        holder.tvTime.setText(item.getTimestamp());

        if (item.isUnread()) {
            holder.tvPreview.setTypeface(null, Typeface.BOLD);
            holder.tvPreview.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_text_primary));
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
        } else {
            holder.tvPreview.setTypeface(null, Typeface.NORMAL);
            holder.tvPreview.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_text_hint));
            holder.viewUnreadDot.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ChatDetailActivity.class);
            intent.putExtra(ChatDetailActivity.EXTRA_CHAT_NAME, item.getName());
            intent.putExtra(ChatDetailActivity.EXTRA_CHAT_AVATAR, item.getAvatarResId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvPreview, tvTime;
        View viewUnreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_conv_avatar);
            tvName = itemView.findViewById(R.id.tv_conv_name);
            tvPreview = itemView.findViewById(R.id.tv_conv_preview);
            tvTime = itemView.findViewById(R.id.tv_conv_time);
            viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);
        }
    }
}

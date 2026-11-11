package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessageItem> messages;

    public ChatMessageAdapter(List<ChatMessageItem> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessageItem.TYPE_INCOMING) {
            View view = inflater.inflate(R.layout.item_chat_incoming, parent, false);
            return new IncomingViewHolder(view);
        } else if (viewType == ChatMessageItem.TYPE_OUTGOING) {
            View view = inflater.inflate(R.layout.item_chat_outgoing, parent, false);
            return new OutgoingViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_attachment, parent, false);
            return new AttachmentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageItem item = messages.get(position);

        if (holder instanceof IncomingViewHolder) {
            IncomingViewHolder vh = (IncomingViewHolder) holder;
            vh.tvText.setText(item.getText());
            vh.tvTime.setText(item.getTimestamp());
            if (item.getAvatarResId() != 0) {
                vh.ivAvatar.setImageResource(item.getAvatarResId());
            }
        } else if (holder instanceof OutgoingViewHolder) {
            OutgoingViewHolder vh = (OutgoingViewHolder) holder;
            vh.tvText.setText(item.getText());
            vh.tvTime.setText(item.getTimestamp());
        } else if (holder instanceof AttachmentViewHolder) {
            AttachmentViewHolder vh = (AttachmentViewHolder) holder;
            vh.tvFileName.setText(item.getFileName());
            vh.tvFileSize.setText(item.getFileSize());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class IncomingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvText, tvTime;

        IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_sender_avatar);
            tvText = itemView.findViewById(R.id.tv_message_text);
            tvTime = itemView.findViewById(R.id.tv_message_time);
        }
    }

    static class OutgoingViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;

        OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_message_text);
            tvTime = itemView.findViewById(R.id.tv_message_time);
        }
    }

    static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        TextView tvFileName, tvFileSize;

        AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPreview = itemView.findViewById(R.id.iv_attachment_preview);
            tvFileName = itemView.findViewById(R.id.tv_attachment_name);
            tvFileSize = itemView.findViewById(R.id.tv_attachment_size);
        }
    }
}

package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenteeApplicationAdapter extends RecyclerView.Adapter<MenteeApplicationAdapter.ViewHolder> {

    public interface OnApplicationActionListener {
        void onRejectClick(MenteeApplication application, int position);
        void onApproveClick(MenteeApplication application, int position);
    }

    private final List<MenteeApplication> items;
    private final OnApplicationActionListener listener;

    public MenteeApplicationAdapter(List<MenteeApplication> items, OnApplicationActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentee_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenteeApplication item = items.get(position);

        holder.tvInitials.setText(item.getInitials());
        holder.tvName.setText(item.getFullName());
        holder.tvUniversity.setText(item.getUniversity());
        holder.tvScholarshipPill.setText("🎓 " + item.getScholarshipName());
        holder.tvDescription.setText(item.getDescription());

        // Status Badge Styling
        if (item.getStatus() == MenteeApplication.Status.PENDING) {
            holder.tvStatusBadge.setText("Chờ duyệt");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_primary));
            holder.layoutCardActions.setVisibility(View.VISIBLE);
        } else if (item.getStatus() == MenteeApplication.Status.APPROVED) {
            holder.tvStatusBadge.setText("Đã nhận");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_approved);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            holder.layoutCardActions.setVisibility(View.GONE);
        } else {
            holder.tvStatusBadge.setText("Từ chối");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_rejected);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_text_hint));
            holder.layoutCardActions.setVisibility(View.GONE);
        }

        holder.btnViewFullProfile.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PersonalProfileActivity.class);
            v.getContext().startActivity(intent);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onRejectClick(item, pos);
                }
            }
        });

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onApproveClick(item, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvUniversity, tvStatusBadge, tvScholarshipPill, tvDescription, btnViewFullProfile;
        View layoutCardActions;
        Button btnReject, btnApprove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tv_avatar_initials);
            tvName = itemView.findViewById(R.id.tv_mentee_name);
            tvUniversity = itemView.findViewById(R.id.tv_mentee_university);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvScholarshipPill = itemView.findViewById(R.id.tv_scholarship_pill);
            tvDescription = itemView.findViewById(R.id.tv_mentee_description);
            btnViewFullProfile = itemView.findViewById(R.id.btn_view_full_profile);
            layoutCardActions = itemView.findViewById(R.id.layout_card_actions);
            btnReject = itemView.findViewById(R.id.btn_card_reject);
            btnApprove = itemView.findViewById(R.id.btn_card_approve);
        }
    }
}

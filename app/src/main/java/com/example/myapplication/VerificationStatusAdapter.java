package com.example.myapplication;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VerificationStatusAdapter extends RecyclerView.Adapter<VerificationStatusAdapter.ViewHolder> {

    private final List<VerificationStatusRecord> records;

    public VerificationStatusAdapter(List<VerificationStatusRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verification_status_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VerificationStatusRecord record = records.get(position);

        holder.tvTitle.setText(record.getTitle());
        holder.tvInstitution.setText(record.getInstitution());
        holder.tvDateNote.setText(record.getDateOrNote());

        String status = record.getStatus();
        holder.tvStatusLabel.setText(status);

        GradientDrawable badgeBackground = new GradientDrawable();
        badgeBackground.setCornerRadius(16);

        if ("Approved".equalsIgnoreCase(status)) {
            badgeBackground.setColor(Color.parseColor("#DCFCE7"));
            holder.layoutBadge.setBackground(badgeBackground);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#15803D"));
            holder.ivStatusIcon.setImageResource(R.drawable.ic_successmark);
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#15803D"), PorterDuff.Mode.SRC_IN);
        } else if ("Pending Review".equalsIgnoreCase(status) || "Pending verification".equalsIgnoreCase(status)) {
            badgeBackground.setColor(Color.parseColor("#FEF3C7"));
            holder.layoutBadge.setBackground(badgeBackground);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#B45309"));
            holder.ivStatusIcon.setImageResource(R.drawable.ic_calendar);
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#B45309"), PorterDuff.Mode.SRC_IN);
        } else if ("Failed / Rejected".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
            badgeBackground.setColor(Color.parseColor("#FEE2E2"));
            holder.layoutBadge.setBackground(badgeBackground);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#B91C1C"));
            holder.ivStatusIcon.setImageResource(R.drawable.ic_star);
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#B91C1C"), PorterDuff.Mode.SRC_IN);
        } else {
            badgeBackground.setColor(Color.parseColor("#F1F5F9"));
            holder.layoutBadge.setBackground(badgeBackground);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#64748B"));
            holder.ivStatusIcon.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInstitution, tvStatusLabel, tvDateNote;
        ImageView ivStatusIcon;
        View layoutBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_record_title);
            tvInstitution = itemView.findViewById(R.id.tv_record_institution);
            tvStatusLabel = itemView.findViewById(R.id.tv_status_label);
            tvDateNote = itemView.findViewById(R.id.tv_record_date_note);
            ivStatusIcon = itemView.findViewById(R.id.iv_status_icon);
            layoutBadge = itemView.findViewById(R.id.layout_status_badge);
        }
    }
}

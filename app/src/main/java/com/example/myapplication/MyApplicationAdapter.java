package com.example.myapplication;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyApplicationAdapter extends RecyclerView.Adapter<MyApplicationAdapter.ViewHolder> {

    private final List<MyApplicationItem> items;

    public MyApplicationAdapter(List<MyApplicationItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyApplicationItem item = items.get(position);

        holder.ivAvatar.setImageResource(item.getAvatarResId());
        holder.tvMentorName.setText(item.getMentorName());
        holder.tvOrg.setText(item.getOrganization());
        holder.tvDate.setText(item.getApplicationDate());
        holder.tvObjective.setText(item.getObjective());

        String status = item.getStatus();
        holder.tvStatusLabel.setText(status);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setCornerRadius(16);

        if ("Đã được nhận".equalsIgnoreCase(status)) {
            badgeBg.setColor(Color.parseColor("#DCFCE7"));
            holder.layoutBadge.setBackground(badgeBg);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#15803D"));
            holder.btnPrimaryAction.setVisibility(View.VISIBLE);
            holder.btnPrimaryAction.setText(R.string.action_start_chat);
        } else if ("Đang chờ phản hồi".equalsIgnoreCase(status)) {
            badgeBg.setColor(Color.parseColor("#FEF3C7"));
            holder.layoutBadge.setBackground(badgeBg);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#B45309"));
            holder.btnPrimaryAction.setVisibility(View.GONE);
        } else {
            // "Chưa phù hợp"
            badgeBg.setColor(Color.parseColor("#FEE2E2"));
            holder.layoutBadge.setBackground(badgeBg);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#B91C1C"));
            holder.btnPrimaryAction.setVisibility(View.GONE);
        }

        holder.btnPrimaryAction.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Bắt đầu trò chuyện với " + item.getMentorName(), Toast.LENGTH_SHORT).show()
        );

        holder.tvViewDetails.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Xem chi tiết đơn của " + item.getMentorName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvMentorName, tvOrg, tvDate, tvStatusLabel, tvObjective, tvViewDetails;
        Button btnPrimaryAction;
        View layoutBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_mentor_avatar);
            tvMentorName = itemView.findViewById(R.id.tv_mentor_name);
            tvOrg = itemView.findViewById(R.id.tv_mentor_org);
            tvDate = itemView.findViewById(R.id.tv_application_date);
            tvStatusLabel = itemView.findViewById(R.id.tv_app_status_label);
            tvObjective = itemView.findViewById(R.id.tv_application_objective);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
            btnPrimaryAction = itemView.findViewById(R.id.btn_primary_action);
            layoutBadge = itemView.findViewById(R.id.layout_app_status_badge);
        }
    }
}

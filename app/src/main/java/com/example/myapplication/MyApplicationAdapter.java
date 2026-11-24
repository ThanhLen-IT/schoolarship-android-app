package com.example.myapplication;

import android.content.Intent;
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
            // Trường hợp Đã được nhận: HIỆN cả 2 nút "Bắt đầu trò chuyện" và "Đánh giá Mentor"
            badgeBg.setColor(Color.parseColor("#DCFCE7"));
            holder.layoutBadge.setBackground(badgeBg);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#15803D"));
            
            holder.btnPrimaryAction.setVisibility(View.VISIBLE);
            holder.btnPrimaryAction.setText(R.string.action_start_chat);
            
            if (holder.btnRateMentor != null) {
                holder.btnRateMentor.setVisibility(View.VISIBLE);
            }
        } else if ("Đang chờ phản hồi".equalsIgnoreCase(status)) {
            // Trường hợp Đang chờ: ẨN cả 2 nút
            badgeBg.setColor(Color.parseColor("#FEF3C7"));
            holder.layoutBadge.setBackground(badgeBg);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#B45309"));

            holder.btnPrimaryAction.setVisibility(View.GONE);
            if (holder.btnRateMentor != null) {
                holder.btnRateMentor.setVisibility(View.GONE);
            }
        } else {
            // Trường hợp Từ chối / Chưa phù hợp: ẨN cả 2 nút
            badgeBg.setColor(Color.parseColor("#FEE2E2"));
            holder.layoutBadge.setBackground(badgeBg);
            holder.tvStatusLabel.setTextColor(Color.parseColor("#B91C1C"));

            holder.btnPrimaryAction.setVisibility(View.GONE);
            if (holder.btnRateMentor != null) {
                holder.btnRateMentor.setVisibility(View.GONE);
            }
        }

        // Nhấn vào Avatar hoặc Tên Mentor -> Mở trang Hồ sơ cá nhân (PersonalProfileActivity)
        View.OnClickListener openProfileListener = v -> {
            Intent intent = new Intent(v.getContext(), PersonalProfileActivity.class);
            v.getContext().startActivity(intent);
        };
        holder.ivAvatar.setOnClickListener(openProfileListener);
        holder.tvMentorName.setOnClickListener(openProfileListener);

        // Action Bắt đầu trò chuyện -> Dẫn tới màn hình ChatDetailActivity
        holder.btnPrimaryAction.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatDetailActivity.class);
            intent.putExtra(ChatDetailActivity.EXTRA_CHAT_NAME, item.getMentorName());
            intent.putExtra(ChatDetailActivity.EXTRA_CHAT_AVATAR, item.getAvatarResId());
            v.getContext().startActivity(intent);
        });

        // Action Đánh giá Mentor
        if (holder.btnRateMentor != null) {
            holder.btnRateMentor.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MentorReviewActivity.class);
                intent.putExtra(MentorReviewActivity.EXTRA_MENTOR_NAME, item.getMentorName());
                intent.putExtra(MentorReviewActivity.EXTRA_SCHOLARSHIP_NAME, "Học bổng Chevening");
                intent.putExtra(MentorReviewActivity.EXTRA_MENTOR_SERVICES, "Sửa luận, Mock interview");
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvMentorName, tvOrg, tvDate, tvStatusLabel, tvObjective;
        Button btnPrimaryAction, btnRateMentor;
        View layoutBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_mentor_avatar);
            tvMentorName = itemView.findViewById(R.id.tv_mentor_name);
            tvOrg = itemView.findViewById(R.id.tv_mentor_org);
            tvDate = itemView.findViewById(R.id.tv_application_date);
            tvStatusLabel = itemView.findViewById(R.id.tv_app_status_label);
            tvObjective = itemView.findViewById(R.id.tv_application_objective);
            btnPrimaryAction = itemView.findViewById(R.id.btn_primary_action);
            btnRateMentor = itemView.findViewById(R.id.btn_rate_mentor);
            layoutBadge = itemView.findViewById(R.id.layout_app_status_badge);
        }
    }
}

package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScholarshipResultAdapter
        extends RecyclerView.Adapter<ScholarshipResultAdapter.ViewHolder> {

    private final List<Scholarship> scholarshipList;
    private java.util.function.Consumer<String> remove;

    public ScholarshipResultAdapter(List<Scholarship> scholarshipList) {
        this.scholarshipList = scholarshipList;
    }

    public ScholarshipResultAdapter(
            List<Scholarship> items, java.util.function.Consumer<String> remove) {
        this(items);
        this.remove = remove;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_scholarship_result_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scholarship scholarship = scholarshipList.get(position);
        holder.tvName.setText(scholarship.getTitle());
        holder.tvCategory.setText(
                scholarship.getDegreeLevel() + " · " + scholarship.getFieldOfStudy());
        holder.tvDeadline.setText("Hạn: " + scholarship.getDeadlineStr());

        RemoteImage.show(holder.ivLogo, scholarship.getImageUrl());
        holder.btnFollow.setText(remove == null ? "Lưu" : "Bỏ lưu");
        holder.btnFollow.setOnClickListener(
                v -> {
                    if (remove != null) {
                        remove.accept(scholarship.getId());
                        return;
                    }
                    android.content.Context context = v.getContext();
                    while (context instanceof android.content.ContextWrapper
                            && !(context instanceof android.app.Activity))
                        context = ((android.content.ContextWrapper) context).getBaseContext();
                    if (context instanceof android.app.Activity)
                        ApiUi.saveScholarship(
                                (android.app.Activity) context, scholarship.getId(), null);
                });
        holder.tvFindMentor.setOnClickListener(
                v -> {
                    android.content.Intent intent =
                            new android.content.Intent(v.getContext(), MentorRankingActivity.class);
                    intent.putExtra(
                            MentorRankingActivity.EXTRA_SCHOLARSHIP_ID, scholarship.getId());
                    v.getContext().startActivity(intent);
                });

        holder.itemView.setOnClickListener(
                v -> {
                    android.content.Intent intent =
                            new android.content.Intent(
                                    v.getContext(), ScholarshipDetailActivity.class);
                    intent.putExtra(
                            ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_ID, scholarship.getId());
                    intent.putExtra(
                            ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_TITLE,
                            scholarship.getTitle());
                    intent.putExtra(
                            ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_CATEGORY,
                            scholarship.getFieldOfStudy());
                    v.getContext().startActivity(intent);
                });
    }

    @Override
    public int getItemCount() {
        return scholarshipList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName, tvCategory, tvDeadline, btnFollow, tvFindMentor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.iv_scholarship_logo);
            tvName = itemView.findViewById(R.id.tv_scholarship_name);
            tvCategory = itemView.findViewById(R.id.tv_scholarship_category);
            tvDeadline = itemView.findViewById(R.id.tv_scholarship_deadline);
            btnFollow = itemView.findViewById(R.id.btn_follow);
            tvFindMentor = itemView.findViewById(R.id.tv_find_mentor);
        }
    }
}

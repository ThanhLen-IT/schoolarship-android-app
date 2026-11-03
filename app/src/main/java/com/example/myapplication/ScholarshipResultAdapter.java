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

public class ScholarshipResultAdapter extends RecyclerView.Adapter<ScholarshipResultAdapter.ViewHolder> {

    private final List<Scholarship> scholarshipList;

    public ScholarshipResultAdapter(List<Scholarship> scholarshipList) {
        this.scholarshipList = scholarshipList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scholarship_result_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scholarship scholarship = scholarshipList.get(position);
        holder.tvName.setText(scholarship.getTitle());
        holder.tvCategory.setText(scholarship.getDegreeLevel() + " · " + scholarship.getFieldOfStudy());
        holder.tvDeadline.setText("Hạn: " + scholarship.getDeadlineStr());

        holder.btnFollow.setOnClickListener(v -> Toast.makeText(v.getContext(), "Followed " + scholarship.getTitle(), Toast.LENGTH_SHORT).show());
        holder.tvFindMentor.setOnClickListener(v -> Toast.makeText(v.getContext(), "Find Mentor for " + scholarship.getTitle(), Toast.LENGTH_SHORT).show());

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ScholarshipDetailActivity.class);
            intent.putExtra(ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_TITLE, scholarship.getTitle());
            intent.putExtra(ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_CATEGORY, scholarship.getFieldOfStudy());
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

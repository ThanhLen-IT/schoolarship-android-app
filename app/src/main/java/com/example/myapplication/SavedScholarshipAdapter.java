package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedScholarshipAdapter extends RecyclerView.Adapter<SavedScholarshipAdapter.ViewHolder> {

    private final List<SavedScholarshipItem> items;

    public SavedScholarshipAdapter(List<SavedScholarshipItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_scholarship_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedScholarshipItem item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvOrg.setText(item.getOrganization());
        holder.tvDeadline.setText(item.getDeadline());
        holder.tvFollowStatus.setText(item.getFollowStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ScholarshipDetailActivity.class);
            intent.putExtra(ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_TITLE, item.getTitle());
            intent.putExtra(ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_CATEGORY, item.getOrganization());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOrg, tvDeadline, tvFollowStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_saved_title);
            tvOrg = itemView.findViewById(R.id.tv_saved_org);
            tvDeadline = itemView.findViewById(R.id.tv_saved_deadline);
            tvFollowStatus = itemView.findViewById(R.id.tv_follow_status);
        }
    }
}

package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MentorRankingAdapter extends RecyclerView.Adapter<MentorRankingAdapter.ViewHolder> {

    private final List<MentorRankingItem> items;

    public MentorRankingAdapter(List<MentorRankingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentor_ranking_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MentorRankingItem item = items.get(position);

        holder.tvRank.setText(String.valueOf(item.getRank()));
        holder.tvMentorName.setText(item.getMentorName());
        holder.tvSuccessRate.setText(item.getSuccessRate());
        holder.tvAvgRating.setText(item.getAvgRating());
        holder.tvCompletionRate.setText(item.getCompletionRate());
        holder.tvResponseRate.setText(item.getResponseRate());
        holder.tvExperience.setText(item.getExperience());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvMentorName, tvSuccessRate, tvAvgRating, tvCompletionRate, tvResponseRate, tvExperience;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvMentorName = itemView.findViewById(R.id.tv_mentor_name);
            tvSuccessRate = itemView.findViewById(R.id.tv_success_rate);
            tvAvgRating = itemView.findViewById(R.id.tv_avg_rating);
            tvCompletionRate = itemView.findViewById(R.id.tv_completion_rate);
            tvResponseRate = itemView.findViewById(R.id.tv_response_rate);
            tvExperience = itemView.findViewById(R.id.tv_experience);
        }
    }
}

package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScholarshipVerifyAdapter extends RecyclerView.Adapter<ScholarshipVerifyAdapter.ViewHolder> {

    private final List<ScholarshipVerifyItem> items;

    public ScholarshipVerifyAdapter(List<ScholarshipVerifyItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scholarship_verify_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScholarshipVerifyItem item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvInstitution.setText(item.getInstitution());

        String status = item.getVerificationStatus();
        if ("Verified".equalsIgnoreCase(status)) {
            holder.btnVerify.setText(R.string.status_verified);
            holder.btnVerify.setEnabled(false);
            holder.btnVerify.setAlpha(0.6f);
        } else if ("Pending verification".equalsIgnoreCase(status)) {
            holder.btnVerify.setText(R.string.status_pending);
            holder.btnVerify.setEnabled(false);
            holder.btnVerify.setAlpha(0.8f);
        } else {
            holder.btnVerify.setText(R.string.verify_this_button);
            holder.btnVerify.setEnabled(true);
            holder.btnVerify.setAlpha(1.0f);
        }

        holder.btnVerify.setOnClickListener(v -> {
            if ("Not verified".equalsIgnoreCase(item.getVerificationStatus())) {
                android.content.Intent intent = new android.content.Intent(v.getContext(), SubmitVerificationActivity.class);
                intent.putExtra(SubmitVerificationActivity.EXTRA_SCHOLARSHIP_ITEM, item);
                if (v.getContext() instanceof android.app.Activity) {
                    ((android.app.Activity) v.getContext()).startActivity(intent);
                } else {
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInstitution, btnVerify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_scholarship_title);
            tvInstitution = itemView.findViewById(R.id.tv_institution_name);
            btnVerify = itemView.findViewById(R.id.btn_verify_this);
        }
    }
}

package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        boolean verified = "Verified".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status);
        boolean pending = "Pending verification".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status);
        final boolean canVerify = !verified && !pending;
        if (verified) {
            holder.btnVerify.setText(R.string.status_verified);
            holder.btnVerify.setAlpha(0.6f);
        } else if (pending) {
            holder.btnVerify.setText(R.string.status_pending);
            holder.btnVerify.setAlpha(0.8f);
        } else {
            holder.btnVerify.setText(R.string.verify_this_button);
            holder.btnVerify.setAlpha(1.0f);
        }
        holder.btnVerify.setEnabled(canVerify);

        holder.btnVerify.setOnClickListener(v -> {
            // Use the same eligibility decision as the visible button. A label
            // or legacy state must not produce an enabled button that does nothing.
            if (canVerify) {
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

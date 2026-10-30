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

public class ContactAvatarAdapter extends RecyclerView.Adapter<ContactAvatarAdapter.ViewHolder> {

    private final List<ContactAvatarItem> items;

    public ContactAvatarAdapter(List<ContactAvatarItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_avatar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactAvatarItem item = items.get(position);

        holder.ivAvatar.setImageResource(item.getAvatarResId());
        holder.tvName.setText(item.getName());
        holder.viewOnlineDot.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Mở đoạn chat với " + item.getName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        View viewOnlineDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_contact_avatar);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            viewOnlineDot = itemView.findViewById(R.id.view_online_dot);
        }
    }
}

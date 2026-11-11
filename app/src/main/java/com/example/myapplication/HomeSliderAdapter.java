package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeSliderAdapter extends RecyclerView.Adapter<HomeSliderAdapter.ViewHolder> {

    public interface OnCtaClickListener {
        void onCtaClick(HomeSliderItem item, int position);
    }

    private final List<HomeSliderItem> banners;
    private final OnCtaClickListener listener;

    public HomeSliderAdapter(List<HomeSliderItem> banners, OnCtaClickListener listener) {
        this.banners = banners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeSliderItem banner = banners.get(position);

        holder.tvTitle.setText(banner.getTitle());
        holder.tvSubtitle.setText(banner.getDescription());
        holder.btnCta.setText(banner.getCtaText());
        holder.ivIllustration.setImageResource(banner.getImageResId());

        holder.btnCta.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onCtaClick(banner, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        Button btnCta;
        ImageView ivIllustration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_slider_headline);
            tvSubtitle = itemView.findViewById(R.id.tv_slider_subtitle);
            btnCta = itemView.findViewById(R.id.btn_slider_cta);
            ivIllustration = itemView.findViewById(R.id.iv_slider_illustration);
        }
    }
}

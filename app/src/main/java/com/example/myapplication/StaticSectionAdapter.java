package com.example.myapplication;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** A screen's persistent controls, displayed as one section of a ConcatAdapter. */
final class StaticSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final View section;

    StaticSectionAdapter(View section) {
        this.section = section;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(section) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {}

    @Override
    public int getItemCount() {
        return 1;
    }
}

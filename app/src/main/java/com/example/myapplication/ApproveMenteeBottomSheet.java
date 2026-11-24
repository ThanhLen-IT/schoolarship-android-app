package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ApproveMenteeBottomSheet extends BottomSheetDialogFragment {

    public interface OnApproveConfirmedListener {
        void onApproveConfirmed(MenteeApplication application);
    }

    private MenteeApplication application;
    private OnApproveConfirmedListener listener;

    public static ApproveMenteeBottomSheet newInstance(MenteeApplication application, OnApproveConfirmedListener listener) {
        ApproveMenteeBottomSheet fragment = new ApproveMenteeBottomSheet();
        fragment.application = application;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_approve_mentee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            int closeId = getResources().getIdentifier("btn_close_approve_sheet", "id", getContext().getPackageName());
            if (closeId != 0) {
                View btnClose = view.findViewById(closeId);
                if (btnClose != null) {
                    btnClose.setOnClickListener(v -> dismiss());
                }
            }
        }

        TextView tvTitle = view.findViewById(R.id.tv_approve_title);
        if (tvTitle != null && application != null) {
            tvTitle.setText(getString(R.string.approve_title_format, application.getFullName()));
        }

        Button btnStartChat = view.findViewById(R.id.btn_approve_start_chat);
        if (btnStartChat != null) {
            btnStartChat.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApproveConfirmed(application);
                }
                dismiss();
            });
        }

        Button btnLater = view.findViewById(R.id.btn_approve_later);
        if (btnLater != null) {
            btnLater.setOnClickListener(v -> dismiss());
        }
    }
}

package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RejectMenteeBottomSheet extends BottomSheetDialogFragment {

    public interface OnRejectConfirmedListener {
        void onRejectConfirmed(MenteeApplication application, String reason);
    }

    private MenteeApplication application;
    private OnRejectConfirmedListener listener;

    private String selectedReason = "Đã đủ slot tháng này";

    public static RejectMenteeBottomSheet newInstance(MenteeApplication application, OnRejectConfirmedListener listener) {
        RejectMenteeBottomSheet fragment = new RejectMenteeBottomSheet();
        fragment.application = application;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_reject_mentee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            int closeId = getResources().getIdentifier("btn_close_reject_sheet", "id", getContext().getPackageName());
            if (closeId != 0) {
                View btnClose = view.findViewById(closeId);
                if (btnClose != null) {
                    btnClose.setOnClickListener(v -> dismiss());
                }
            }
        }

        TextView chipReason1 = view.findViewById(R.id.chip_reason_1);
        TextView chipReason2 = view.findViewById(R.id.chip_reason_2);
        TextView chipReason3 = view.findViewById(R.id.chip_reason_3);
        EditText etFeedback = view.findViewById(R.id.et_reject_feedback);
        TextView tvCharCounter = view.findViewById(R.id.tv_char_counter);
        Button btnSubmit = view.findViewById(R.id.btn_submit_reject);

        if (chipReason1 != null) {
            chipReason1.setOnClickListener(v -> {
                selectedReason = getString(R.string.reason_slot_full);
                updateChipStyles(chipReason1, chipReason2, chipReason3);
            });
        }

        if (chipReason2 != null) {
            chipReason2.setOnClickListener(v -> {
                selectedReason = getString(R.string.reason_not_suitable);
                updateChipStyles(chipReason2, chipReason1, chipReason3);
            });
        }

        if (chipReason3 != null) {
            chipReason3.setOnClickListener(v -> {
                selectedReason = getString(R.string.reason_other);
                updateChipStyles(chipReason3, chipReason1, chipReason2);
                if (etFeedback != null) {
                    etFeedback.requestFocus();
                }
            });
        }

        if (etFeedback != null && tvCharCounter != null) {
            etFeedback.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tvCharCounter.setText(s.length() + "/200");
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String feedback = etFeedback != null ? etFeedback.getText().toString().trim() : "";
                if (getString(R.string.reason_other).equals(selectedReason) && feedback.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập lý do từ chối", Toast.LENGTH_SHORT).show();
                    return;
                }

                String finalReason = feedback.isEmpty() ? selectedReason : selectedReason + ": " + feedback;
                if (listener != null) {
                    listener.onRejectConfirmed(application, finalReason);
                }
                Toast.makeText(getContext(), "Đã gửi phản hồi từ chối tới Mentee", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        }
    }

    private void updateChipStyles(TextView selected, TextView unselected1, TextView unselected2) {
        if (getContext() == null) return;

        if (selected != null) {
            selected.setBackgroundResource(R.drawable.bg_reason_chip_selected);
            selected.setTextColor(ContextCompat.getColor(getContext(), R.color.color_primary));
        }
        if (unselected1 != null) {
            unselected1.setBackgroundResource(R.drawable.bg_reason_chip_unselected);
            unselected1.setTextColor(ContextCompat.getColor(getContext(), R.color.color_text_primary));
        }
        if (unselected2 != null) {
            unselected2.setBackgroundResource(R.drawable.bg_reason_chip_unselected);
            unselected2.setTextColor(ContextCompat.getColor(getContext(), R.color.color_text_primary));
        }
    }
}

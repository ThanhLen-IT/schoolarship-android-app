package com.example.myapplication;

import android.util.Patterns;
import android.widget.EditText;

final class FormValidation {
    private FormValidation() {}

    static boolean required(EditText field) {
        return check(
                field, !field.getText().toString().trim().isEmpty(), "Vui lòng nhập thông tin này");
    }

    static boolean email(EditText field) {
        return check(
                field,
                Patterns.EMAIL_ADDRESS.matcher(field.getText().toString().trim()).matches(),
                "Email không hợp lệ");
    }

    static boolean password(EditText field) {
        return check(
                field,
                field.getText().toString().getBytes(java.nio.charset.StandardCharsets.UTF_8).length
                                >= 8
                        && field.getText()
                                        .toString()
                                        .getBytes(java.nio.charset.StandardCharsets.UTF_8)
                                        .length
                                <= 72,
                "Mật khẩu cần từ 8–72 byte UTF-8");
    }

    static boolean phone(EditText field) {
        return check(
                field,
                field.getText().toString().trim().matches("[0-9]{9,11}"),
                "Số điện thoại cần 9–11 chữ số");
    }

    private static boolean check(EditText field, boolean valid, String error) {
        field.setError(valid ? null : error);
        if (!valid) field.requestFocus();
        return valid;
    }
}

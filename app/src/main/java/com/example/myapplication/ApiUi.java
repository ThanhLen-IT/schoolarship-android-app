package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.function.Consumer;

/** A request is successful only when both HTTP and the API envelope succeed. */
final class ApiUi {
    private ApiUi() {}

    static boolean alive(Activity activity) {
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    static String bearer(Context context) {
        String token = new TokenManager(context).getToken();
        return token == null ? null : "Bearer " + token;
    }

    static boolean requireLogin(Activity activity) {
        if (new TokenManager(activity).isLoggedIn()) return true;
        activity.startActivity(new Intent(activity, SignInActivity.class));
        return false;
    }

    static void error(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    static <T> Call<ApiResponse<T>> request(
            Activity activity,
            Call<ApiResponse<T>> call,
            Consumer<ApiResponse<T>> success,
            Consumer<String> failure) {
        call.enqueue(
                new Callback<ApiResponse<T>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<T>> c, Response<ApiResponse<T>> response) {
                        if (c.isCanceled() || !alive(activity)) return;
                        String sentToken = c.request().header("Authorization");
                        if (sentToken != null && !sentToken.equals(bearer(activity))) return;
                        ApiResponse<T> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            success.accept(body);
                            return;
                        }
                        ApiResponse<?> error = body;
                        if (error == null && response.errorBody() != null) {
                            try {
                                error =
                                        new com.google.gson.Gson()
                                                .fromJson(
                                                        response.errorBody().string(),
                                                        ApiResponse.class);
                            } catch (Exception ignored) {
                            }
                        }
                        if (sentToken != null
                                && (response.code() == 401
                                        || (response.code() == 403
                                                && error != null
                                                && "ACCOUNT_INACTIVE".equals(error.getCode())))) {
                            SessionStore.clear(activity);
                            activity.startActivity(
                                    new Intent(activity, SignInActivity.class)
                                            .addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            return;
                        }
                        failure.accept(
                                error != null && error.getMessage() != null
                                        ? error.getMessage()
                                        : "Yêu cầu thất bại (HTTP "
                                                + response.code()
                                                + "). Vui lòng thử lại.");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<T>> c, Throwable error) {
                        String sentToken = c.request().header("Authorization");
                        if (sentToken != null && !sentToken.equals(bearer(activity))) return;
                        if (!c.isCanceled() && alive(activity))
                            failure.accept("Không thể kết nối máy chủ. Vui lòng thử lại.");
                    }
                });
        return call;
    }

    static String errorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                ApiResponse<?> error =
                        new com.google.gson.Gson()
                                .fromJson(response.errorBody().string(), ApiResponse.class);
                if (error != null && error.getMessage() != null) return error.getMessage();
            }
        } catch (Exception ignored) {
            /* A proxy may return HTML instead of JSON. */
        }
        return "Yêu cầu thất bại (HTTP " + response.code() + "). Vui lòng thử lại.";
    }

    static void saveScholarship(Activity activity, String id, Runnable saved) {
        if (!requireLogin(activity)) return;
        request(
                activity,
                ApiClient.getApiService().saveScholarship(bearer(activity), id),
                result -> {
                    error(activity, "Đã lưu học bổng vào tài khoản.");
                    if (saved != null) saved.run();
                },
                message -> error(activity, message));
    }

    static void openUrl(Activity activity, String url) {
        try {
            android.net.Uri uri = android.net.Uri.parse(url);
            if (!("https".equalsIgnoreCase(uri.getScheme())
                            || "http".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null) throw new IllegalArgumentException();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (RuntimeException error) {
            error(activity, "Chưa có đường dẫn hợp lệ hoặc không thể mở liên kết.");
        }
    }
}

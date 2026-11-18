package com.example.myapplication;

import android.app.Activity;
import android.net.Uri;

import java.io.*;

final class MobileFiles {
    static void pdf(
            Activity a,
            Uri uri,
            java.util.function.Consumer<byte[]> done,
            java.util.function.Consumer<String> failed) {
        new Thread(
                        () -> {
                            try (InputStream in = a.getContentResolver().openInputStream(uri);
                                    ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                if (in == null) throw new IOException();
                                byte[] buffer = new byte[8192];
                                int n;
                                while ((n = in.read(buffer)) != -1) {
                                    if (out.size() + n > 5 * 1024 * 1024)
                                        throw new IOException("PDF tối đa 5 MB.");
                                    out.write(buffer, 0, n);
                                }
                                byte[] bytes = out.toByteArray();
                                if (bytes.length < 5
                                        || !new String(
                                                        bytes,
                                                        0,
                                                        5,
                                                        java.nio.charset.StandardCharsets.US_ASCII)
                                                .equals("%PDF-"))
                                    throw new IOException("Tệp không phải PDF hợp lệ.");
                                a.runOnUiThread(
                                        () -> {
                                            if (ApiUi.alive(a)) done.accept(bytes);
                                        });
                            } catch (Exception e) {
                                a.runOnUiThread(
                                        () -> {
                                            if (ApiUi.alive(a))
                                                failed.accept(
                                                        e.getMessage() == null
                                                                ? "Không thể đọc tệp."
                                                                : e.getMessage());
                                        });
                            }
                        },
                        "read-pdf")
                .start();
    }

    static okhttp3.RequestBody value(String v) {
        return okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), v);
    }

    static void avatar(
            Activity a,
            Uri uri,
            java.util.function.Consumer<String> done,
            java.util.function.Consumer<String> failed) {
        new Thread(
                        () -> {
                            try {
                                android.graphics.BitmapFactory.Options options =
                                        new android.graphics.BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                try (InputStream in = a.getContentResolver().openInputStream(uri)) {
                                    android.graphics.BitmapFactory.decodeStream(in, null, options);
                                }
                                if (options.outWidth <= 0 || options.outHeight <= 0)
                                    throw new IOException();
                                options.inSampleSize = 1;
                                while (Math.max(options.outWidth, options.outHeight)
                                                / options.inSampleSize
                                        > 1024) options.inSampleSize *= 2;
                                options.inJustDecodeBounds = false;
                                android.graphics.Bitmap bitmap;
                                try (InputStream in = a.getContentResolver().openInputStream(uri)) {
                                    bitmap =
                                            android.graphics.BitmapFactory.decodeStream(
                                                    in, null, options);
                                }
                                if (bitmap == null) throw new IOException();
                                try (java.io.InputStream exifIn =
                                        a.getContentResolver().openInputStream(uri)) {
                                    int orientation =
                                            new android.media.ExifInterface(exifIn)
                                                    .getAttributeInt(
                                                            android.media.ExifInterface
                                                                    .TAG_ORIENTATION,
                                                            1);
                                    android.graphics.Matrix matrix = new android.graphics.Matrix();
                                    switch (orientation) {
                                        case 2:
                                            matrix.setScale(-1, 1);
                                            break;
                                        case 3:
                                            matrix.setRotate(180);
                                            break;
                                        case 4:
                                            matrix.setScale(1, -1);
                                            break;
                                        case 5:
                                            matrix.setRotate(90);
                                            matrix.postScale(-1, 1);
                                            break;
                                        case 6:
                                            matrix.setRotate(90);
                                            break;
                                        case 7:
                                            matrix.setRotate(-90);
                                            matrix.postScale(-1, 1);
                                            break;
                                        case 8:
                                            matrix.setRotate(-90);
                                            break;
                                    }
                                    if (!matrix.isIdentity()) {
                                        android.graphics.Bitmap corrected =
                                                android.graphics.Bitmap.createBitmap(
                                                        bitmap,
                                                        0,
                                                        0,
                                                        bitmap.getWidth(),
                                                        bitmap.getHeight(),
                                                        matrix,
                                                        true);
                                        if (corrected != bitmap) bitmap.recycle();
                                        bitmap = corrected;
                                    }
                                } catch (Exception ignored) {
                                }
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                bitmap.compress(
                                        android.graphics.Bitmap.CompressFormat.JPEG, 90, out);
                                bitmap.recycle();
                                okhttp3.MultipartBody.Part file =
                                        okhttp3.MultipartBody.Part.createFormData(
                                                "avatar",
                                                "avatar.jpg",
                                                okhttp3.RequestBody.create(
                                                        okhttp3.MediaType.parse("image/jpeg"),
                                                        out.toByteArray()));
                                a.runOnUiThread(
                                        () -> {
                                            if (ApiUi.alive(a))
                                                ApiUi.request(
                                                        a,
                                                        ApiClient.getApiService()
                                                                .uploadAvatar(
                                                                        ApiUi.bearer(a), file),
                                                        r -> {
                                                            if (r.getData() != null)
                                                                done.accept(r.getData().avatar_url);
                                                            else
                                                                failed.accept(
                                                                        "Chưa nhận được ảnh đã cập"
                                                                                + " nhật.");
                                                        },
                                                        failed);
                                        });
                            } catch (Exception e) {
                                a.runOnUiThread(
                                        () -> {
                                            if (ApiUi.alive(a))
                                                failed.accept(
                                                        "Không thể đọc ảnh. Vui lòng chọn ảnh"
                                                                + " khác.");
                                        });
                            }
                        },
                        "upload-avatar")
                .start();
    }

    static void openCv(Activity a, String id) {
        new Thread(
                        () -> {
                            try {
                                retrofit2.Response<okhttp3.ResponseBody> r =
                                        ApiClient.getApiService().cv(ApiUi.bearer(a), id).execute();
                                if (!r.isSuccessful() || r.body() == null) {
                                    if (r.errorBody() != null) r.errorBody().close();
                                    throw new IOException();
                                }
                                File dir = new File(a.getCacheDir(), "cv");
                                dir.mkdirs();
                                File target = new File(dir, "cv-" + Long.parseLong(id) + ".pdf");
                                try (okhttp3.ResponseBody body = r.body();
                                        InputStream in = body.byteStream();
                                        OutputStream out = new FileOutputStream(target)) {
                                    byte[] b = new byte[8192];
                                    int n, total = 0;
                                    while ((n = in.read(b)) != -1) {
                                        total += n;
                                        if (total > 5 * 1024 * 1024) throw new IOException();
                                        out.write(b, 0, n);
                                    }
                                }
                                a.runOnUiThread(
                                        () -> {
                                            if (!ApiUi.alive(a)) return;
                                            try {
                                                Uri uri =
                                                        androidx.core.content.FileProvider
                                                                .getUriForFile(
                                                                        a,
                                                                        a.getPackageName()
                                                                                + ".files",
                                                                        target);
                                                a.startActivity(
                                                        new android.content.Intent(
                                                                        android.content.Intent
                                                                                .ACTION_VIEW)
                                                                .setDataAndType(
                                                                        uri, "application/pdf")
                                                                .addFlags(
                                                                        android.content.Intent
                                                                                .FLAG_GRANT_READ_URI_PERMISSION));
                                            } catch (RuntimeException e) {
                                                ApiUi.error(a, "Cần ứng dụng đọc PDF để mở CV.");
                                            }
                                        });
                            } catch (Exception e) {
                                a.runOnUiThread(
                                        () -> {
                                            if (ApiUi.alive(a))
                                                ApiUi.error(
                                                        a, "Không thể tải CV. Vui lòng thử lại.");
                                        });
                            }
                        },
                        "download-cv")
                .start();
    }
}

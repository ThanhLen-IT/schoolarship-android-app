package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Device-local avatar copies; does not upload photos to a server. */
final class LocalAvatarStorage {
    private LocalAvatarStorage() {}

    static Bitmap preview(Context context, Uri uri) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(input, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw new IOException("Invalid image");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        while (Math.max(bounds.outWidth, bounds.outHeight) / options.inSampleSize > 1024)
            options.inSampleSize *= 2;
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            Bitmap image = BitmapFactory.decodeStream(input, null, options);
            if (image == null) throw new IOException("Cannot decode image");
            return image;
        }
    }

    static String importPhoto(Context context, Uri uri) throws IOException {
        Bitmap source = preview(context, uri);
        Bitmap oriented = source;
        Bitmap square = null;
        Bitmap output = null;
        File destination = null;
        try {
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            try (InputStream input = context.getContentResolver().openInputStream(uri)) {
                if (input != null) orientation = new ExifInterface(input).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException ignored) { /* Images without EXIF use normal orientation. */ }
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: matrix.setScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_180: matrix.setRotate(180); break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL: matrix.setScale(1, -1); break;
                case ExifInterface.ORIENTATION_TRANSPOSE: matrix.setRotate(90); matrix.postScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_90: matrix.setRotate(90); break;
                case ExifInterface.ORIENTATION_TRANSVERSE: matrix.setRotate(270); matrix.postScale(-1, 1); break;
                case ExifInterface.ORIENTATION_ROTATE_270: matrix.setRotate(270); break;
                default: break;
            }
            if (!matrix.isIdentity()) oriented = Bitmap.createBitmap(source, 0, 0,
                    source.getWidth(), source.getHeight(), matrix, true);
            square = ThumbnailUtils.extractThumbnail(oriented, 512, 512);
            output = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(square, 0, 0, null);
            File folder = new File(context.getFilesDir(), "profile_avatars");
            if (!folder.isDirectory() && !folder.mkdirs()) throw new IOException("Cannot create avatar folder");
            destination = File.createTempFile("avatar_", ".jpg", folder);
            try (FileOutputStream stream = new FileOutputStream(destination)) {
                if (!output.compress(Bitmap.CompressFormat.JPEG, 90, stream)) throw new IOException("Cannot save photo");
            }
            return Uri.fromFile(destination).toString();
        } catch (IOException | RuntimeException e) {
            if (destination != null) destination.delete();
            throw e;
        } finally {
            if (output != null) output.recycle();
            if (square != null && square != oriented) square.recycle();
            if (oriented != source) oriented.recycle();
            source.recycle();
        }
    }

    static void deleteCopy(Context context, String value) {
        try {
            Uri uri = Uri.parse(value);
            if (!"file".equals(uri.getScheme()) || uri.getPath() == null) return;
            File file = new File(uri.getPath()).getCanonicalFile();
            File folder = new File(context.getFilesDir(), "profile_avatars").getCanonicalFile();
            if (folder.equals(file.getParentFile())) file.delete();
        } catch (IOException | RuntimeException ignored) { /* No owned copy to remove. */ }
    }
}

package com.example.cscan.main_utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

public class ImageUtils {

    public static void saveImageToGallery(Context context, String imagePath) {
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            // Cập nhật thông tin vào MediaStore
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Quét và cập nhật thư viện
            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, null);

            Toast.makeText(context, "Lưu ảnh vào thư viện thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Đường dẫn ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}

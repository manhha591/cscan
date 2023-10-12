package com.example.cscan.main_utils;

import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.cscan.models.DataTypes;
import com.example.cscan.models.Datas;
import com.example.cscan.models.Documents;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.Images;
import com.example.cscan.models.User;

import java.util.Date;
import java.util.List;

public class Constant {
    public static String IdentifyActivity = "IdentifyActivity";
    public static List<Images> imagesList;
    public static String inputType = "Group";
    public static Bitmap original;

    public static GroupImage group_current = null;
    public static User user_current;
    public static Documents document_current = null;

    public static DataTypes Doc;
    public static String type;
    public static DataTypes Pdf;
    public static DataTypes Image;
    public static Datas data;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateTime(String str) {
        return new SimpleDateFormat(str).format(new Date());
    }

}

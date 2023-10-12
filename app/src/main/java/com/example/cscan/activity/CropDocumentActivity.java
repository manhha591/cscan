package com.example.cscan.activity;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cscan.R;
import com.example.cscan.main_utils.AdjustUtil;
import com.example.cscan.main_utils.BitmapUtils;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.Images;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.InsertGroupCallback;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.pqpo.smartcropperlib.view.CropImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropDocumentActivity extends AppCompatActivity {
    private CropImageView iv_preview_crop;
    protected ImageView iv_done;
    public String selected_group_name;
    protected ImageView iv_retake;
    protected ImageView iv_back;
    public Bitmap original;
    public String username = "HA";
    public GroupImage group_current;
    protected ImageView iv_Rotate_Doc;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//          if (Constant.IdentifyActivity.equals("CurrentFilterActivity")) {
//                startActivity(new Intent(CropDocumentActivity.this, CurrentFilterActivity.class));
//                Constant.IdentifyActivity = "";
//                finish();
//            } else
            if (Constant.IdentifyActivity.equals("ScannerActivity_Retake")) {
                Constant.IdentifyActivity = "";
                finish();
            }
        }

    };
    private String group_name;
    private String group_date;
    private int group_id = 0;
    private int user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_document);
        //dbHelper = new DBHelper(this);

        init();

    }

    private void init() {
        iv_preview_crop = (CropImageView) findViewById(R.id.iv_preview_crop);
        iv_done = (ImageView) findViewById(R.id.iv_done);
        iv_retake = findViewById(R.id.iv_retake);
        iv_Rotate_Doc =  findViewById(R.id.iv_Rotate_Doc);
        iv_back = findViewById(R.id.iv_back);

        if (Constant.original != null) {
            iv_preview_crop.setImageToCrop(Constant.original);
            original = Constant.original;
            //changeBrightness(20);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_done:
                if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    if (Constant.inputType.equals("Group")) {
                        group_name = "CamScanner" + Constant.getDateTime("_ddMMHHmmss");
                        group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                        insertGroupImage(group_name, group_date, Constant.Image.getDataTypeId(), new InsertGroupCallback() {
                            @Override
                            public void onGroupInserted(GroupImage group) {
                                Constant.group_current = group;
                                System.out.println(Constant.group_current);
                                insertImage();

                            }
                        });
                    } else {
                        insertImage();
                    }

                }
                return;
            case R.id.iv_retake:
                startActivity(new Intent(CropDocumentActivity.this, ScannerActivity.class));
                finish();
                return;
            case R.id.iv_Rotate_Doc:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                original = createBitmap;
                iv_preview_crop.setImageToCrop(Constant.original);
                iv_preview_crop.setFullImgCrop();
                Log.e(TAG, "onClick: Rotate");
                return;
            case R.id.iv_back:
                finish();
                return;
        }
    }

    private void changeBrightness(float brightness) {
        iv_preview_crop.setImageBitmap(AdjustUtil.changeBitmapContrastBrightness(original, 1.0f, brightness));
    }



    private void insertGroupImage(String group_name, String group_date, int dataTypeId, InsertGroupCallback callback) {
        GroupImage group = new GroupImage(group_name, group_date, Constant.Image.getDataTypeId());
        IApiUserService.apiService.insertGroup(group)
                .enqueue(new Callback<GroupImage>() {
                    @Override
                    public void onResponse(Call<GroupImage> call, Response<GroupImage> response) {
                        if (response.isSuccessful()) {
                            GroupImage group_current = response.body();
                            System.out.println(group_current);
                            //Toast.makeText(CropDocumentActivity.this, "Đăng kí thành công!", Toast.LENGTH_LONG).show();
                            callback.onGroupInserted(group_current);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GroupImage> call, Throwable t) {
                        Toast.makeText(CropDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void insertImage() {

        Bitmap bitmap = Constant.original;
        byte[] bytes = BitmapUtils.getBytes(bitmap);
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        group_id = Constant.group_current.getGroupId();
        group_name = Constant.group_current.getGroupName();
        Images image = new Images(file.getPath(), group_id);
        IApiUserService.apiService.insertImage(image)
                .enqueue(new Callback<Images>() {
                    @Override
                    public void onResponse(Call<Images> call, Response<Images> response) {
                         if (response.isSuccessful()) {
                            Images img = response.body();
                            System.out.println(img);
                            //Toast.makeText(CropDocumentActivity.this, "!", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Images> call, Throwable t) {
                        Toast.makeText(CropDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
        Intent intent2 = new Intent(CropDocumentActivity.this, GroupDocumentActivity.class);
        intent2.putExtra("current_group", group_name);
        startActivity(intent2);
        finish();
        return;
    }

}
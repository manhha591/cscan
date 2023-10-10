package com.example.cscan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscan.R;
import com.example.cscan.adapter.ImageAdapter;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.main_utils.ImageUtils;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.ImageToPdfConverter;
import com.example.cscan.models.Images;
import com.example.cscan.service.DeleteCallback;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.getListImageCallBack;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupDocumentActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    public static String current_group;
    //protected GroupDocAdapter groupDocAdapter;
    protected ImageView iv_back;
    protected ImageView iv_create_pdf;
    protected ImageView iv_doc_camera;
    protected ImageView iv_doc_more;
    private LinearLayout ly_doc_camera;
    public Uri pdfUri;
    protected ImageAdapter imageAdapter;

    public RecyclerView rv_group_doc;

    public String selected_group_name;

    public int selected_position;

    public ArrayList<Bitmap> singleBitmap = new ArrayList<>();

    public String singleDoc;

    public TextView tv_title;

    public static List<Images> imagesList;

    @Override
    protected void onResume() {
        tv_title.setText(Constant.group_current.getGroupName());
        System.out.println(Constant.group_current);
        GroupDocumentActivity.imagesList = null;
        getAllImage(Constant.group_current.getGroupId(), new getListImageCallBack() {
            @Override
            public void onGetListImageCallBack(List<Images> list) {
                GroupDocumentActivity.imagesList = list;
                setImageAdapter();

            }
        });
        super.onResume();
    }

    private void setImageAdapter() {
        rv_group_doc.setHasFixedSize(true);
        rv_group_doc.setLayoutManager(new GridLayoutManager((Context) GroupDocumentActivity.this, 2, RecyclerView.VERTICAL, false));
        imageAdapter = new ImageAdapter(GroupDocumentActivity.this, imagesList);
        rv_group_doc.setAdapter(imageAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_document);

        init();

    }

    private void init() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_create_pdf = (ImageView) findViewById(R.id.iv_create_pdf);
        iv_doc_more = (ImageView) findViewById(R.id.iv_doc_more);
        rv_group_doc = (RecyclerView) findViewById(R.id.rv_group_doc);
        iv_doc_camera = (ImageView) findViewById(R.id.iv_doc_camera);
        ly_doc_camera = (LinearLayout) findViewById(R.id.ly_doc_camera);


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_create_pdf:
                List<String> imageDatas = new ArrayList<>();
                for (Images images : imagesList){
                    imageDatas.add(images.getImageData());
                }
                String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                String outputFilePath = outputDir + File.separator + Constant.group_current.getGroupName() + ".pdf";

                ImageToPdfConverter.convertToPdf(imageDatas, outputFilePath);
                Toast.makeText(this, "Create file pdf success", Toast.LENGTH_SHORT).show();
                return;
            case R.id.iv_doc_camera:
                Constant.inputType = "GroupItem";
                startActivity(new Intent(GroupDocumentActivity.this, ScannerActivity.class));
                finish();
                return;
            case R.id.iv_doc_more:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
                popupMenu.inflate(R.menu.group_doc_more);
                try {
                    Field declaredField = PopupMenu.class.getDeclaredField("mPopup");
                    declaredField.setAccessible(true);
                    Object obj = declaredField.get(popupMenu);
                    obj.getClass().getDeclaredMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{true});
                    popupMenu.show();
                    return;
                } catch (Exception e) {
                    popupMenu.show();
                    e.printStackTrace();
                    return;
                }
            default:
                return;
        }
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDocumentActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("You want to delete this group ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý logic khi người dùng chọn xóa
                        // Thêm mã xóa ở đây
                        deleteGroup(Constant.group_current.getGroupId(), new DeleteCallback() {
                            @Override
                            public void onDeleteCompleted() {
                                dialog.dismiss();
                                finish();
                            }
                        }, GroupDocumentActivity.this);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý logic khi người dùng chọn hủy
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;

            case R.id.rename:
                updateGroupName(Constant.group_current);
                return true;
            case R.id.save_to_gallery:
                for (Images images : imagesList){
                    ImageUtils.saveImageToGallery(getApplicationContext(), images.getImageData());
                }
                return true;
            default:
                return false;
        }
    }

    private void upDateGroup(GroupImage groupImage, String group_name) {
        groupImage.setGroupName(group_name);
        IApiUserService.apiService.updateGroup(groupImage)
                .enqueue(new Callback<GroupImage>() {
                    @Override
                    public void onResponse(Call<GroupImage> call, Response<GroupImage> response) {
                        if (response.isSuccessful()) {
                            GroupImage group_current = response.body();
                        } else {
                            Toast.makeText(GroupDocumentActivity.this, "Name already exist", Toast.LENGTH_SHORT).show();
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GroupImage> call, Throwable t) {
                        //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void deleteImage(int imageId, DeleteCallback callback) {
        IApiUserService.apiService.deleteImage(imageId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(GroupDocumentActivity.this, "Delete succes", Toast.LENGTH_SHORT).show();
                            callback.onDeleteCompleted();
                        } else {
                            Toast.makeText(GroupDocumentActivity.this, "Name already exist", Toast.LENGTH_SHORT).show();
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }
    public static void deleteGroup(int groupId, DeleteCallback callback, Activity activity) {
        IApiUserService.apiService.deleteGroup(groupId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            callback.onDeleteCompleted();
                            Toast.makeText(activity, "Delete succes!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(activity, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static void getAllImage(int groupId, getListImageCallBack callback) {

        IApiUserService.apiService.getAllImages(groupId)
                .enqueue(new Callback<List<Images>>() {
                    @Override
                    public void onResponse(Call<List<Images>> call, Response<List<Images>> response) {
                        if (response.isSuccessful()) {
                            List<Images> images = response.body();
                            for (Images img : images) {
                                System.out.println(img);
                            }
                           // Toast.makeText(activity, "Create pdf succes!", Toast.LENGTH_LONG).show();

                            // Process the list of images as needed
                            callback.onGetListImageCallBack(images);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Images>> call, Throwable t) {
                        //Toast.makeText(activity, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private  void updateGroupName(GroupImage groupImage) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.update_group_name);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final EditText editText = (EditText) dialog.findViewById(R.id.et_group_name);
        editText.setText(groupImage.getGroupName());
        editText.setSelection(editText.length());
        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                    Toast.makeText(GroupDocumentActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                upDateGroup(groupImage,editText.getText().toString());
                dialog.dismiss();
                GroupDocumentActivity.current_group = editText.getText().toString();
                tv_title.setText(GroupDocumentActivity.current_group);
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onClickItemMore(int imageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupDocumentActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("You want to delete this image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý logic khi người dùng chọn xóa
                // Thêm mã xóa ở đây
                deleteImage(imageId, new DeleteCallback() {
                    @Override
                    public void onDeleteCompleted() {
                        onResume();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý logic khi người dùng chọn hủy
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
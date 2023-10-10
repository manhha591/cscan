package com.example.cscan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cscan.R;
import com.example.cscan.adapter.GroupAdapter;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.main_utils.ImageUtils;
import com.example.cscan.models.DataTypes;
import com.example.cscan.models.Datas;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.ImageToPdfConverter;
import com.example.cscan.models.Images;
import com.example.cscan.service.DeleteCallback;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.getListDataTypeCallBack;
import com.example.cscan.service.getListGroupCallBack;
import com.example.cscan.service.getListImageCallBack;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataActivity extends AppCompatActivity {
    private List<Datas> ListData;
    private RecyclerView rv_group;
    private List<GroupImage> groupImageList;
    protected LinearLayoutManager layoutManager;

    protected GroupAdapter groupAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        rv_group = (RecyclerView) findViewById(R.id.rv_group);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_group.setLayoutManager(layoutManager);
    }
    @Override
    protected void onResume() {

        if(Constant.type.equals("Doc")){

        }
        else if(Constant.type.equals("Pdf")){

        }else {
            getAllGroup(Constant.Image.getDataTypeId(), new getListGroupCallBack() {
                @Override
                public void onGetListGroupCallBack(List<GroupImage> list) {
                    groupImageList = list;
                    System.out.println(groupImageList);
                    setGroupAdapter();
                }
            });
        }
        super.onResume();
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_doc_camera:
                Constant.inputType = "Group";
                startActivity(new Intent(DataActivity.this, ScannerActivity.class));
                return;
            default:
                return;
        }
    }
    private void setGroupAdapter() {

        groupAdapter = new GroupAdapter(this, groupImageList);
        rv_group.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
    }
    private void getAllGroup(int dataTypeId, getListGroupCallBack callback) {

        IApiUserService.apiService.getAllGroup(dataTypeId)
                .enqueue(new Callback<List<GroupImage>>() {
                    @Override
                    public void onResponse(Call<List<GroupImage>> call, Response<List<GroupImage>> response) {
                        if (response.isSuccessful()) {
                            List<GroupImage> groupImage = response.body();
                            for (GroupImage gp : groupImage) {
                                System.out.println(gp);
                            }
                            // Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                            // Process the list of images as needed
                            callback.onGetListGroupCallBack(groupImage);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GroupImage>> call, Throwable t) {
                        //Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                        Log.e("CALL API", t.getMessage());

                    }
                });
    }

    public void clickOnListItem(GroupImage groupImage) {
        Constant.group_current = groupImage;

        Intent intent2 = new Intent(this, GroupDocumentActivity.class);
        intent2.putExtra("current_group", Constant.group_current.getGroupName());
        startActivity(intent2);
    }

    public void clickOnListMore(GroupImage groupImage) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.group_bottomsheet_dialog, (ViewGroup) null);
        final TextView tv_dialog_title = (TextView) inflate.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(groupImage.getGroupName());
        ((TextView) inflate.findViewById(R.id.tv_dialog_date)).setText(groupImage.getGroupDate());

        RelativeLayout rl_save_as_pdf = inflate.findViewById(R.id.rl_save_as_pdf);
        RelativeLayout rl_share = inflate.findViewById(R.id.rl_share);
        RelativeLayout rl_save_to_gallery = inflate.findViewById(R.id.rl_save_to_gallery);

        rl_save_as_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
                    @Override
                    public void onGetListImageCallBack(List<Images> list) {
                        List<String> imageDatas = new ArrayList<>();
                        for (Images images : list) {
                            imageDatas.add(images.getImageData());
                        }
                        String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String outputFilePath = outputDir + File.separator + groupImage.getGroupName() + ".pdf";
                        ImageToPdfConverter.convertToPdf(imageDatas, outputFilePath);
                        Datas pdf = new Datas(groupImage.getGroupName(), outputFilePath, Constant.Pdf.getDataTypeId());
                        insertData(pdf);
                    }
                });
                bottomSheetDialog.dismiss();


            }
        });
        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // shareGroup(name);
                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
                    @Override
                    public void onGetListImageCallBack(List<Images> list) {
                        List<String> imageDatas = new ArrayList<>();
                        for (Images images : list) {
                            imageDatas.add(images.getImageData());
                        }
                        String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String outputFilePath = outputDir + File.separator + Constant.group_current.getGroupName() + ".pdf";
                        ImageToPdfConverter.convertToPdf(imageDatas, outputFilePath);
                        File file = new File(outputFilePath);
                        Uri pdfFileUri = FileProvider.getUriForFile(DataActivity.this, "com.example.fileprovider", file);

                        // Tạo Intent chia sẻ
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("application/pdf");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfFileUri);

                        // Mở hộp thoại chọn ứng dụng chia sẻ
                        startActivity(Intent.createChooser(shareIntent, "Share PDF File"));
                    }
                });
                bottomSheetDialog.dismiss();
            }
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_rename)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroupName(groupImage);
                bottomSheetDialog.dismiss();
            }
        });
        rl_save_to_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new saveGroupToGallery(name).execute(new String[0]);
                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
                    @Override
                    public void onGetListImageCallBack(List<Images> list) {
                        List<Images> imagesList = list;
                        for (Images images : imagesList) {
                            ImageUtils.saveImageToGallery(getApplicationContext(), images.getImageData());
                        }
                    }
                });
                bottomSheetDialog.dismiss();
            }
        });

        ((RelativeLayout) inflate.findViewById(R.id.rl_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("You want to delete this group?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý logic khi người dùng chọn xóa
                        // Thêm mã xóa ở đây
                        GroupDocumentActivity.deleteGroup(groupImage.getGroupId(), new DeleteCallback() {
                            @Override
                            public void onDeleteCompleted() {
                                onResume();
                            }
                        }, DataActivity.this);
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
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }

    private void insertData(Datas datas) {
        IApiUserService.apiService.InsertData(datas)
                .enqueue(new Callback<Datas>() {
                    @Override
                    public void onResponse(Call<Datas> call, Response<Datas> response) {
                        if (response.isSuccessful()) {

                            //Toast.makeText(CropDocumentActivity.this, "!", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Datas> call, Throwable t) {
                        Toast.makeText(DataActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateGroupName(GroupImage groupImage) {
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
                    Toast.makeText(DataActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    upDateGroup(groupImage, editText.getText().toString());
                    dialog.dismiss();

                    onResume();

                }

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

    private void upDateGroup(GroupImage groupImage, String group_name) {
        groupImage.setGroupName(group_name);
        IApiUserService.apiService.updateGroup(groupImage)
                .enqueue(new Callback<GroupImage>() {
                    @Override
                    public void onResponse(Call<GroupImage> call, Response<GroupImage> response) {
                        if (response.isSuccessful()) {
                            GroupImage group_current = response.body();
                            onResume();

                        } else {
                            Toast.makeText(DataActivity.this, "Name already exist", Toast.LENGTH_SHORT).show();
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
}
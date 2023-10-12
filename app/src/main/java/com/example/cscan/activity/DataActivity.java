package com.example.cscan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cscan.R;
import com.example.cscan.adapter.DataAdapter;

import com.example.cscan.adapter.GroupAdapter;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.main_utils.ImageUtils;
import com.example.cscan.models.Datas;
import com.example.cscan.models.Documents;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.ImageToPdfConverter;
import com.example.cscan.models.Images;
import com.example.cscan.service.CallbackService;
import com.example.cscan.service.DeleteCallback;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.getListDataCallBack;
import com.example.cscan.service.getListGroupCallBack;
import com.example.cscan.service.getListImageCallBack;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataActivity extends AppCompatActivity {
    private List<Datas> ListData;
    private RecyclerView rv_group;


    private List<GroupImage> groupImageList;
    protected LinearLayoutManager layoutManager;
    SpinKitView process;
    public String str = "";
    protected GroupAdapter groupAdapter;
    protected DataAdapter datasAdapter;
    ImageView iv_clear_txt;
    EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
         iv_clear_txt = (ImageView) findViewById(R.id.iv_clear_txt);
         et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Nếu kích thước của văn bản là 0, "iv_clear_txt" sẽ không hiển thị
                if (i3 == 0) {
                    iv_clear_txt.setVisibility(View.INVISIBLE);

                } else if (i3 == 1) {
                    //Nếu kích thước của văn bản là 1, "iv_clear_txt" sẽ hiển thị
                    iv_clear_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            //Khi văn bản được nhập, hàm "filter" sẽ được gọi với văn bản đó là tham số.
            public void afterTextChanged(Editable editable) {
                if(Constant.type.equals("Image")){
                    if (groupImageList.size() > 0) {
                        ArrayList arrayList = new ArrayList();
                        Iterator<GroupImage> it = groupImageList.iterator();
                        while (it.hasNext()) {
                            GroupImage next = it.next();
                            if (next.getGroupName().toLowerCase().contains(editable.toString().toLowerCase())) {
                                arrayList.add(next);
                            }
                        }
                        groupAdapter.filterList(arrayList);
                    }
                }else {
                    if (ListData.size() > 0) {
                        ArrayList arrayList = new ArrayList();
                        Iterator<Datas> it = ListData.iterator();
                        while (it.hasNext()) {
                            Datas next = it.next();
                            if (next.getDataName().toLowerCase().contains(editable.toString().toLowerCase())) {
                                arrayList.add(next);
                            }
                        }
                        datasAdapter.filterList(arrayList);
                    }
                }

            }
        });
    }


    @Override
    protected void onResume() {
        rv_group = (RecyclerView) findViewById(R.id.rv_group);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_group.setLayoutManager(layoutManager);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(Constant.document_current.getDocumentName());
        if (Constant.type.equals("Doc")) {

            getAllData(Constant.Doc.getDataTypeId(), new getListDataCallBack() {
                @Override
                public void onGetListDataCallBack(List<Datas> list) {
                    ListData = list;
                    System.out.println(ListData);
                    datasAdapter = new DataAdapter(DataActivity.this, ListData, "Doc");
                    rv_group.setAdapter(datasAdapter);
                    datasAdapter.notifyDataSetChanged();
                }


            });
        } else if (Constant.type.equals("Pdf")) {
            ListData = null;
            getAllData(Constant.Pdf.getDataTypeId(), new getListDataCallBack() {
                @Override
                public void onGetListDataCallBack(List<Datas> list) {
                    ListData = list;
                    System.out.println(ListData);
                    datasAdapter = new DataAdapter(DataActivity.this, ListData, "Pdf");
                    rv_group.setAdapter(datasAdapter);
                    datasAdapter.notifyDataSetChanged();
                }


            });
        } else {
            getAllGroup(Constant.Image.getDataTypeId(), new getListGroupCallBack() {
                @Override
                public void onGetListGroupCallBack(List<GroupImage> list) {
                    groupImageList = list;
                    System.out.println(groupImageList);
                    groupAdapter = new GroupAdapter(DataActivity.this, groupImageList);
                    rv_group.setAdapter(groupAdapter);
                    groupAdapter.notifyDataSetChanged();
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
            case R.id.iv_group_camera:
                Constant.inputType = "Group";
                startActivity(new Intent(DataActivity.this, ScannerActivity.class));
                return;
            case R.id.iv_clear_txt:
                et_search.setText("");
                iv_clear_txt.setVisibility(View.GONE);//ẩn text
                return;
            default:
                return;
        }
    }

    private void setGroupAdapter() {


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

    private void getAllData(int dataTypeId, getListDataCallBack callback) {

        IApiUserService.apiService.getAllData(dataTypeId)
                .enqueue(new Callback<List<Datas>>() {
                    @Override
                    public void onResponse(Call<List<Datas>> call, Response<List<Datas>> response) {
                        if (response.isSuccessful()) {
                            List<Datas> datas = response.body();
                            for (Datas gp : datas) {
                                System.out.println(gp);
                            }
                            // Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                            // Process the list of images as needed
                            callback.onGetListDataCallBack(datas);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Datas>> call, Throwable t) {
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
        Constant.group_current = groupImage;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.group_bottomsheet_dialog, (ViewGroup) null);
        final TextView tv_dialog_title = (TextView) inflate.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(groupImage.getGroupName());
        ((TextView) inflate.findViewById(R.id.tv_dialog_date)).setText(groupImage.getGroupDate());
        RelativeLayout rl_save_as_doc = inflate.findViewById(R.id.rl_save_as_doc);
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
                        Datas pdf = new Datas(groupImage.getGroupName(), outputFilePath, Constant.Pdf.getDataTypeId(), Constant.getDateTime("yyyy-MM-dd  hh:mm a"));
                        insertData(pdf);
                        Constant.data = pdf;
                        startActivity(new Intent(DataActivity.this, PdfViewerActivity.class));
                        finish();
                        Constant.type = "Pdf";
                        onResume();
                    }
                });
                bottomSheetDialog.dismiss();


            }
        });
        rl_save_as_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
                    @Override
                    public void onGetListImageCallBack(List<Images> list) {
                        Constant.imagesList = list;
                        startActivity(new Intent(DataActivity.this, OcrActivity.class));
                        finish();
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
                        String outputFilePath = outputDir + File.separator + groupImage.getGroupName() + ".pdf";
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

    public static void insertData(Datas datas) {
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
                        //Toast.makeText(DataActivity.this, "Call api error", Toast.LENGTH_LONG).show();
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

    public void clickOnListItem(Datas datas) {
        Constant.data = datas;
        if(Constant.type.equals("Doc")){
            startActivity(new Intent(DataActivity.this, OcrActivity.class));
            finish();
        }
        if(Constant.type.equals("Pdf")){
            startActivity(new Intent(DataActivity.this, PdfViewerActivity.class));
            finish();
        }
    }

    public void clickOnListMore(Datas datas) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.doc_more_dialog, (ViewGroup) null);
        final TextView tv_dialog_title = (TextView) inflate.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(datas.getDataName());
        ((TextView) inflate.findViewById(R.id.tv_dialog_date)).setText(datas.getDate());

        RelativeLayout rl_share = inflate.findViewById(R.id.rl_share);


        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // shareGroup(name);
                File file = new File(datas.getDataValue());
                Uri pdfFileUri = FileProvider.getUriForFile(DataActivity.this, "com.example.fileprovider", file);

                // Tạo Intent chia sẻ
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("*/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, pdfFileUri);

                // Mở hộp thoại chọn ứng dụng chia sẻ
                startActivity(Intent.createChooser(shareIntent, "Share"));
                bottomSheetDialog.dismiss();
            }
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_rename)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataName(datas);
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
                        deleteData(datas.getDataId(), new DeleteCallback() {
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
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }

    private void updateData(Datas datas, CallbackService callback) {

        IApiUserService.apiService.UpdateData(datas)
                .enqueue(new Callback<Datas>() {
                    @Override
                    public void onResponse(Call<Datas> call, Response<Datas> response) {
                        if (response.isSuccessful()) {
                            callback.onCallbackService();
                        } else {
                            Toast.makeText(DataActivity.this, "Name already exist", Toast.LENGTH_SHORT).show();
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Datas> call, Throwable t) {
                        //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateDataName(Datas datas) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.update_group_name);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final EditText editText = (EditText) dialog.findViewById(R.id.et_group_name);
        editText.setText(datas.getDataName());
        editText.setSelection(editText.length());
        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                    Toast.makeText(DataActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    datas.setDataName(editText.getText().toString());
                    updateData(datas, new CallbackService() {
                        @Override
                        public void onCallbackService() {
                            onResume();
                        }
                    });
                    dialog.dismiss();
                }
                onResume();

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

    public void deleteData(int dataId, DeleteCallback callback) {
        IApiUserService.apiService.deleteData(dataId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            callback.onDeleteCompleted();
                            Toast.makeText(DataActivity.this, "Delete succes!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(DataActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
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


}
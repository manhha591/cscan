package com.example.cscan.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscan.R;
import com.example.cscan.adapter.DataApdapter;
import com.example.cscan.adapter.DocumentAdapter;
import com.example.cscan.adapter.GroupAdapter;
import com.example.cscan.db.DBHelper;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.main_utils.ImageUtils;
import com.example.cscan.models.ChangePasswordRequest;
import com.example.cscan.models.DataTypes;
import com.example.cscan.models.Documents;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.ImageToPdfConverter;
import com.example.cscan.models.Images;
import com.example.cscan.service.DeleteCallback;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.InsertDocuentCallBack;
import com.example.cscan.service.getListDocumentCallBack;
import com.example.cscan.service.getListGroupCallBack;
import com.example.cscan.service.getListImageCallBack;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText et_search;
    protected ImageView iv_drawer;
    protected ImageView iv_group_camera;
    private ListView lv_drawer;
    public RecyclerView rv_group;
    public LinearLayout ly_empty;
    private ImageView iv_folder;
    private ImageView iv_close_search;
    private ImageView iv_clear_txt;
    private static String current_group;
    private static String document_name_current;
    protected GroupAdapter groupAdapter;

    protected DocumentAdapter documentAdapter;
    private List<Documents> ListDocument;
    public static MainActivity mainActivity;
    protected String current_mode;
    public SharedPreferences preferences;
    protected LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFormWidgets();

    }

    @Override
    protected void onResume() {
        ListDocument= null;
        getAllDocument(Constant.user_current.getUserId(), new getListDocumentCallBack() {
            @Override
            public void onGetListDocumentCallBack(List<Documents> list) {
                ListDocument = list;
                for (Documents gp : list) {
                    System.out.println(gp);
                }
                System.out.println(ListDocument);
                setDocumentAdapter();
            }

        });
        super.onResume();
    }

    private void setDocumentAdapter() {

        layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        rv_group.setLayoutManager(layoutManager);
        documentAdapter = new DocumentAdapter(MainActivity.this, ListDocument);
        rv_group.setAdapter(documentAdapter);
        documentAdapter.notifyDataSetChanged();
    }

    private void getAllDocument(int userId, getListDocumentCallBack callback) {

        IApiUserService.apiService.getAllDocument(userId)
                .enqueue(new Callback<List<Documents>>() {
                    @Override
                    public void onResponse(Call<List<Documents>> call, Response<List<Documents>> response) {
                        if (response.isSuccessful()) {
                            List<Documents> documents = response.body();
//                            for (Documents gp : documents) {
//                                System.out.println(gp);
//                            }
                            Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                            // Process the list of images as needed
                            callback.onGetListDocumentCallBack(documents);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Documents>> call, Throwable t) {
                        //Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                        Log.e("CALL API", t.getMessage());

                    }
                });
    }

    private void getFormWidgets() {
//            drawer_ly = (DrawerLayout) findViewById(R.id.drawer_ly);
//            lv_drawer = (ListView) findViewById(R.id.lv_drawer);
        iv_drawer = (ImageView) findViewById(R.id.iv_drawer);
//            rl_search_bar = (RelativeLayout) findViewById(R.id.rl_search_bar);
        iv_close_search = (ImageView) findViewById(R.id.iv_close_search);

        iv_clear_txt = (ImageView) findViewById(R.id.iv_clear_txt);
//            tag_tabs = (TabLayout) findViewById(R.id.tag_tabs);
        rv_group = (RecyclerView) findViewById(R.id.rv_group);
//        ly_empty = (LinearLayout) findViewById(R.id.ly_empty);
//            tv_empty = (TextView) findViewById(R.id.tv_empty);
        iv_folder = findViewById(R.id.iv_folder);
        iv_group_camera = (ImageView) findViewById(R.id.iv_group_camera);
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
                if (ListDocument.size() > 0) {
                    filter(editable.toString());
                }
            }
        });
    }

    public void filter(String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<Documents> it = ListDocument.iterator();
        while (it.hasNext()) {
            Documents next = it.next();
            if (next.getDocumentName().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(next);
            }
        }
        documentAdapter.filterList(arrayList);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear_txt:
                et_search.setText("");
                iv_clear_txt.setVisibility(View.GONE);//ẩn text
                return;
            case R.id.iv_drawer:
                onClickInfo();
                return;
            case R.id.iv_group_camera:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
                return;
            case R.id.iv_folder:
                openNewFolderDialog();
                return;
            default:
                return;
        }
    }
    public void clickOnListItem(Documents documents) {
        Constant.document_current = documents;
        document_name_current = Constant.document_current.getDocumentName();
        Intent intent2 = new Intent(MainActivity.this, dataTypeActivity.class);
        //intent2.putExtra("current_group", current_group);
        startActivity(intent2);
    }
//    public void clickOnListItem(GroupImage groupImage) {
//        Constant.group_current = groupImage;
//        current_group = Constant.group_current.getGroupName();
//        Intent intent2 = new Intent(MainActivity.this, GroupDocumentActivity.class);
//        intent2.putExtra("current_group", current_group);
//        startActivity(intent2);
//    }

    private void openNewFolderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.create_folder_dialog);
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        EditText et_folder_name = (EditText) dialog.findViewById(R.id.et_folder_name);


        ((TextView) dialog.findViewById(R.id.tv_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalFolderName = et_folder_name.getText().toString().trim();
                if (!finalFolderName.isEmpty()) {
                    insertDocument(finalFolderName, new InsertDocuentCallBack() {
                        @Override
                        public void onInsertDocumentCallBack(Documents documents) {
                            Constant.document_current = documents;
                            DataTypes doc = new DataTypes("Doc", Constant.document_current.getDocumentId());
                            insertDataType(doc);
                            DataTypes pdf = new DataTypes("Pdf", Constant.document_current.getDocumentId());
                            insertDataType(pdf);
                            DataTypes Image = new DataTypes("Image", Constant.document_current.getDocumentId());
                            insertDataType(Image);
                            onResume();
                        }
                    });
                    dialog.dismiss();
                } else {
                    Toast.makeText(mainActivity, "Folder name is required", Toast.LENGTH_SHORT).show();
                }


            }
        });
        ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void insertDataType(DataTypes dataTypes) {

            IApiUserService.apiService.InsertDataType(dataTypes)
                    .enqueue(new Callback<DataTypes>() {
                        @Override
                        public void onResponse(Call<DataTypes> call, Response<DataTypes> response) {
                            if (response.isSuccessful()) {
//                                DataTypes documents_current = response.body();
//                                System.out.println(documents_current);
                                //Toast.makeText(CropDocumentActivity.this, "Đăng kí thành công!", Toast.LENGTH_LONG).show();

                            } else {
                                // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                                Log.e("API Response", "Request URL: " + call.request().url());
                                Log.e("API Response", "Response Code: " + response.code());
                                Log.e("API Response", "Response Message: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<DataTypes> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                        }
                    });

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
            Constant.inputType = "Group";
            Constant.IdentifyActivity = "ScannerActivity";
            startActivity(new Intent(MainActivity.this, ScannerActivity.class));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 2);
        }
    }

    public void onClickInfo() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.infor_dialog, (ViewGroup) null);
        TextView tvUserName = (TextView) inflate.findViewById(R.id.txtUserName);
        tvUserName.setText("NAME:  " + Constant.user_current.getUsername());
        TextView tvEmail = (TextView) inflate.findViewById(R.id.txtEmail);
        tvEmail.setText("EMAIL: " + Constant.user_current.getEmail());

        TextView tvPhoneNumber = (TextView) inflate.findViewById(R.id.txtPhoneNumber);
        tvPhoneNumber.setText("PHONE: " + Constant.user_current.getPhoneNumber());

        RelativeLayout rl_logOut = inflate.findViewById(R.id.rl_LogOut);
        RelativeLayout rl_changepassword = inflate.findViewById(R.id.rl_change_pass);
        rl_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        rl_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.change_password_dialog, null);

                final EditText editTextOldPassword = view.findViewById(R.id.editTextOldPassword);
                final EditText editTextNewPassword = view.findViewById(R.id.editTextNewPassword);

                builder.setView(view)
                        .setTitle("Change Password")
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String oldPassword = editTextOldPassword.getText().toString();
                                String newPassword = editTextNewPassword.getText().toString();
                                // TODO: Handle password change logic
                                ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(Constant.user_current.getUsername(), oldPassword,newPassword);
                                    IApiUserService.apiService.changePassword(changePasswordRequest)
                                            .enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(MainActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        bottomSheetDialog.dismiss();
                                                        Intent intent = new Intent(MainActivity.this, Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
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
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }

    public void clickOnListMore(Documents documents) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.group_bottomsheet_dialog, (ViewGroup) null);
        final TextView tv_dialog_title = (TextView) inflate.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(documents.getDocumentName());
//        ((TextView) inflate.findViewById(R.id.tv_dialog_date)).setText(groupImage.getGroupDate());

        RelativeLayout rl_save_as_pdf = inflate.findViewById(R.id.rl_save_as_pdf);
        RelativeLayout rl_share = inflate.findViewById(R.id.rl_share);
        RelativeLayout rl_save_to_gallery = inflate.findViewById(R.id.rl_save_to_gallery);

        rl_save_as_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
//                    @Override
//                    public void onGetListImageCallBack(List<Images> list) {
//                        List<String> imageDatas = new ArrayList<>();
//                        for (Images images : list) {
//                            imageDatas.add(images.getImageData());
//                        }
//                        String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//                        String outputFilePath = outputDir + File.separator + groupImage.getGroupName() + ".pdf";
//                        ImageToPdfConverter.convertToPdf(imageDatas, outputFilePath);
//
//                    }
//                });
                bottomSheetDialog.dismiss();


            }
        });
//        rl_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // shareGroup(name);
//                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
//                    @Override
//                    public void onGetListImageCallBack(List<Images> list) {
//                        List<String> imageDatas = new ArrayList<>();
//                        for (Images images : list) {
//                            imageDatas.add(images.getImageData());
//                        }
//                        String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//                        String outputFilePath = outputDir + File.separator + Constant.group_current.getGroupName() + ".pdf";
//                        ImageToPdfConverter.convertToPdf(imageDatas, outputFilePath);
//                        File file = new File(outputFilePath);
//                        Uri pdfFileUri = FileProvider.getUriForFile(MainActivity.this, "com.example.fileprovider", file);
//
//                        // Tạo Intent chia sẻ
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        shareIntent.setType("application/pdf");
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfFileUri);
//
//                        // Mở hộp thoại chọn ứng dụng chia sẻ
//                        startActivity(Intent.createChooser(shareIntent, "Share PDF File"));
//                    }
//                });
//                bottomSheetDialog.dismiss();
//            }
//        });
//        ((RelativeLayout) inflate.findViewById(R.id.rl_rename)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateGroupName(groupImage);
//                bottomSheetDialog.dismiss();
//            }
//        });
//        rl_save_to_gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //new saveGroupToGallery(name).execute(new String[0]);
//                GroupDocumentActivity.getAllImage(groupImage.getGroupId(), new getListImageCallBack() {
//                    @Override
//                    public void onGetListImageCallBack(List<Images> list) {
//                        List<Images> imagesList = list;
//                        for (Images images : imagesList) {
//                            ImageUtils.saveImageToGallery(getApplicationContext(), images.getImageData());
//                        }
//                    }
//                });
//                bottomSheetDialog.dismiss();
//            }
//        });
//
//        ((RelativeLayout) inflate.findViewById(R.id.rl_delete)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Delete");
//                builder.setMessage("You want to delete this group?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Xử lý logic khi người dùng chọn xóa
//                        // Thêm mã xóa ở đây
//                        GroupDocumentActivity.deleteGroup(groupImage.getGroupId(), new DeleteCallback() {
//                            @Override
//                            public void onDeleteCompleted() {
//                                onResume();
//                            }
//                        }, MainActivity.this);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Xử lý logic khi người dùng chọn hủy
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                bottomSheetDialog.dismiss();
//            }
//        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
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
                    Toast.makeText(MainActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, "Name already exist", Toast.LENGTH_SHORT).show();
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
    private void insertDocument(String folderName, InsertDocuentCallBack callback) {
        Documents documents = new Documents(folderName, Constant.user_current.getUserId());
        IApiUserService.apiService.insertDocument(documents)
                .enqueue(new Callback<Documents>() {
                    @Override
                    public void onResponse(Call<Documents> call, Response<Documents> response) {
                        if (response.isSuccessful()) {
                            Documents documents_current = response.body();
                            System.out.println(documents_current);
                            //Toast.makeText(CropDocumentActivity.this, "Đăng kí thành công!", Toast.LENGTH_LONG).show();
                            callback.onInsertDocumentCallBack(documents_current);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Documents> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
package com.example.cscan.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cscan.R;
import com.example.cscan.adapter.DataTypeAdapter;
import com.example.cscan.adapter.DataTypeAdapter;
import com.example.cscan.adapter.GroupAdapter;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.DataTypes;
import com.example.cscan.models.DataTypes;
import com.example.cscan.models.ImageToPdfConverter;
import com.example.cscan.models.Images;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.getListDataTypeCallBack;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class dataTypeActivity extends AppCompatActivity {

    ImageView btnDoc, btnPdf, btnImage;
    private List<DataTypes> ListDataType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_type);
        //getFormWidgets();
    }

//    private void getFormWidgets() {
//        btnDoc = findViewById(R.id.btndoc);
//        btnPdf = findViewById(R.id.btnpdf);
//        btnImage = findViewById(R.id.btnImage);
//    }

    @Override
    protected void onResume() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(Constant.document_current.getDocumentName());
        ListDataType= null;
        getAllDataType(Constant.document_current.getDocumentId(), new getListDataTypeCallBack() {
            @Override
            public void ononGetListDataTypeCallBack(List<DataTypes> list) {
                ListDataType = list;
                for (DataTypes gp : ListDataType) {
                    System.out.println(gp);
                    if(gp.getDataTypeName().equals("Doc")){
                        Constant.Doc = gp;
                    }else if(gp.getDataTypeName().equals("Pdf")){
                        Constant.Pdf = gp;
                    }else {
                        Constant.Image = gp;
                    }
                }

            }
        });
        super.onResume();
    }
    private void getAllDataType(int documentId, getListDataTypeCallBack callBack) {

        IApiUserService.apiService.getAllDataType(documentId)
                .enqueue(new Callback<List<DataTypes>>() {
                    @Override
                    public void onResponse(Call<List<DataTypes>> call, Response<List<DataTypes>> response) {
                        if (response.isSuccessful()) {
                            List<DataTypes> dataTypes = response.body();
//                            for (DataTypes gp : DataTypes) {
//                                System.out.println(gp);
//                            }
                           // Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                            // Process the list of images as needed
                            callBack.ononGetListDataTypeCallBack(dataTypes);
                        } else {
                            // Toast.makeText(CropDataTypeActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DataTypes>> call, Throwable t) {
                        //Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                        Log.e("CALL API", t.getMessage());

                    }
                });
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;

            case R.id.iv_doc_camera:
                Constant.inputType = "Group";
                startActivity(new Intent(dataTypeActivity.this, ScannerActivity.class));
                return;
            case R.id.btndoc:
                Constant.type = "Doc";
                startActivity(new Intent(dataTypeActivity.this, DataActivity.class));
                return;
            case R.id.btnpdf:
                Constant.type = "Pdf";
                startActivity(new Intent(dataTypeActivity.this, DataActivity.class));
                return;
            case R.id.btnImage:
                Constant.type = "Image";
                startActivity(new Intent(dataTypeActivity.this, DataActivity.class));
                return;
            default:
                return;
        }
    }

}
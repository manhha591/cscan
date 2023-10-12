package com.example.cscan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cscan.R;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.User;
import com.example.cscan.service.CallbackUser;
import com.example.cscan.service.IApiUserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    Button btn_reg;
    EditText txt_user, txt_pass, txt_repass, txt_email, txt_sdt;

    int requestCode;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        txt_user = findViewById(R.id.username);
        txt_email = findViewById(R.id.email);
        txt_sdt = findViewById(R.id.phonenumber);
        txt_pass = findViewById(R.id.password);
//        txt_repass = findViewById(R.id.repassword);

        btn_reg = findViewById(R.id.btnReg);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txt_user.getText().toString();
                String password = txt_pass.getText().toString();
                String email = txt_email.getText().toString();
                String phoneNumber = txt_sdt.getText().toString();



                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(Register.this, "Bạn cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if(txt_pass.length() != txt_repass.length()){
//                    showAlertDialog("Lỗi", "Mật khẩu không khớp");
//                    return;
//                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showAlertDialog("Lỗi", "Email không hợp lệ");
                    return;
                }

                if (phoneNumber.length() != 10) {
                    showAlertDialog("Lỗi", "Số điện thoại phải có 10 chữ số");
                    return;
                }

                User us = new User(username, password, email, phoneNumber);
                callApiRegister(us, new CallbackUser() {
                    @Override
                    public void onCallbackUser(User user) {
                        Constant.user_current = user;
                        finish();
                    }
                });
            }
        });
    }
//    private void changeStatusBarColor() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
//        }
//    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,Login.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callApiRegister(User user, CallbackUser callback) {
        IApiUserService.apiService.registerUser(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User u1 = response.body();
                            Toast.makeText(Register.this, "Success!", Toast.LENGTH_LONG).show();
                            callback.onCallbackUser(u1);
                        } else {
                            Toast.makeText(Register.this, "Fail!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(Register.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }


}
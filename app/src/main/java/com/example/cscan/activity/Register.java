package com.example.cscan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    EditText txt_user, txt_pass, txt_email, txt_sdt;

    int requestCode;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_user = findViewById(R.id.username);
        txt_email = findViewById(R.id.email);
        txt_sdt = findViewById(R.id.phonenumber);
        txt_pass = findViewById(R.id.password);

        btn_reg = findViewById(R.id.btnReg);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txt_user.getText().toString();
                String password = txt_pass.getText().toString();
                String email = txt_email.getText().toString();
                String phoneNumber = txt_sdt.getText().toString();


                if (username.isEmpty() && password.isEmpty() && email.isEmpty() && phoneNumber.isEmpty()) {
                    Toast.makeText(Register.this, "Bạn cần nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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

    private void callApiRegister(User user, CallbackUser callback) {
        IApiUserService.apiService.registerUser(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User u1 = response.body();
                            Toast.makeText(Register.this, "Đăng kí thành công!", Toast.LENGTH_LONG).show();
                            callback.onCallbackUser(u1);
                        } else {
                            Toast.makeText(Register.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
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
package com.example.cscan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cscan.R;
import com.example.cscan.activity.MainActivity;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.User;
import com.example.cscan.service.CallbackUser;
import com.example.cscan.service.IApiUserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText txt_user, txt_pass;
    Button btn_reg, btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        btn_reg = findViewById(R.id.btnRegInLayoutLog);
        btn_login = findViewById(R.id.btnLogin);

        txt_user = findViewById(R.id.edit_txt_username);
        txt_pass = findViewById(R.id.edit_txt_pass);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String us = txt_user.getText().toString();
                String pass = txt_pass.getText().toString();
                User user = new User(us, pass, us, us);
                callApiLogin(user, new CallbackUser() {
                    @Override
                    public void onCallbackUser(User user) {
                        Constant.user_current = user;
                        System.out.println(Constant.user_current);
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
 
    private void callApiLogin(User user, CallbackUser callback) {
        IApiUserService.apiService.login(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User u1 = response.body();
                            callback.onCallbackUser(u1);
                            //mới map sang main chưa truyền data
                        } else {
                            Toast.makeText(Login.this, "username or password is incorrect !", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(Login.this, "Call api error", Toast.LENGTH_LONG).show();
                        Log.e("API Response", "Request URL: " + t.getMessage());

                    }
                });
    }

}
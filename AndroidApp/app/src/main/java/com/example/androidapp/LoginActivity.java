package com.example.androidapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.model.SqlServerHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtLoginUser, edtLoginPass;
    private Button btnLogin, btnRegisterNow;
    private TextView tvForgotPass;
    private SqlServerHelper sqlServerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#7FD8BE"));
            window.setNavigationBarColor(Color.parseColor("#04221B"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        sqlServerHelper = new SqlServerHelper();

        edtLoginUser = findViewById(R.id.edtLoginUser);
        edtLoginPass = findViewById(R.id.edtLoginPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterNow = findViewById(R.id.btnRegisterNow);
        tvForgotPass = findViewById(R.id.tvForgotPass);

        // Bấm Sign In -> Xác thực trực tiếp với SQL Server (.\SQLEXPRESS -> BadmintonDB)
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtLoginUser.getText().toString().trim();
                String pass = edtLoginPass.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Vui lòng nhập Tài khoản (khach / nhanvien / quanly) và Mật khẩu!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                btnLogin.setEnabled(false);
                sqlServerHelper.authenticateAsync(user, pass, new SqlServerHelper.AuthCallback() {
                    @Override
                    public void onSuccess(SqlServerHelper.UserAccount account) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thành công (SQL Server): " + account.fullName,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, DaviesArenaActivity.class);
                        intent.putExtra("USER_ROLE", account.role);
                        intent.putExtra("USER_NAME", account.fullName);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Bấm Create an account -> Chuyển sang màn hình Đăng ký (ghi trực tiếp vào SQL Server)
        btnRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,
                        "SQL Server BadmintonDB: khach / nhanvien / quanly (MK: 123456)",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

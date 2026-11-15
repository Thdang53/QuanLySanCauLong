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

        // Bấm Đăng nhập -> Xác thực trực tiếp với SQL Server (.\SQLEXPRESS -> BadmintonDB)
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rawUser = edtLoginUser.getText().toString().trim();
                String pass = edtLoginPass.getText().toString().trim();

                if (rawUser.isEmpty() || pass.isEmpty()) {
                    UiPopupHelper.showWarningWithIcon(
                            LoginActivity.this,
                            "⚠️",
                            "THIẾU THÔNG TIN",
                            "Chưa nhập đủ thông tin",
                            "Vui lòng nhập đầy đủ Tài khoản (khach / nhanvien / quanly) và Mật khẩu để đăng nhập vào hệ thống."
                    );
                    return;
                }

                String user = rawUser.toLowerCase();
                if ("khachhang@demo.vn".equals(user)) user = "khach";
                else if ("nhanvien@demo.vn".equals(user)) user = "nhanvien";
                else if ("chusan@demo.vn".equals(user)) user = "quanly";

                btnLogin.setEnabled(false);
                sqlServerHelper.authenticateAsync(user, pass, new SqlServerHelper.AuthCallback() {
                    @Override
                    public void onSuccess(final SqlServerHelper.UserAccount account) {
                        btnLogin.setEnabled(true);
                        String roleVi;
                        if ("ADMIN".equalsIgnoreCase(account.role)) {
                            roleVi = "Chủ sân / Quản lý";
                        } else if ("STAFF".equalsIgnoreCase(account.role)) {
                            roleVi = "Nhân viên trực quầy";
                        } else {
                            roleVi = "Khách hàng đặt sân";
                        }

                        UiPopupHelper.showSuccessWithAction(
                                LoginActivity.this,
                                "🎉",
                                "ĐĂNG NHẬP THÀNH CÔNG",
                                "Đăng nhập thành công!",
                                "Xin chào " + account.fullName + "!\n"
                                        + "Vai trò: " + roleVi + "\n"
                                        + "Đã kết nối trực tiếp cơ sở dữ liệu BadmintonDB.",
                                "Vào hệ thống ngay",
                                1500,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(LoginActivity.this, DaviesArenaActivity.class);
                                        intent.putExtra("USER_ROLE", account.role);
                                        intent.putExtra("USER_NAME", account.fullName);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(String message) {
                        btnLogin.setEnabled(true);
                        UiPopupHelper.showError(
                                LoginActivity.this,
                                "Đăng nhập thất bại",
                                message
                        );
                    }
                });
            }
        });

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
                UiPopupHelper.showInfo(
                        LoginActivity.this,
                        "🔑",
                        "TÀI KHOẢN HỆ THỐNG",
                        "Thông tin đăng nhập SQL Server",
                        "• Khách hàng: khach (Mật khẩu: 123456)\n"
                                + "• Nhân viên: nhanvien (Mật khẩu: 123456)\n"
                                + "• Quản lý sân: quanly (Mật khẩu: 123456)"
                );
            }
        });
    }
}

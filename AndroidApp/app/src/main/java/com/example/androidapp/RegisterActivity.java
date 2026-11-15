package com.example.androidapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.model.SqlServerHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtRegEmail, edtRegUser, edtRegPass, edtRegConfirmPass;
    private Button btnRegSignIn, btnRegCancel;
    private SqlServerHelper sqlServerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#7FD8BE"));
            window.setNavigationBarColor(Color.parseColor("#04221B"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        sqlServerHelper = new SqlServerHelper();

        edtRegEmail = findViewById(R.id.edtRegEmail);
        edtRegUser = findViewById(R.id.edtRegUser);
        edtRegPass = findViewById(R.id.edtRegPass);
        edtRegConfirmPass = findViewById(R.id.edtRegConfirmPass);
        btnRegSignIn = findViewById(R.id.btnRegSignIn);
        btnRegCancel = findViewById(R.id.btnRegCancel);

        btnRegSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtRegEmail.getText().toString().trim();
                final String user = edtRegUser.getText().toString().trim();
                String pass = edtRegPass.getText().toString().trim();
                String confirmPass = edtRegConfirmPass.getText().toString().trim();

                if (email.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                    UiPopupHelper.showWarningWithIcon(
                            RegisterActivity.this,
                            "⚠️",
                            "THIẾU THÔNG TIN",
                            "Chưa nhập đủ thông tin",
                            "Vui lòng điền đầy đủ Số điện thoại/Email, Tên tài khoản và Mật khẩu để đăng ký."
                    );
                    return;
                }

                if (!pass.equals(confirmPass)) {
                    UiPopupHelper.showError(
                            RegisterActivity.this,
                            "Mật khẩu không khớp",
                            "Mật khẩu xác nhận không trùng khớp với mật khẩu đã nhập. Vui lòng kiểm tra lại!"
                    );
                    return;
                }

                btnRegSignIn.setEnabled(false);
                sqlServerHelper.registerCustomerAsync(user, email, user, pass, new SqlServerHelper.RegisterCallback() {
                    @Override
                    public void onSuccess(String message) {
                        btnRegSignIn.setEnabled(true);
                        UiPopupHelper.showSuccessWithAction(
                                RegisterActivity.this,
                                "🎉",
                                "ĐĂNG KÝ THÀNH CÔNG",
                                "Tạo tài khoản thành công!",
                                "Tài khoản @" + user + " (" + email + ") đã được lưu vào bảng NGUOI_DUNG trong SQL Server.\nBạn có thể đăng nhập ngay bây giờ.",
                                "Quay lại Đăng nhập",
                                1800,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(String message) {
                        btnRegSignIn.setEnabled(true);
                        UiPopupHelper.showError(
                                RegisterActivity.this,
                                "Đăng ký không thành công",
                                message
                        );
                    }
                });
            }
        });

        btnRegCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

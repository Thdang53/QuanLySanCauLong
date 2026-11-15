package com.example.androidapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                String email = edtRegEmail.getText().toString().trim();
                String user = edtRegUser.getText().toString().trim();
                String pass = edtRegPass.getText().toString().trim();
                String confirmPass = edtRegConfirmPass.getText().toString().trim();

                if (email.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ tất cả thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass.equals(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnRegSignIn.setEnabled(false);
                sqlServerHelper.registerCustomerAsync(user, email, user, pass, new SqlServerHelper.RegisterCallback() {
                    @Override
                    public void onSuccess(String message) {
                        btnRegSignIn.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        btnRegSignIn.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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

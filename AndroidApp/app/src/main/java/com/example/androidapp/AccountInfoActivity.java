package com.example.androidapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AccountInfoActivity extends AppCompatActivity {

    private EditText edtInfoName, edtInfoEmail, edtInfoUser, edtInfoPass;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private TextView btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        edtInfoName = findViewById(R.id.edtInfoName);
        edtInfoEmail = findViewById(R.id.edtInfoEmail);
        edtInfoUser = findViewById(R.id.edtInfoUser);
        edtInfoPass = findViewById(R.id.edtInfoPass);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSave);

        // Nhận dữ liệu username từ LoginActivity nếu có
        String passedUser = getIntent().getStringExtra("USERNAME_EXTRA");
        if (passedUser != null && !passedUser.isEmpty()) {
            edtInfoUser.setText(passedUser);
            edtInfoName.setText(passedUser);
        }

        // Mặc định chọn Male
        rbMale.setChecked(true);

        // Bấm nút SAVE trên thanh tiêu đề
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtInfoName.getText().toString().trim();
                String gender = rbMale.isChecked() ? "Male (Nam)" : "Female (Nữ)";

                Toast.makeText(AccountInfoActivity.this, "Đã lưu tài khoản: " + name + " (" + gender + ")", Toast.LENGTH_LONG).show();
            }
        });
    }
}

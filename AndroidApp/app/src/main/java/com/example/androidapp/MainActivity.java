package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtName;
    private Button btnSend;
    private TextView txtPrice;

    // Sử dụng registerForActivityResult để tạo hàm getQuote lắng nghe dữ liệu trả về
    private ActivityResultLauncher<Intent> getQuote = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == 1) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Bundle bundle = intent.getExtras();
                        String priceQuote = bundle.getString("priceQuote");
                        txtPrice.setText(priceQuote);
                    }
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ các widget
        edtName = findViewById(R.id.edtName);
        btnSend = findViewById(R.id.btnSend);
        txtPrice = findViewById(R.id.txtPrice);

        // 2. Tạo sự kiện click vào nút "Gửi" để gửi tên sản phẩm cần báo giá
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReceiveActivity.class);
                intent.putExtra("name", edtName.getText().toString());
                getQuote.launch(intent);
            }
        });
    }
}
package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiveActivity extends AppCompatActivity {

    private TextView txtItem;
    private EditText edtPrice;
    private Button btnQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        // 1. Ánh xạ các widget
        txtItem = findViewById(R.id.txtItem);
        edtPrice = findViewById(R.id.edtPrice);
        btnQuote = findViewById(R.id.btnQuote);

        // 2. Lấy tên sản phẩm cần báo giá từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            txtItem.setText(name);
        }

        // 3. Tạo sự kiện trong nút "báo giá" gửi kết quả trả về Activity ban đầu
        btnQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceQuote = edtPrice.getText().toString();
                Intent intentResult = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("priceQuote", priceQuote);
                intentResult.putExtras(bundle);
                setResult(1, intentResult);
                finish(); // Đóng Activity 2 để quay về Activity 1
            }
        });
    }
}

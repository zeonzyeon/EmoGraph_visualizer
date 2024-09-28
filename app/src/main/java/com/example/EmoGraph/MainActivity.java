package com.example.EmoGraph;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 녹음 화면으로 이동하는 버튼 찾기
        Button btnRecord = findViewById(R.id.btn_record);

        // 버튼 클릭 리스너 설정
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RecordActivity로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);  // 녹음 화면으로 이동
            }
        });
    }
}

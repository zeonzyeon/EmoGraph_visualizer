package com.example.EmoGraph;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);  // 녹음 화면 레이아웃 설정

        // 뒤로 가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RecordActivity를 종료하고 이전 화면(MainActivity)로 돌아감
                finish();
            }
        });
    }
}

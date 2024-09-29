package com.example.EmoGraph;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;  // AppCompatActivity를 상속받도록 수정

public class AiMessageActivity extends AppCompatActivity {  // Activity 대신 AppCompatActivity 상속

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_message);  // 올바른 레이아웃 파일 연결
    }
}

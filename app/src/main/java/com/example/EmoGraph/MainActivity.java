package com.example.EmoGraph;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RecordActivity로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);  // 녹음 화면으로 이동
            }
        });

        // 감정 상태 기록 화면으로 이동하는 버튼 찾기
        Button btnWriteEmotion = findViewById(R.id.btn_write_emotion);
        btnWriteEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EmotionRecordActivity로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, WriteEmotionActivity.class);
                startActivity(intent);  // 감정 기록 화면으로 이동
            }
        });

        // EmoGraph 화면으로 이동하는 버튼 찾기
        Button btnEmoGraph = findViewById(R.id.btn_emograph);
        btnEmoGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EmoGraphActivity로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, EmoGraphActivity.class);
                startActivity(intent);  // EmoGraph 화면으로 이동
            }
        });

        // Ai 메시지 화면으로 이동하는 버튼 찾기
        Button btnAiMessage = findViewById(R.id.btn_ai_message);
        btnAiMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AiMessageActivity로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, AiMessageActivity.class);
                startActivity(intent);  // Ai 메시지 화면으로 이동
            }
        });
    }
}

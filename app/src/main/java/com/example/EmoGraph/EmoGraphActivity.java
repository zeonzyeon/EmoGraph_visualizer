package com.example.EmoGraph;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EmoGraphActivity extends AppCompatActivity {

    private CustomGraphView customGraphView;
    private List<Integer> emotionScores = new ArrayList<>();  // 여러 감정 점수를 저장할 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emograph);

        // SharedPreferences에서 여러 감정 점수를 불러오기
        SharedPreferences sharedPreferences = getSharedPreferences("your_shared_preferences_name", MODE_PRIVATE);
        int size = sharedPreferences.getInt("emotionScores_size", 0);

        // 여러 감정 점수 리스트에 데이터 추가
        for (int i = 0; i < size; i++) {
            int score = sharedPreferences.getInt("emotionScore_" + i, -1);
            if (score != -1) {
                emotionScores.add(score);
            }
        }

        // CustomGraphView 찾기
        customGraphView = findViewById(R.id.customGraphView);

        // 감정 점수 리스트로 그래프 그리기
        customGraphView.setEmotionScores(emotionScores);
    }
}

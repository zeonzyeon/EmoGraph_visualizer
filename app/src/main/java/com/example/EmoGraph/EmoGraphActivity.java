package com.example.EmoGraph;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class EmoGraphActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private List<String> emotionData = new ArrayList<>();
    private List<String> recordingData = new ArrayList<>();
    private CustomGraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emograph);  // 레이아웃 연결

        // 그래프를 그릴 커스텀 뷰
        graphView = findViewById(R.id.customGraphView);

        // 감정 기록과 녹음 데이터를 가져오기 위해 SharedPreferences 사용
        sharedPreferences = getSharedPreferences("EmoGraphPrefs", MODE_PRIVATE);

        // 저장된 감정 점수 가져오기
        int emotionScore = sharedPreferences.getInt("emotionScore", 0);
        if (emotionScore != 0) {
            emotionData.add("Emotion Score: " + emotionScore);
        } else {
            emotionData.add("No emotion data found.");
        }

        // 혼잣말 녹음 파일을 저장하고 있는 리스트 (예시 데이터를 추가)
        // 실제로는 녹음 파일과 연결된 데이터를 추가해야 함
        recordingData.add("녹음 파일 1");
        recordingData.add("녹음 파일 2");
        recordingData.add("녹음 파일 3");

        // 텍스트로 데이터 확인 (디버깅용)
        TextView emotionTextView = findViewById(R.id.emotionDataText);
        TextView recordingTextView = findViewById(R.id.recordingDataText);
        emotionTextView.setText(emotionData.toString());
        recordingTextView.setText(recordingData.toString());

        // 그래프 그리기
        graphView.setData(emotionData, recordingData);  // 그래프 뷰에 데이터 전달
    }
}
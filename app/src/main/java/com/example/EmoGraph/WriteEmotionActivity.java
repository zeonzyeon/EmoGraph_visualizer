package com.example.EmoGraph;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteEmotionActivity extends AppCompatActivity {

    private EditText emotionInput;
    private TextView displayRecord;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_emotion);

        // View 초기화
        emotionInput = findViewById(R.id.editText_emotion);
        displayRecord = findViewById(R.id.textView_record);
        Button saveEmotionButton = findViewById(R.id.btn_save_emotion);

        // SharedPreferences를 사용하여 기록 저장
        sharedPreferences = getSharedPreferences("EmotionRecord", MODE_PRIVATE);

        // 저장된 감정 상태 가져오기
        String savedRecord = sharedPreferences.getString("record", "No record found.");
        displayRecord.setText(savedRecord);

        // 저장 버튼 클릭 리스너
        saveEmotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emotion = emotionInput.getText().toString();
                String currentDateTime = getCurrentDateTime();

                if (!emotion.isEmpty()) {
                    // 새로운 기록을 이전 기록에 추가
                    String newRecord = "Date: " + currentDateTime + "\nEmotion: " + emotion + "\n\n";
                    String allRecords = sharedPreferences.getString("records", "");  // 모든 기록을 가져옴
                    allRecords = newRecord + allRecords;  // 새로운 기록을 기존 기록 위에 추가

                    // 기록을 SharedPreferences에 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("records", allRecords);  // 모든 기록 저장
                    editor.putString("latestEmotionRecord", emotion); // 가장 최신 감정 상태를 별도로 저장
                    editor.apply();

                    // 화면에 모든 기록 표시
                    displayRecord.setText(allRecords);

                    // 입력 필드 초기화
                    emotionInput.setText("");
                }
            }
        });

    }

    // 현재 날짜 및 시간을 반환하는 함수
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}

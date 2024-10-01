package com.example.EmoGraph;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.EmoGraph.api.AIRequest;
import com.example.EmoGraph.api.AIResponse;
import com.example.EmoGraph.api.OpenAIApiService;
import com.example.EmoGraph.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiMessageActivity extends AppCompatActivity {

    private TextView aiMessageTextView;
    private Button fetchMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_message);  // XML 파일에서 activity_ai_message로 설정하세요

        aiMessageTextView = findViewById(R.id.aiMessageTextView);  // XML에서 정의한 텍스트뷰 ID에 맞춰 수정
        fetchMessageButton = findViewById(R.id.fetchMessageButton);  // AI 메시지 가져오는 버튼 ID

        fetchMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAIMessage();
            }
        });
    }

    private void fetchAIMessage() {
        OpenAIApiService apiService = RetrofitClient.getRetrofitInstance().create(OpenAIApiService.class);

        // OpenAI에 요청할 메시지를 설정
        AIRequest request = new AIRequest("text-davinci-003", "오늘 나에게 응원의 메시지를 보내줘.", 0.7, 50);

        // API 호출
        Call<AIResponse> call = apiService.getAIMessage(request);
        call.enqueue(new Callback<AIResponse>() {
            @Override
            public void onResponse(Call<AIResponse> call, Response<AIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 응답 처리
                    Log.d("API Response", "응답 내용: " + response.body().toString());  // 응답 내용을 로그로 출력
                    String aiMessage = response.body().getChoices().get(0).getText();  // getText()로 수정
                    aiMessageTextView.setText(aiMessage);
                    Log.d("API Response", "응답 메시지: " + aiMessage);  // 성공 메시지 로그 출력
                } else {
                    // 실패한 응답 메시지와 코드를 출력
                    Log.e("API Response", "응답 실패: " + response.message() + ", 코드: " + response.code());
                    Toast.makeText(AiMessageActivity.this, "AI 응답을 처리할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onFailure(Call<AIResponse> call, Throwable t) {
                Log.e("API Response", "API 호출 실패: " + t.getMessage());
                Toast.makeText(AiMessageActivity.this, "AI 메시지를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

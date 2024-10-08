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
import com.example.EmoGraph.api.Message;
import com.example.EmoGraph.api.OpenAIApiService;
import com.example.EmoGraph.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;
import android.os.Looper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AiMessageActivity extends AppCompatActivity {

    private TextView aiMessageTextView;
    private Button fetchMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_message);  // XML 파일

        aiMessageTextView = findViewById(R.id.aiMessageTextView);  // XML에서 정의한 텍스트뷰 ID에 맞춰 수정
        fetchMessageButton = findViewById(R.id.fetchMessageButton);  // AI 메시지 가져오는 버튼 ID

        fetchMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAIMessage(0);
            }
        });
    }

    private void fetchAIMessage(int retryCount) {
        OpenAIApiService apiService = getRetrofitInstance().create(OpenAIApiService.class);

        // OpenAI에 요청할 메시지를 설정
        AIRequest request = new AIRequest(
                "gpt-3.5-turbo",
                Arrays.asList(new Message("user", "오늘 나에게 응원의 메시지를 보내줘.")),  // messages 파라미터 추가
                0.7,  // temperature
                100   // max_tokens
        );

        // API 호출
        Call<AIResponse> call = apiService.getAIMessage(request);
        call.enqueue(new Callback<AIResponse>() {
            @Override
            public void onResponse(Call<AIResponse> call, Response<AIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 경우
                    String aiMessage = response.body().getChoices().get(0).getMessage().getContent();
                    aiMessageTextView.setText(aiMessage);
                } else if (response.code() == 429) {
                    // 속도 제한에 걸린 경우 비동기로 1초 대기 후 다시 시도
                    if (retryCount < 5) {  // 최대 5번까지 재시도
                        int backoffTime = (int) Math.pow(2, retryCount);  // 백오프 시간 (2의 제곱으로 점점 늘어남)
                        Log.e("API Response", "429 Too Many Requests: " + backoffTime + "초 후 다시 시도합니다.");

                        // Handler를 이용하여 비동기적으로 대기
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            fetchAIMessage(retryCount + 1);  // 재시도
                        }, backoffTime * 1000);  // 백오프 시간만큼 대기
                    } else {
                        // 최대 재시도 횟수를 초과했을 경우
                        Toast.makeText(AiMessageActivity.this, "요청 실패: 최대 재시도 횟수를 초과했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        // 실패한 응답 처리 및 상세 오류 본문 출력
                        Log.e("API Response", "응답 실패: " + response.message() + ", 코드: " + response.code() + ", 오류 본문: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("API Response", "오류 본문 파싱 중 예외 발생: " + e.getMessage());
                    }
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

    private Retrofit getRetrofitInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 타임아웃 설정
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

package com.example.EmoGraph;

import android.os.AsyncTask;
import android.util.Log;
import android.content.SharedPreferences;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmotionScoreTask extends AsyncTask<String, Void, Integer> {
    private static final String TAG = "EmotionScoreTask";
    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "YOUR_API_KEY";

    private EmotionScoreListener listener;
    private SharedPreferences sharedPreferences;

    public EmotionScoreTask(EmotionScoreListener listener, SharedPreferences sharedPreferences) {
        this.listener = listener;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String transcript = params[0];
        String emotionRecord = params[1];

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject promptJson = new JSONObject();
            String prompt = "오늘의 감정 상태를 평가해줘. 음성 텍스트: " + transcript + " 기록된 감정 상태: " + emotionRecord + ". 이 내용을 바탕으로 0에서 100까지의 감정 점수를 숫자로만 응답해줘.";

            promptJson.put("model", "text-davinci-003");
            promptJson.put("prompt", prompt);
            promptJson.put("max_tokens", 10);
            promptJson.put("temperature", 0.7);

            RequestBody body = RequestBody.create(promptJson.toString(), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                String resultText = responseJson.getJSONArray("choices").getJSONObject(0).getString("text").trim();

                try {
                    return Integer.parseInt(resultText);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "GPT 응답 형식 오류: " + resultText);
                    return -1; // 잘못된 형식의 응답 처리
                }
            } else {
                Log.e(TAG, "응답 실패: " + response.message());
            }
        } catch (IOException | org.json.JSONException e) {
            Log.e(TAG, "오류 발생: " + e.getMessage());
        }

        return -1; // 오류가 발생했을 때의 처리
    }

    @Override
    protected void onPostExecute(Integer score) {
        if (listener != null) {
            listener.onEmotionScoreReceived(score);
        }
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("emotionScore", score);
            editor.apply();
            Log.d(TAG, "감정 점수가 SharedPreferences에 저장되었습니다: " + score);
        }
    }

    public interface EmotionScoreListener {
        void onEmotionScoreReceived(int score);
    }
}

// 사용 예시
// EmotionScoreTask task = new EmotionScoreTask(score -> {
//     if (score != -1) {
//         Log.d("MainActivity", "오늘의 감정 점수: " + score);
//         // 그래프에 점수를 추가하고 업데이트
//     } else {
//         Log.e("MainActivity", "감정 점수 계산 실패");
//     }
// }, sharedPreferences);
// task.execute(transcript, emotionRecord);
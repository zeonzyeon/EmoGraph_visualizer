package com.example.EmoGraph;

import android.os.AsyncTask;
import android.util.Log;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmotionScoreTask extends AsyncTask<String, Void, Integer> {
    private static final String TAG = "EmotionScoreTask";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    private EmotionScoreTask.Callback callback;
    private SharedPreferences sharedPreferences;

    public EmotionScoreTask(EmotionScoreTask.Callback callback, SharedPreferences sharedPreferences) {
        this.callback = callback;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String transcript = params[0];
        String emotionRecord = params[1];

        OkHttpClient client = new OkHttpClient();

        try {
            // JSON 객체 생성
            JSONObject promptJson = new JSONObject();
            JSONArray messagesArray = new JSONArray();  // 메시지 배열 생성

            // 사용자 메시지 생성
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");  // 역할: 'user'
            userMessage.put("content", "오늘의 감정 상태를 평가해줘. 음성 텍스트: " + transcript + " 기록된 감정 상태: " + emotionRecord + ". 이 내용을 바탕으로 0에서 100까지의 감정 점수를 매겨줘. ex) 55");
            messagesArray.put(userMessage);

            // 모델과 메시지 설정
            promptJson.put("model", "gpt-3.5-turbo");
            promptJson.put("messages", messagesArray);  // 메시지 배열 추가
            promptJson.put("max_tokens", 10);
            promptJson.put("temperature", 0.7);

            // HTTP 요청 준비
            RequestBody body = RequestBody.create(promptJson.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(API_URL)  // URL 수정
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            String responseBodyString = response.body().string();  // 응답 본문을 변수에 저장
            Log.d(TAG, "응답 내용: " + responseBodyString);

            if (response.isSuccessful()) {
                JSONObject responseJson = new JSONObject(responseBodyString);
                String resultText = responseJson.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim();

                try {
                    return Integer.parseInt(resultText.replace("점", "").trim());  // "50점" -> "50" 추출 후 변환
                } catch (NumberFormatException e) {
                    Log.e(TAG, "응답 형식 오류: " + resultText);
                    return -1;
                }
            } else {
                Log.e(TAG, "응답 실패: " + response.message());
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "오류 발생: " + e.getMessage());
        }
        return -1;  // 오류가 발생한 경우
    }

    @Override
    protected void onPostExecute(Integer score) {
        if (callback != null) {
            if (score != -1) {
                callback.onEmotionScoreReceived(score);
            } else {
                callback.onEmotionScoreError();
            }
        }
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("emotionScore", score);
            editor.apply();
            Log.d(TAG, "감정 점수가 SharedPreferences에 저장되었습니다: " + score);
        }
    }

    public interface Callback {
        void onEmotionScoreReceived(int score);

        void onEmotionScoreError();
    }
}
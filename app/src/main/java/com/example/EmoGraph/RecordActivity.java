package com.example.EmoGraph;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordActivity extends AppCompatActivity {
    ImageButton audioRecordImageBtn;
    TextView audioRecordText;

    // 오디오 권한
    private final String recordPermission = Manifest.permission.RECORD_AUDIO;
    private final int PERMISSION_CODE = 21;

    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName;
    private boolean isRecording = false;
    private Uri audioUri = null; // 오디오 파일 uri

    // 오디오 파일 목록 관련 변수
    private ArrayList<Uri> audioList;
    private ArrayList<String> transcriptList; // 인식된 텍스트를 저장할 리스트
    private AudioAdapter audioAdapter;

    // 오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);  // 녹음 화면 레이아웃 설정

        // 리스트 초기화
        audioList = new ArrayList<>();
        transcriptList = new ArrayList<>();

        // 어댑터 초기화 (리스트 초기화 후 설정)
        audioAdapter = new AudioAdapter(this, audioList, transcriptList);

        // 리사이클러뷰 초기화
        RecyclerView audioRecyclerView = findViewById(R.id.audioRecyclerview);
        audioRecyclerView.setAdapter(audioAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(layoutManager);

        // 뷰 초기화
        audioRecordImageBtn = findViewById(R.id.audioRecordImageBtn);
        audioRecordText = findViewById(R.id.audioRecordText);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("EmoGraphPrefs", MODE_PRIVATE);
        if (sharedPreferences == null) {
            Log.e("RecordActivity", "SharedPreferences 초기화 실패");
        }

        // 녹음 버튼 클릭 리스너 설정
        audioRecordImageBtn.setOnClickListener(view -> {
            if (isRecording) {
                stopRecording();
                isRecording = false;
                audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.start_recording));
                audioRecordText.setText("녹음 시작");

                // 현재 녹음 파일의 위치
                int position = audioList.size() - 1;

                // 음성을 텍스트로 변환 (파일의 위치 정보도 전달)
                convertSpeechToText(audioFileName, position);
            } else {
                if (checkAudioPermission()) {
                    startRecording();
                    isRecording = true;
                    audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.recording_red));
                    audioRecordText.setText("녹음 중");
                }
            }
        });

        // RecyclerView의 각 아이템에 대한 클릭 리스너 설정
        audioAdapter.setOnItemClickListener((view, position) -> {
            Uri uri = audioList.get(position);
            ImageButton playBtn = view.findViewById(R.id.playBtn_itemAudio);
            if (isPlaying) {
                stopPlaying();
                isPlaying = false;
                playBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.audio_play)); // 녹음 시작 이미지로 변경
            } else {
                startPlaying(uri, playBtn);
                isPlaying = true;
                playBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.audio_pause)); // 재생 중 이미지로 변경
            }
        });
    }

    // 오디오 파일 권한 체크
    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    // 녹음 시작
    private void startRecording() {
        // 녹음 파일 경로 설정
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());

        // 파일 이름 생성 및 확장자 설정 (.3gp)
        audioFileName = generateUniqueFileName(recordPath, timeStamp + "_emotion", ".3gp");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFileName);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d("RecordActivity", "녹음 시작됨: " + audioFileName);
        } catch (IOException | IllegalStateException e) {
            Log.e("RecordActivity", "녹음 시작 중 오류 발생: " + e.getMessage());
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    // 녹음 종료
    private void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (RuntimeException e) {
            Log.e("RecordActivity", "녹음 중지 오류: " + e.getMessage());
        } finally {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        // 녹음된 파일을 Uri로 변환하여 리스트에 추가
        audioUri = Uri.fromFile(new File(audioFileName));
        audioList.add(audioUri);
        transcriptList.add(""); // 빈 문자열을 추가하여 transcriptList와 audioList의 크기를 맞춤
        audioAdapter.notifyDataSetChanged();
        Log.d("RecordActivity", "녹음된 파일 리스트 갱신: " + audioFileName);
    }

    // 오디오 재생 시작
    private void startPlaying(Uri uri, ImageButton playBtn) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
            mediaPlayer.prepareAsync(); // 비동기 준비를 통해 준비가 완료되면 재생 시작

            mediaPlayer.setOnCompletionListener(mp -> {
                stopPlaying();
                isPlaying = false;
                playBtn.setImageDrawable(ContextCompat.getDrawable(playBtn.getContext(), R.drawable.audio_play));
            });
        } catch (IOException e) {
            Log.e("RecordActivity", "오디오 재생 중 오류 발생: " + e.getMessage());
        }
    }

    // 오디오 재생 중지
    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("RecordActivity", "오디오 재생 중지");
        }
    }

    private String generateUniqueFileName(String dirPath, String baseName, String extension) {
        int fileIndex = 1;
        String newFileName = baseName + extension;
        File file = new File(dirPath, newFileName);

        while (file.exists()) {
            fileIndex++;
            newFileName = baseName + "_" + fileIndex + extension;
            file = new File(dirPath, newFileName);
        }
        return file.getAbsolutePath();
    }

    // 음성 텍스트 변환
    private void convertSpeechToText(String audioFilePath, int position) {
        new Thread(() -> {
            try {
                // 오디오 파일을 바이트 배열로 읽기
                File audioFile = new File(audioFilePath);
                byte[] audioBytes = new byte[(int) audioFile.length()];
                FileInputStream fis = new FileInputStream(audioFile);
                fis.read(audioBytes);
                fis.close();

                // OkHttp 클라이언트를 사용하여 요청 생성
                OkHttpClient client = new OkHttpClient();

                // JSON 요청 본문 생성
                JSONObject audioContent = new JSONObject();
                audioContent.put("content", Base64.encodeToString(audioBytes, Base64.NO_WRAP));

                JSONObject config = new JSONObject();
                config.put("encoding", "AMR");
                config.put("sampleRateHertz", 8000);
                config.put("languageCode", "ko-KR");

                JSONObject requestBodyJson = new JSONObject();
                requestBodyJson.put("audio", audioContent);
                requestBodyJson.put("config", config);

                RequestBody body = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));

                // API 키를 포함한 요청 URL 생성
                String apiKey = "AIzaSyC5owhMxdZnvPz-pTZL6fChTCXj7ld6OI0"; // 발급받은 API 키를 사용하세요.
                String url = "https://speech.googleapis.com/v1/speech:recognize?key=" + apiKey;

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // 요청 보내기
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.d("RecordActivity", "응답: " + responseString);

                    if (responseString.contains("results")) {
                        JSONObject responseJson = new JSONObject(responseString);
                        StringBuilder transcript = new StringBuilder();
                        for (int i = 0; i < responseJson.getJSONArray("results").length(); i++) {
                            JSONObject result = responseJson.getJSONArray("results").getJSONObject(i);
                            transcript.append(result.getJSONArray("alternatives").getJSONObject(0).getString("transcript")).append(" ");
                        }

                        String finalTranscript = transcript.toString();

                        // UI 업데이트
                        runOnUiThread(() -> {
                            // RecyclerView 어댑터에 인식된 텍스트를 추가하고 갱신
                            audioAdapter.setTranscriptText(position, finalTranscript);
                            Log.d("RecordActivity", "인식된 텍스트가 업데이트되었습니다: " + finalTranscript);
                        });
                    } else {
                        Log.e("RecordActivity", "응답에 results 필드가 없습니다: " + responseString);
                    }
                } else {
                    Log.e("RecordActivity", "응답 실패: " + response.message());
                }
            } catch (Exception e) {
                Log.e("RecordActivity", "오류 발생: " + e.getMessage());
            }
        }).start();
    }

    private void requestEmotionScore(String transcript) {
        // GPT API를 통해 감정 점수 요청하는 로직 추가 예정
        // transcript와 '감정상태 기록'에서 사용자가 입력한 데이터를 이용하여 GPT-3.5 Turbo 모델에게 감정 점수를 요청
        // 점수를 받은 후 해당 값을 이용해 EmoGraph를 그리는데 사용할 예정

        // 예시: GPT에게 요청 후 받은 감정 점수 출력
        int emotionScore = 75; // GPT API 응답 예시 값
        Log.d("RecordActivity", "감정 점수: " + emotionScore);

        // 감정 점수를 저장하는 메서드를 호출하여 SharedPreferences에 저장
        saveEmotionScore(emotionScore);
    }

    private void saveEmotionScore(int score) {
        // SharedPreferences에 감정 점수를 저장하는 메서드
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("emotionScore", score);
        editor.apply();
        Log.d("RecordActivity", "감정 점수가 저장되었습니다: " + score);
    }
}

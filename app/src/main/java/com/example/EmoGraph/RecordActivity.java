package com.example.EmoGraph;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    ImageButton audioRecordImageBtn;
    TextView audioRecordText;

    // 오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName;
    private boolean isRecording = false; // 현재 녹음 상태를 확인하기 위함
    private Uri audioUri = null; // 오디오 파일 uri

    // 오디오 파일 목록 관련 변수
    private ArrayList<Uri> audioList;
    private AudioAdapter audioAdapter;

    // 오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);  // 녹음 화면 레이아웃 설정

        audioRecordImageBtn = findViewById(R.id.audioRecordImageBtn);
        audioRecordText = findViewById(R.id.audioRecordText);

        // RecyclerView 설정
        RecyclerView audioRecyclerView = findViewById(R.id.audioRecyclerview);
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        audioRecyclerView.setAdapter(audioAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(layoutManager);

        // 녹음 버튼 클릭 리스너 설정
        audioRecordImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    // 녹음 중일 때 녹음 중지 처리
                    stopRecording();
                    isRecording = false;
                    audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.start_recording)); // 녹음 시작 이미지
                    audioRecordText.setText("녹음 시작");
                    convertSpeechToText(audioFileName); // 녹음이 끝난 후 음성을 텍스트로 변환
                } else {
                    // 녹음 중이 아닐 때 녹음 시작 처리
                    if (checkAudioPermission()) { // 권한이 있는지 확인
                        startRecording();
                        isRecording = true;
                        audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.recording_red)); // 녹음 중 이미지
                        audioRecordText.setText("녹음 중");
                    }
                }
            }
        });

        // RecyclerView의 각 아이템에 대한 클릭 리스너 설정
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position) {
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
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 파일 이름 생성 및 확장자 설정 (.3gp)
        audioFileName = generateUniqueFileName(recordPath, timeStamp + "_오늘의 기분", ".3gp");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  // 마이크로부터 오디오 입력받음
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  // 3GP 형식 설정
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  // AMR_NB 인코더 설정
        mediaRecorder.setOutputFile(audioFileName);  // 녹음 파일 저장 경로

        try {
            mediaRecorder.prepare();  // 준비
            mediaRecorder.start();  // 녹음 시작
            Log.d("RecordActivity", "녹음 시작됨: " + audioFileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("RecordActivity", "녹음 준비 중 오류 발생: " + e.getMessage());
        }
    }

    // 녹음 종료
    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        // 녹음된 파일을 Uri로 변환하여 리스트에 추가
        audioUri = Uri.parse(audioFileName);
        audioList.add(audioUri);

        // 어댑터에 데이터가 변경되었음을 알림
        audioAdapter.notifyDataSetChanged();
        Log.d("RecordActivity", "녹음된 파일 리스트 갱신: " + audioFileName);
    }

    // 오디오 재생 시작
    private void startPlaying(Uri uri, ImageButton playBtn) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d("RecordActivity", "오디오 재생 시작: " + uri.getPath());

            mediaPlayer.setOnCompletionListener(mp -> {
                stopPlaying();
                isPlaying = false;
                playBtn.setImageDrawable(ContextCompat.getDrawable(playBtn.getContext(), R.drawable.audio_play)); // 재생 완료 후 이미지 변경
            });
        } catch (IOException e) {
            e.printStackTrace();
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
            newFileName = baseName + fileIndex + extension;
            file = new File(dirPath, newFileName);
        }

        return file.getAbsolutePath();
    }

    private void convertSpeechToText(String audioFilePath) {
        new Thread(() -> {
            try (SpeechClient speechClient = SpeechClient.create()) {
                FileInputStream fileInputStream = new FileInputStream(audioFilePath);
                ByteString audioBytes = ByteString.readFrom(fileInputStream);

                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(audioBytes)
                        .build();

                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("ko-KR")
                        .build();

                RecognizeResponse response = speechClient.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                StringBuilder transcript = new StringBuilder();
                for (SpeechRecognitionResult result : results) {
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    transcript.append(alternative.getTranscript()).append(" ");
                }

                String finalTranscript = transcript.toString();
                Log.d("RecordActivity", "인식된 텍스트: " + finalTranscript);
                runOnUiThread(() -> {
                    audioRecordText.setText(finalTranscript); // 음성을 텍스트로 변환한 결과를 화면에 표시
                    requestEmotionScore(finalTranscript); // 변환된 텍스트를 이용해 감정 점수를 요청
                });

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("RecordActivity", "음성 인식 중 오류 발생: " + e.getMessage());
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

        // 감정 점수를 SharedPreferences에 저장
        SharedPreferences sharedPreferences = getSharedPreferences("EmoGraphPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("emotionScore", emotionScore);
        editor.apply();
    }

    private void updateEmotionGraph(int score) {
        // 감정 점수를 이용하여 EmoGraph를 업데이트하는 로직 추가 예정
        // 안드로이드 스튜디오에서 그래프를 그리고 사용자에게 시각화된 정보를 제공
        Log.d("RecordActivity", "EmoGraph 업데이트: 감정 점수 " + score);
    }
}
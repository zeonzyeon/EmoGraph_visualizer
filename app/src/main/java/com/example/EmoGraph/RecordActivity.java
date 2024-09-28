package com.example.EmoGraph;

import android.Manifest;
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
import java.util.ArrayList;

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
    private Boolean isPlaying = false;

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

        // 뒤로 가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        audioFileName = generateUniqueFileName(recordPath, timeStamp, ".3gp");

        // 파일 이름 생성 및 확장자
        audioFileName = generateUniqueFileName(recordPath, timeStamp + "_오늘의 기분", ".3gp");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFileName);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
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
        audioList.add(audioUri);  // 녹음된 파일을 리스트에 추가

        // 어댑터에 데이터가 변경되었음을 알림
        audioAdapter.notifyDataSetChanged();
        Log.d("RecordActivity", "녹음된 파일 리스트 갱신: " + audioFileName);
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
}

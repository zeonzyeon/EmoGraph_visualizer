package com.example.EmoGraph;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.core.content.ContextCompat; // ContextCompat 임포트

public class MainActivity extends AppCompatActivity {
    ImageButton audioRecordImageBtn;
    TextView audioRecordText;

    // 오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName; // 오디오 녹음 생성 파일 이름
    private boolean isRecording = false;    // 현재 녹음 상태를 확인하기 위함.
    private Uri audioUri = null; // 오디오 파일 uri

    // 오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    ImageView playIcon;

    /**
     * 리사이클러뷰
     */
    private AudioAdapter audioAdapter;
    private ArrayList<Uri> audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRecord = findViewById(R.id.btn_record);  // 녹음 버튼 찾기
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RecordActivity로 이동하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);  // 녹음 화면으로 이동
            }
        });
    }

    // xml 변수 초기화
    // 리사이클러뷰 생성 및 클릭 이벤트
    private void init() {
        audioRecordImageBtn = findViewById(R.id.audioRecordImageBtn);
        audioRecordText = findViewById(R.id.audioRecordText);

        // 녹음 상태 아이콘 변경
        audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.start_recording));

        audioRecordImageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    // 녹음 중일 때 녹음 중지 처리
                    stopRecording();
                    isRecording = false; // 녹음 상태 변경
                    audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.start_recording)); // 버튼 이미지를 녹음 시작 이미지로 변경
                    audioRecordText.setText("녹음 시작"); // 텍스트 변경
                } else {
                    // 녹음 중이 아닐 때 녹음 시작 처리
                    if (checkAudioPermission()) { // 권한이 있는지 확인
                        startRecording();
                        isRecording = true; // 녹음 상태 변경
                        audioRecordImageBtn.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.recording_red)); // 버튼 이미지를 녹음 중 이미지로 변경
                        audioRecordText.setText("녹음 중"); // 텍스트 변경
                    }
                }
            }
        });

        // 리사이클러뷰
        RecyclerView audioRecyclerView = findViewById(R.id.audioRecyclerview);

        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        audioRecyclerView.setAdapter(audioAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);

        // 커스텀 이벤트 리스너 4. 액티비티에서 커스텀 리스너 객체 생성 및 전달
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String uriName = String.valueOf(audioList.get(position));

                // 음성 녹화 파일에 대한 접근 변수 생성;
                File file = new File(uriName);

                if (isPlaying) {
                    // 음성 녹화 파일이 여러개를 클릭했을 때 재생중인 파일의 Icon을 비활성화(비 재생중)으로 바꾸기 위함.
                    if (playIcon == (ImageView) view) {
                        // 같은 파일을 클릭했을 경우
                        stopAudio();
                    } else {
                        // 다른 음성 파일을 클릭했을 경우
                        // 기존의 재생중인 파일 중지
                        stopAudio();

                        // 새로 파일 재생하기
                        playIcon = (ImageView) view;
                        playIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.hear_recording));
                        playAudio(file);
                    }
                } else {
                    playIcon = (ImageView) view;
                    playAudio(file);
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
        //파일의 외부 경로 확인
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        // 파일 이름 변수를 현재 날짜가 들어가도록 초기화. 그 이유는 중복된 이름으로 기존에 있던 파일이 덮어 쓰여지는 것을 방지하고자 함.
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        /*audioFileName = recordPath + "/" +"RecordExample_" + timeStamp + "_"+"audio.mp4";*/
        audioFileName = generateUniqueFileName(recordPath, timeStamp + "_오늘의 기분", ".3gp");
        //Media Recorder 생성 및 설정
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare(); // 녹음기 준비
            mediaRecorder.start(); // 녹음 시작
            Log.d("MainActivity", "녹음 시작됨: " + audioFileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "녹음 준비 중 오류 발생: " + e.getMessage());
        }
    }

    // 녹음 종료
    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        // 파일 경로(String) 값을 Uri로 변환해서 저장
        //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
        //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
        audioUri = Uri.parse(audioFileName);

        // 데이터 ArrayList에 담기
        audioList.add(audioUri);

        // 데이터 갱신
        audioAdapter.notifyDataSetChanged();
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

    // 녹음 파일 재생
    private void playAudio(File file) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hear_recording));
        isPlaying = true;

        ImageButton playIcon = findViewById(R.id.playBtn_itemAudio); // play_icon은 XML 레이아웃에 정의된 ID입니다.

        if (playIcon != null) {
            // ContextCompat를 사용하여 drawable을 설정합니다.
            playIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hear_recording));
            isPlaying = true;
        } else {
            Log.e("YourActivity", "playIcon is null. Check your XML layout and ensure the ID is correct.");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });
    }

    // 녹음 파일 중지
    private void stopAudio() {
        playIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.audio_play));
        isPlaying = false;
        mediaPlayer.stop();
    }
}
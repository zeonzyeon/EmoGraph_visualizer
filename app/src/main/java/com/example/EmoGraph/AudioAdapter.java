package com.example.EmoGraph;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> {
    // 리사이클러뷰에 넣을 데이터 리스트
    ArrayList<Uri> dataModels;
    Context context;
    ArrayList<String> transcriptList;

    // 리스너 객체 참조를 저장하는 변수
    private OnIconClickListener listener = null;

    // 커스텀 리스너 인터페이스 정의
    public interface OnIconClickListener {
        void onItemClick(View view, int position);
    }

    // 리스너 객체를 전달하는 메서드와 전달된 객체를 저장할 변수 추가
    public void setOnItemClickListener(OnIconClickListener listener) {
        this.listener = listener;
    }

    // 생성자를 통해 데이터 리스트와 context를 받음
    public AudioAdapter(Context context, ArrayList<Uri> dataModels, ArrayList<String> transcriptList) {
        this.dataModels = dataModels;
        this.context = context;
        this.transcriptList = transcriptList;

        // 기존 데이터 모델 크기와 transcriptList 크기가 다르면 일치시키기 위해 빈 항목을 추가
        while (transcriptList.size() < dataModels.size()) {
            transcriptList.add("");
        }
    }

    // 인식된 텍스트 업데이트 메서드
    public void setTranscriptText(int position, String transcript) {
        if (position >= 0 && position < transcriptList.size()) {
            transcriptList.set(position, transcript);
            notifyItemChanged(position);  // 특정 항목만 갱신하여 효율적으로 업데이트
            Log.d("AudioAdapter", "인식된 텍스트가 설정되었습니다: " + transcript + " at position: " + position);
        } else {
            Log.e("AudioAdapter", "잘못된 위치에 접근하려고 합니다: " + position);
        }
    }

    @Override
    public int getItemCount() {
        // 데이터 리스트의 크기를 전달해주어야 함
        return dataModels.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String uriName = String.valueOf(dataModels.get(position));
        File file = new File(uriName);

        // 오디오 파일 제목 설정
        holder.audioTitle.setText(file.getName());

        // 인식된 텍스트가 있는 경우 설정
        String transcript = transcriptList.get(position);
        if (transcript != null && !transcript.isEmpty()) {
            holder.audioTranscript.setText(transcript);
            holder.audioTranscript.setVisibility(View.VISIBLE);
            Log.d("AudioAdapter", "인식된 텍스트 설정: " + transcript + " at position: " + position);
        } else {
            holder.audioTranscript.setVisibility(View.GONE);
            Log.d("AudioAdapter", "인식된 텍스트 없음 at position: " + position);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton audioBtn;
        TextView audioTitle;
        TextView audioTranscript;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            audioBtn = itemView.findViewById(R.id.playBtn_itemAudio);
            audioTitle = itemView.findViewById(R.id.audioTitle_itemAudio);
            audioTranscript = itemView.findViewById(R.id.audioTranscript_itemAudio);

            audioBtn.setOnClickListener(view -> {
                // 아이템 클릭 이벤트 핸들러 메서드에서 리스너 객체 메서드 호출
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (listener != null) {
                        listener.onItemClick(view, pos);
                    }
                }
            });
        }
    }
}


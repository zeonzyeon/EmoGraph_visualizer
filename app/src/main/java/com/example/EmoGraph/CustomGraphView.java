package com.example.EmoGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomGraphView extends View {
    private Paint paint;
    private List<Integer> emotionScores = new ArrayList<>();  // 여러 감정 점수 리스트

    // 생성자
    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);  // 그래프 색상 설정
        paint.setStrokeWidth(8f);
        paint.setStyle(Paint.Style.STROKE);
    }

    // 여러 감정 점수를 설정하는 메서드
    public void setEmotionScores(List<Integer> scores) {
        this.emotionScores = scores;
        invalidate();  // 다시 그리기 요청
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!emotionScores.isEmpty()) {  // 리스트가 비어 있지 않을 때만 그리기
            int width = getWidth();
            int height = getHeight();
            int padding = 20;  // 그래프 여백
            int numScores = emotionScores.size();

            if (numScores == 1) {
                // 점수가 하나일 경우: 점 하나만 표시
                int score = emotionScores.get(0);
                int y = height - (int) ((score / 100.0) * height);  // y값 계산

                // 중간에 점을 표시
                int x = width / 2;  // 중간 위치에 점 그리기
                paint.setStyle(Paint.Style.FILL);  // 원을 채우는 스타일
                canvas.drawCircle(x, y, 10, paint);  // 반지름 10짜리 점 그리기

                // 점수 텍스트 표시
                paint.setTextSize(50);
                canvas.drawText("Emotion Score: " + score, 10, 60, paint);

            } else if (numScores > 1) {
                // 점수가 여러 개일 경우: 선으로 연결
                int stepX = (width - 2 * padding) / (numScores - 1);  // 점수 간의 간격 계산

                // 그래프 그리기
                for (int i = 0; i < numScores - 1; i++) {
                    int score1 = emotionScores.get(i);
                    int score2 = emotionScores.get(i + 1);

                    // 점수를 y축 값으로 변환
                    int y1 = height - (int) ((score1 / 100.0) * height);
                    int y2 = height - (int) ((score2 / 100.0) * height);

                    // 점들 사이를 선으로 연결
                    canvas.drawLine(padding + i * stepX, y1, padding + (i + 1) * stepX, y2, paint);

                    // 각 점을 원으로 표시 (점 스타일로 채우기)
                    canvas.drawCircle(padding + i * stepX, y1, 10, paint);  // 첫 번째 점
                    canvas.drawCircle(padding + (i + 1) * stepX, y2, 10, paint);  // 두 번째 점
                }

                // 마지막 점수 텍스트 표시
                paint.setTextSize(50);
                canvas.drawText("Emotion Score: " + emotionScores.get(numScores - 1), 10, 60, paint);
            }
        } else {
            Log.e("CustomGraphView", "감정 점수가 없습니다.");
        }
    }
}

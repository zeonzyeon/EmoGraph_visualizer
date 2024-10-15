package com.example.EmoGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
        paint.setColor(Color.WHITE);  // 그래프 색상 설정
        paint.setStrokeWidth(8f);
        paint.setStyle(Paint.Style.STROKE);
    }

    // 여러 감정 점수를 설정하는 메서드
    public void setEmotionScores(List<Integer> scores) {
        // 감정 점수가 10개보다 많을 경우, 최근 10개만 저장
        if (scores.size() > 10) {
            this.emotionScores = scores.subList(scores.size() - 10, scores.size());
        } else {
            this.emotionScores = scores;
        }
        invalidate();  // 다시 그리기 요청
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        // 감정 점수의 개수에 따라 가로 크기 계산
//        int numScores = emotionScores.size();
//        int minWidth = 1000;  // 최소 가로 크기 설정
//        int width = Math.max(minWidth, numScores * 200);  // 감정 점수 1개당 200dp 크기로 계산
//
//        // 가로 크기만 설정하고, 높이는 현재 주어진 값 그대로 유지
//        setMeasuredDimension(width, h);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!emotionScores.isEmpty()) {
            int width = getWidth();
            int height = getHeight();
            int padding = 10;
            int numScores = emotionScores.size();

            paint.setTextSize(60);

            if (numScores == 1) {
                // 점수 하나일 경우
                int score = emotionScores.get(0);
                int y = height - (int) ((score / 100.0) * height);
                int x = width / 2;
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(x, y, 5, paint);
            } else if (numScores > 1) {
                int stepX = (width - 2 * padding) / (numScores - 1);

                // x축을 먼저 그리기
//                canvas.drawLine(padding, height - padding, width - padding, height - padding, paint);

                paint.setTextSize(60);

                // 감정 점수 연결
                for (int i = 0; i < numScores - 1; i++) {
                    int score1 = emotionScores.get(i);
                    int score2 = emotionScores.get(i + 1);

                    int y1 = height - (int) ((score1 / 100.0) * height);
                    int y2 = height - (int) ((score2 / 100.0) * height);

                    canvas.drawLine(padding + i * stepX, y1, padding + (i + 1) * stepX, y2, paint);
                    canvas.drawCircle(padding + i * stepX, y1, 5, paint);
                    canvas.drawText(String.valueOf(score1), padding + i * stepX, y1 - 20, paint);
                    canvas.drawCircle(padding + (i + 1) * stepX, y2, 5, paint);
                    canvas.drawText(String.valueOf(score2), padding + (i + 1) * stepX, y2 - 20, paint);
                }
            }
        } else {
            Log.e("CustomGraphView", "감정 점수가 없습니다.");
        }
    }
}

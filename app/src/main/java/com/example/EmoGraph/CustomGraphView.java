package com.example.EmoGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

public class CustomGraphView extends View {

    private List<String> emotionData;
    private List<String> recordingData;
    private Paint paint;

    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomGraphView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5f);
    }

    // 감정 데이터와 녹음 데이터를 설정하는 메서드
    public void setData(List<String> emotionData, List<String> recordingData) {
        this.emotionData = emotionData;
        this.recordingData = recordingData;
        invalidate();  // 뷰를 다시 그리도록 요청
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (emotionData == null || recordingData == null) {
            return;
        }

        // 간단한 그래프 그리기: 감정 데이터를 기반으로 선 그래프를 그리는 예시
        float barWidth = getWidth() / (float) (emotionData.size() + 1);  // 그래프 간격

        for (int i = 0; i < emotionData.size(); i++) {
            // 각 감정 상태의 값을 높이로 반영 (여기서는 데이터가 수치가 아니므로 단순히 위치)
            float barHeight = (i + 1) * 100;
            canvas.drawRect(i * barWidth, getHeight() - barHeight, (i + 1) * barWidth, getHeight(), paint);
        }
    }
}

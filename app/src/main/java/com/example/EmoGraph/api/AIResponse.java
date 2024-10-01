package com.example.EmoGraph.api;

import java.util.List;

public class AIResponse {

    private List<Choice> choices;

    // 선택지를 반환하는 getter
    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    // Choice 클래스 정의
    public static class Choice {
        private String text;  // OpenAI 응답의 'text' 필드

        // text를 반환하는 메서드
        public String getText() {
            return text;  // text 필드를 반환
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

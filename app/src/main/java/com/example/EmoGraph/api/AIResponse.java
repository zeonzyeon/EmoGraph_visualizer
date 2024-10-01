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
        private Message message;

        // message를 반환하는 메서드
        public String getMessage() {
            return message.getContent();  // message 객체의 content를 반환
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }
}

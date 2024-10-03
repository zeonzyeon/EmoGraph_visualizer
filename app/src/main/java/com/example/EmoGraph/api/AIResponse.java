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
        private Message message;  // Message 객체로 설정

        // message를 반환하는 메서드
        public Message getMessage() {  // 반환 타입을 Message로 변경
            return message;  // message 객체 반환
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }
}

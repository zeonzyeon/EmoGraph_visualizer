package com.example.EmoGraph.api;

public class Message {
    private String role;     // 역할 (예: "user", "assistant")
    private String content;  // 메시지 내용

    // 생성자: 역할과 메시지 내용을 받아 초기화
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // 역할을 반환하는 getter 메서드
    public String getRole() {
        return role;
    }

    // 역할을 설정하는 setter 메서드
    public void setRole(String role) {
        this.role = role;
    }

    // 메시지 내용을 반환하는 getter 메서드
    public String getContent() {
        return content;
    }

    // 메시지 내용을 설정하는 setter 메서드
    public void setContent(String content) {
        this.content = content;
    }

    // 역할과 내용을 합친 메시지를 반환하는 메서드
    public String getMessage() {
        return "Role: " + role + "\nContent: " + content;
    }
}

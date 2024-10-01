package com.example.EmoGraph.api;

import java.util.List;

public class AIRequest {
    private String model;
    private String prompt;
    private double temperature;  // 기존에는 int였지만 double로 변경
    private int maxTokens;

    // 생성자
    public AIRequest(String model, String prompt, double temperature, int maxTokens) {
        this.model = model;
        this.prompt = prompt;
        this.temperature = temperature;  // double로 변경
        this.maxTokens = maxTokens;
    }

    // Getters and Setters (필요시)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
}

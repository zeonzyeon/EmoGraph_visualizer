package com.example.EmoGraph.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.openai.com/";

    // Retrofit 인스턴스를 반환하는 메서드
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Gson을 사용하여 JSON 데이터를 파싱
                    .build();
        }
        return retrofit;
    }
}

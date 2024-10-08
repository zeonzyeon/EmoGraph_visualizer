package com.example.EmoGraph.api; // 패키지명은 실제로 생성한 패키지명을 사용하세요.

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApiService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"  // 여기에 API 키를 넣으세요
    })
    @POST("v1/chat/completions")
    Call<AIResponse> getAIMessage(@Body AIRequest request);
}

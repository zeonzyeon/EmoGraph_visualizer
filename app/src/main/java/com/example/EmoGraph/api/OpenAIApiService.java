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

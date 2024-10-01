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
    @POST("v1/completions")
    Call<AIResponse> getAIMessage(@Body AIRequest request);
}

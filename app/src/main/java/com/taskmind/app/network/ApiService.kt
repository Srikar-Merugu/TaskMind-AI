package com.taskmind.app.network

import com.taskmind.app.domain.model.GeminiRequest
import com.taskmind.app.domain.model.GeminiResponse
import com.taskmind.app.domain.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    
    @GET("posts/1")
    suspend fun getSamplePost(): Response<Post>

    @POST("v1beta/models/gemini-flash-latest:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}

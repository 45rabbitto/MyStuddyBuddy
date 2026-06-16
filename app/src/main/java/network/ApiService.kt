package com.studdy.mystudybuddy.network

import androidx.annotation.Keep
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>

    @POST("summarize")
    suspend fun summarizeText(
        @Body request: SummarizeRequest
    ): Response<SummaryResponse>
}

@Keep
data class SummarizeRequest(
    val text: String
)

@Keep
data class SummaryResponse(
    val success: Boolean = true,
    val summary: String = "",
    val original_length: Int = 0,
    val summary_length: Int = 0
)

@Keep
data class HealthResponse(
    val status: String,
    val model_loaded: Boolean,
    val model_type: String
)
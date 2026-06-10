package com.studdy.mystudybuddy.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("summarize")
    suspend fun summarizeText(
        @Body request: SummarizeRequest
    ): Response<SummaryResponse>
}

data class SummarizeRequest(
    val text: String
)

data class SummaryResponse(
    val success: Boolean = true,
    val summary: String = "",
    val original_length: Int = 0,
    val summary_length: Int = 0
)
package com.studdy.mystudybuddy.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface SummaryApiService {

    @POST("/summarize")
    suspend fun summarizeText(@Body request: SummarizeRequest): SummarizeResponse

    @GET("/health")
    suspend fun healthCheck(): HealthResponse
}

data class SummarizeRequest(
    val text: String
)

data class SummarizeResponse(
    val success: Boolean,
    val summary: String,
    val original_length: Int,
    val summary_length: Int,
    val model: String = "MobileBERT ONNX"
)

data class HealthResponse(
    val status: String,
    val model_loaded: Boolean,
    val model_type: String
)

object RetrofitClient {
    // GANTI URL DI BAWAH INI DENGAN URL DARI RAILWAY
    // Pastikan menggunakan https:// dan diakhiri dengan /
    private const val BASE_URL = "https://mystudybuddy-backend-production.up.railway.app/"

    val instance: SummaryApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SummaryApiService::class.java)
    }
}
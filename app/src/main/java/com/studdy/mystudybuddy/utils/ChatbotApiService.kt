package com.studdy.mystudybuddy.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatbotApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // Ganti dengan URL Railway backend Anda
    private val BASE_URL = "https://mystudybuddy-backend.up.railway.app"

    /**
     * Chat dengan DeepSeek API menggunakan ringkasan sebagai konteks
     */
    suspend fun chatWithSummary(question: String, summaryContext: String): String {
        return try {
            val jsonBody = JSONObject().apply {
                put("question", question)
                put("context", summaryContext)
            }.toString()

            val request = Request.Builder()
                .url("$BASE_URL/chat")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBody)
                jsonResponse.getString("answer")
            } else {
                "Maaf, terjadi kesalahan. Error: ${response.code}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
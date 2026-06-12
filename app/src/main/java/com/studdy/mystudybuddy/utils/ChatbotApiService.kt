package com.studdy.mystudybuddy.utils

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatbotApiService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    // 🔥 GEMINI API - PASTIKAN KEY VALID
    private val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent"

    private val apiKey: String by lazy {
        loadTokenFromAssets()
    }

    private fun loadTokenFromAssets(): String {
        return try {
            val inputStream = context.assets.open("chatbot_token.txt")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val token = String(buffer, Charsets.UTF_8).trim()

            android.util.Log.d("CHATBOT_API", "Token loaded, length: ${token.length}")

            if (token.isEmpty() || token == "your_api_key_here") {
                "MISSING_TOKEN"
            } else {
                token
            }
        } catch (e: Exception) {
            android.util.Log.e("CHATBOT_API", "Error: ${e.message}")
            "MISSING_TOKEN"
        }
    }

    suspend fun chatWithSummary(question: String, summaryContext: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey == "MISSING_TOKEN") {
                    return@withContext "❌ API Key tidak ditemukan. Cek file chatbot_token.txt"
                }

                if (summaryContext.isEmpty()) {
                    return@withContext "⚠️ Teks dokumen kosong"
                }

                // 🔥 BATASI TEKS AGAR TIDAK TERLALU PANJANG
                val maxContextLength = 2000
                val trimmedContext = if (summaryContext.length > maxContextLength) {
                    summaryContext.take(maxContextLength) + "..."
                } else {
                    summaryContext
                }

                val prompt = """
                    Anda adalah asisten belajar AI yang ramah dan membantu bernama "My Study Buddy".
                    
                    Jawablah pertanyaan berdasarkan MATERI berikut:
                    
                    MATERI:
                    $trimmedContext
                    
                    PERTANYAAN: $question
                    
                    ATURAN:
                    1. Jawab berdasarkan materi di atas
                    2. Gunakan bahasa Indonesia yang baik
                    3. Jawab singkat dan jelas (maksimal 100 kata)
                    4. Jika pertanyaan tidak relevan, bilang "Maaf, itu di luar materi"
                """.trimIndent()

                val requestBody = mapOf(
                    "contents" to listOf(
                        mapOf(
                            "parts" to listOf(
                                mapOf("text" to prompt)
                            )
                        )
                    ),
                    "generationConfig" to mapOf(
                        "maxOutputTokens" to 300,
                        "temperature" to 0.7
                    )
                )

                val jsonBody = gson.toJson(requestBody)

                val request = Request.Builder()
                    .url("$BASE_URL?key=$apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                android.util.Log.d("CHATBOT_API", "Response code: ${response.code}")
                android.util.Log.d("CHATBOT_API", "Response: ${responseBody.take(300)}")

                if (response.isSuccessful) {
                    val jsonResponse = gson.fromJson(responseBody, GeminiResponse::class.java)
                    val answer = jsonResponse.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text ?: "Maaf, tidak ada jawaban."
                    return@withContext answer.trim()
                } else {
                    return@withContext when (response.code) {
                        400 -> "❌ Request error. Coba pertanyaan yang lebih singkat."
                        401 -> "❌ API Key tidak valid. Cek token di chatbot_token.txt"
                        429 -> "❌ Rate limit. Coba lagi dalam 10-30 detik (Gemini free: 60 request/menit)"
                        503 -> "❌ Server sibuk. Coba lagi nanti."
                        else -> "❌ Error ${response.code}"
                    }
                }

            } catch (e: IOException) {
                android.util.Log.e("CHATBOT_API", "IO Error: ${e.message}")
                return@withContext "❌ Gagal terhubung ke server. Periksa internet."
            } catch (e: Exception) {
                android.util.Log.e("CHATBOT_API", "Error: ${e.message}", e)
                return@withContext "❌ Error: ${e.message}"
            }
        }
    }
}

// Response model untuk Gemini
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>? = null
)

data class GeminiPart(
    val text: String? = null
)
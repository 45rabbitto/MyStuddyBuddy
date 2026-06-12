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

    // 🔥 OPENROUTER API - FREE TIER
    private val BASE_URL = "https://openrouter.ai/api/v1/chat/completions"
    private val MODEL_NAME = "openrouter/free"  // 🔥 ROUTER OTOMATIS KE MODEL FREE TERBAIK

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
            if (token.isEmpty() || token == "your_openrouter_api_key_here") {
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
                    return@withContext "❌ API Key tidak ditemukan. Cek file chatbot_token.txt\n\nDapatkan key di: https://openrouter.ai/keys"
                }

                if (summaryContext.isEmpty()) {
                    return@withContext "⚠️ Teks dokumen kosong. Silakan upload PDF terlebih dahulu."
                }

                val maxContextLength = 3000
                val trimmedContext = if (summaryContext.length > maxContextLength) {
                    summaryContext.take(maxContextLength) + "..."
                } else {
                    summaryContext
                }

                android.util.Log.d("CHATBOT_API", "Model: $MODEL_NAME")
                android.util.Log.d("CHATBOT_API", "Context length: ${trimmedContext.length}")

                val systemPrompt = """
                    Anda adalah asisten belajar AI yang ramah dan membantu bernama "My Study Buddy".
                    
                    Jawablah berdasarkan MATERI berikut:
                    
                    MATERI:
                    $trimmedContext
                    
                    ATURAN:
                    1. Jawab berdasarkan materi di atas
                    2. Gunakan bahasa Indonesia yang baik
                    3. Jawab singkat dan jelas (maksimal 2 paragraf)
                    4. Jika pertanyaan tidak relevan, bilang "Maaf, itu di luar materi yang dipelajari"
                """.trimIndent()

                val requestBody = mapOf(
                    "model" to MODEL_NAME,
                    "messages" to listOf(
                        mapOf("role" to "system", "content" to systemPrompt),
                        mapOf("role" to "user", "content" to question)
                    ),
                    "max_tokens" to 500,
                    "temperature" to 0.7
                )

                val jsonBody = gson.toJson(requestBody)

                val request = Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                android.util.Log.d("CHATBOT_API", "Response code: ${response.code}")

                if (response.isSuccessful) {
                    val jsonResponse = gson.fromJson(responseBody, OpenRouterResponse::class.java)
                    val answer = jsonResponse.choices?.firstOrNull()?.message?.content ?: "Maaf, tidak ada jawaban."
                    return@withContext answer.trim()
                } else {
                    return@withContext when (response.code) {
                        401 -> "❌ API Key tidak valid. Cek token di chatbot_token.txt"
                        402 -> "❌ Credit limit habis. Bisa top up minimal 10 credits (sekali saja)"
                        429 -> "❌ Rate limit. Coba lagi nanti (free tier: ~20 request/menit)"
                        else -> "❌ Error ${response.code}: ${responseBody.take(200)}"
                    }
                }

            } catch (e: IOException) {
                android.util.Log.e("CHATBOT_API", "IO Error: ${e.message}")
                return@withContext "❌ Gagal terhubung ke server. Periksa koneksi internet."
            } catch (e: Exception) {
                android.util.Log.e("CHATBOT_API", "Error: ${e.message}", e)
                return@withContext "❌ Error: ${e.message}"
            }
        }
    }
}

data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>? = null
)

data class OpenRouterChoice(
    val message: OpenRouterMessage? = null
)

data class OpenRouterMessage(
    val content: String? = null
)
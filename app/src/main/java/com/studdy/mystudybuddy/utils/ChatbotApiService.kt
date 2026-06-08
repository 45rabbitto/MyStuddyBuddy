package com.studdy.mystudybuddy.utils

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.studdy.mystudybuddy.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatbotApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private val API_KEY = BuildConfig.OPENAI_API_KEY

    private val BASE_URL = "https://api.openai.com/v1"
    private val MODEL = "gpt-4o-mini"

    suspend fun chatWithSummary(question: String, summaryContext: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val systemPrompt = """
                    Anda adalah asisten belajar AI bernama "My Study Buddy".
                    
                    MATERI RINGKASAN:
                    $summaryContext
                    
                    ATURAN:
                    1. Jawab berdasarkan MATERI DI ATAS
                    2. Gunakan bahasa Indonesia
                    3. Jawab singkat, jelas, dan edukatif
                    4. Jika pertanyaan di luar materi, katakan "Maaf, itu di luar materi yang sedang dipelajari"
                """.trimIndent()

                val requestBody = mapOf(
                    "model" to MODEL,
                    "messages" to listOf(
                        mapOf("role" to "system", "content" to systemPrompt),
                        mapOf("role" to "user", "content" to question)
                    ),
                    "max_tokens" to 500,
                    "temperature" to 0.7
                )

                val jsonBody = gson.toJson(requestBody)

                val request = Request.Builder()
                    .url("$BASE_URL/chat/completions")
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = gson.fromJson(responseBody, OpenAiResponse::class.java)
                    return@withContext jsonResponse.choices?.firstOrNull()?.message?.content?.trim() ?: "Maaf, tidak ada jawaban."
                } else {
                    val errorMsg = when (response.code) {
                        401 -> "❌ API Key tidak valid"
                        429 -> "⏰ Terlalu banyak permintaan, coba lagi nanti"
                        402 -> "💰 Kredit OpenAI habis"
                        else -> "❌ Error ${response.code}"
                    }
                    return@withContext errorMsg
                }
            } catch (e: IOException) {
                return@withContext "❌ Gagal terhubung ke server"
            } catch (e: Exception) {
                return@withContext "❌ Error: ${e.message}"
            }
        }
    }

    suspend fun checkConnection(): String {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://api.openai.com/v1/models")
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) "✅ OpenAI Connected" else "⚠️ Connection Failed"
            } catch (e: Exception) {
                return@withContext "❌ Error: ${e.message}"
            }
        }
    }
}

data class OpenAiResponse(
    val choices: List<Choice>? = null
)

data class Choice(
    val message: Message? = null
)

data class Message(
    val content: String? = null
)
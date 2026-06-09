package com.studdy.mystudybuddy.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileReader
import java.io.IOException
import java.util.Properties
import java.util.concurrent.TimeUnit

class ChatbotApiService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private val apiKey: String by lazy {
        loadApiKeyFromLocalProperties()
    }

    private val BASE_URL = "https://api.openai.com/v1"
    private val MODEL = "gpt-4o-mini"

    private fun loadApiKeyFromLocalProperties(): String {
        return try {
            val properties = Properties()
            val file = FileReader("local.properties")
            properties.load(file)
            file.close()
            val key = properties.getProperty("OPENAI_API_KEY")
            if (key.isNullOrBlank()) "MISSING_API_KEY" else key
        } catch (e: Exception) {
            "MISSING_API_KEY"
        }
    }

    suspend fun chatWithSummary(question: String, summaryContext: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey == "MISSING_API_KEY") {
                    return@withContext "❌ API Key tidak ditemukan. Silakan tambahkan OPENAI_API_KEY di file local.properties"
                }

                val systemPrompt = """
                    Anda adalah asisten belajar AI yang ramah dan membantu bernama "Study Buddy".
                    
                    MATERI RINGKASAN:
                    ---
                    $summaryContext
                    ---
                    
                    ATURAN:
                    1. Jawab berdasarkan MATERI DI ATAS
                    2. Gunakan bahasa Indonesia yang baik
                    3. Jawab singkat dan jelas
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
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = gson.fromJson(responseBody, OpenAiResponse::class.java)
                    val answer = jsonResponse.choices?.firstOrNull()?.message?.content ?: "Maaf, tidak ada jawaban."
                    return@withContext answer.replace(Regex("```\\w*"), "").trim()
                } else {
                    val errorMsg = when (response.code) {
                        401 -> "❌ API Key tidak valid"
                        429 -> "❌ Terlalu banyak request, coba lagi nanti"
                        402 -> "❌ Kredit habis, daftar akun baru"
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
                if (apiKey == "MISSING_API_KEY") return@withContext "❌ API Key tidak ditemukan"
                val request = Request.Builder()
                    .url("$BASE_URL/models")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) "✅ Koneksi berhasil" else "⚠️ Gagal koneksi"
            } catch (e: Exception) {
                "❌ Error: ${e.message}"
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
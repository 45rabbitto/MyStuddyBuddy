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

    // ========== GITHUB MODELS CONFIGURATION ==========
    private val BASE_URL = "https://models.github.ai/inference"

    // Model name dengan prefix "openai/" untuk GPT-4o-mini [citation:6]
    private val MODEL_NAME = "openai/gpt-4o-mini"

    // Ambil GitHub Token dari local.properties
    private val githubToken: String by lazy {
        loadTokenFromLocalProperties()
    }

    private fun loadTokenFromLocalProperties(): String {
        return try {
            val properties = Properties()
            val file = FileReader("local.properties")
            properties.load(file)
            file.close()

            // Coba ambil GITHUB_TOKEN
            val token = properties.getProperty("GITHUB_TOKEN")
            if (token.isNullOrBlank()) {
                "MISSING_TOKEN"
            } else {
                token
            }
        } catch (e: Exception) {
            "MISSING_TOKEN"
        }
    }

    /**
     * Chat dengan GitHub Models (GPT-4o-mini FREE!)
     */
    suspend fun chatWithSummary(question: String, summaryContext: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Validasi token
                if (githubToken == "MISSING_TOKEN") {
                    return@withContext """❌ GitHub Token tidak ditemukan!"""
                }

                if (summaryContext.isEmpty()) {
                    return@withContext "⚠️ Ringkasan materi kosong. Silakan upload PDF terlebih dahulu."
                }

                // System prompt dengan konteks ringkasan
                val systemPrompt = """
                    Anda adalah asisten belajar AI yang ramah dan membantu bernama "Study Buddy".
                    
                    TUGAS ANDA:
                    Bantu user memahami materi yang sedang mereka pelajari. 
                    Jawablah berdasarkan MATERI yang diberikan di bawah ini.
                    
                    MATERI RINGKASAN:
                    ---
                    $summaryContext
                    ---
                    
                    ATURAN PENTING:
                    1. Jawab berdasarkan MATERI DI ATAS. Jangan berasumsi atau menambah informasi di luar materi!
                    2. Jika pertanyaan tidak relevan dengan materi, katakan: "Maaf, pertanyaan Anda tidak relevan dengan materi yang sedang dipelajari."
                    3. Gunakan bahasa Indonesia yang baik, jelas, dan mudah dipahami.
                    4. Berikan jawaban yang edukatif dan membantu pemahaman user.
                    5. Jawaban singkat, padat, dan langsung ke poin (maksimal 3-4 paragraf).
                """.trimIndent()

                // Request body untuk GitHub Models API [citation:2][citation:6]
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
                    .addHeader("Authorization", "Bearer $githubToken")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonResponse = gson.fromJson(responseBody, GitHubModelsResponse::class.java)
                    val answer = jsonResponse.choices?.firstOrNull()?.message?.content ?: "Maaf, tidak ada jawaban."

                    return@withContext answer.trim()

                } else {
                    // Handle error response
                    val errorMsg = when (response.code) {
                        401 -> "❌ Token tidak valid. Periksa kembali GITHUB_TOKEN di local.properties"
                        403 -> "❌ Token tidak memiliki permission. Pastikan Models permission sudah Read-only"
                        429 -> "❌ Rate limit tercapai. Coba lagi nanti (gratis, limited per hari) [citation:10]"
                        404 -> "❌ Model tidak ditemukan. Cek nama model: $MODEL_NAME"
                        else -> "❌ Error ${response.code}: $responseBody"
                    }
                    return@withContext errorMsg
                }

            } catch (e: IOException) {
                return@withContext "❌ Gagal terhubung ke server. Periksa koneksi internet Anda."
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "❌ Error: ${e.message}"
            }
        }
    }

    /**
     * Cek koneksi ke GitHub Models
     */
    suspend fun checkConnection(): String {
        return withContext(Dispatchers.IO) {
            try {
                if (githubToken == "MISSING_TOKEN") {
                    return@withContext "❌ Token tidak ditemukan"
                }

                val request = Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Authorization", "Bearer $githubToken")
                    .addHeader("Content-Type", "application/json")
                    .post("""{"model":"$MODEL_NAME","messages":[{"role":"user","content":"Hi"}]}""".toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    "✅ Koneksi ke GitHub Models berhasil! Menggunakan $MODEL_NAME"
                } else {
                    "⚠️ Gagal koneksi: ${response.code}"
                }
            } catch (e: Exception) {
                "❌ Error: ${e.message}"
            }
        }
    }
}

// ========== RESPONSE MODEL UNTUK GITHUB MODELS API ==========
data class GitHubModelsResponse(
    val choices: List<GitHubChoice>? = null,
    val usage: GitHubUsage? = null
)

data class GitHubChoice(
    val message: GitHubMessage? = null
)

data class GitHubMessage(
    val content: String? = null,
    val role: String? = null
)

data class GitHubUsage(
    val prompt_tokens: Int? = null,
    val completion_tokens: Int? = null,
    val total_tokens: Int? = null
)
package com.studdy.mystudybuddy.presentation.screens.ringkasan

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object OpenAiHelper {

    private const val API_KEY = ""
    private const val BASE_URL = "https://api.openai.com/v1/chat/completions"
    private const val MODEL = "gpt-3.5-turbo" // bisa ganti ke gpt-4 jika mau

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // =========================
    // BUAT RINGKASAN DARI TEKS PDF
    // =========================
    fun generateRingkasan(
        pdfText: String,
        onSuccess: (ringkasan: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val prompt = """
            Buatkan ringkasan yang jelas dan mudah dipahami dalam Bahasa Indonesia 
            dari materi berikut ini. Ringkasan harus mencakup poin-poin penting 
            dan terstruktur dengan baik:
            
            ${pdfText.take(4000)}
            
            Format ringkasan dengan poin-poin menggunakan tanda •
        """.trimIndent()

        callOpenAI(prompt, onSuccess, onError)
    }

    // =========================
    // GENERATE SOAL DARI RINGKASAN
    // =========================
    fun generateSoal(
        ringkasan: String,
        jumlahSoal: Int,
        onSuccess: (soalJson: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val prompt = """
            Buatkan $jumlahSoal soal pilihan ganda dalam Bahasa Indonesia 
            berdasarkan ringkasan materi berikut:
            
            $ringkasan
            
            PENTING: Respons HANYA berupa JSON array, tanpa teks lain, tanpa markdown.
            Format JSON:
            [
              {
                "question": "Pertanyaan di sini?",
                "options": ["Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"],
                "correctAnswer": 0
              }
            ]
            
            correctAnswer adalah index (0-3) dari jawaban yang benar.
            Pastikan soal bervariasi dan sesuai dengan materi.
        """.trimIndent()

        callOpenAI(prompt, onSuccess, onError)
    }

    // =========================
    // BASE CALL KE OPENAI
    // =========================
    private fun callOpenAI(
        prompt: String,
        onSuccess: (result: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val requestBody = JSONObject().apply {
            put("model", MODEL)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 2000)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL)
            .header("Authorization", "Bearer $API_KEY")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "Koneksi gagal")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    val json = JSONObject(body ?: "")

                    // Cek apakah ada error dari OpenAI
                    if (json.has("error")) {
                        val errorMsg = json.getJSONObject("error").getString("message")
                        onError(errorMsg)
                        return
                    }

                    val content = json
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    onSuccess(content)

                } catch (e: Exception) {
                    onError("Gagal memproses respons: ${e.message}")
                }
            }
        })
    }
}